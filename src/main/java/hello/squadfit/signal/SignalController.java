package hello.squadfit.signal;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class SignalController {

    // ─────────────────────────────────────────────────────────────────────────
    // Spring은 "시그널링 서버" 역할만 수행합니다.
    //  - /turn/credentials: 클라이언트(Flutter)에게 TURN/STUN 접속용 임시 계정 발급
    //  - /signal/offer: Flutter가 만든 Offer SDP를 백엔드(C++ Ingress)의 /offer로 프록시
    //  - /signal/candidate: Flutter ICE candidate를 C++ Ingress의 /candidate로 프록시
    //
    // ※ 실제 미디어(영상/오디오 RTP)는 Spring을 통과하지 않고
    //    Flutter ↔ C++ Ingress 사이의 WebRTC로 직접 흐릅니다.
    // ─────────────────────────────────────────────────────────────────────────

    // 환경변수 (기본값 포함)
    private final String AUTH_SECRET =
            System.getenv().getOrDefault("TURN_AUTH_SECRET", "devsecret");
    private final String TURN_HOST =
            System.getenv().getOrDefault("TURN_HOST", "localhost");

    // ⚠️ 여기 URL만 C++ Ingress의 /offer 엔드포인트로 맞춰주면 됩니다.
    //    (이전에는 Python(aiortc)였던 자리를 C++ 서버가 동일 API로 대체)
    private final String AI_OFFER_URL =
            System.getenv().getOrDefault("AI_OFFER_URL", "http://localhost:7000/offer");

    // /candidate URL은 /offer 기준으로 치환. (원하면 별도 ENV로 분리 가능)
    private final String AI_CANDIDATE_URL = AI_OFFER_URL.replace("/offer", "/candidate");

    @Autowired
    private RestTemplate http; // 커넥션 풀 적용된 RestTemplate(타임아웃/재사용은 Bean에서 설정)

    /**
     * TURN/STUN 크레덴셜 발급
     * - WebRTC에서 P2P 경로를 찾기 위해 STUN/TURN 서버를 사용함
     * - 여기서는 coturn "REST API 방식"을 따라, 유효기간 기반 username과 HMAC-SHA1 credential을 발급
     * - 클라이언트는 이 값을 RTCPeerConnection(iceServers)에 넣어 ICE 수집 시 사용
     */
    @GetMapping("/turn/credentials")
    public Map<String, Object> creds(@RequestParam(name = "ttl", defaultValue = "3600") long ttl) {

        long expiry = (System.currentTimeMillis()/1000L) + ttl; // seconds(만료 시각)
        String username = String.valueOf(expiry);               // 만료 에폭을 username으로
        String credential = hmacSha1Base64(AUTH_SECRET, username); // coturn secret로 서명

        // 기본 STUN: 공개 STUN 사용(선택)
        Map<String,Object> stun = Map.of("urls", List.of("stun:stun.l.google.com:19302"));

        // TURN: 우리 coturn 서버(udp/tcp)
        Map<String,Object> turn = new HashMap<>();
        turn.put("urls", List.of(
                "turn:" + TURN_HOST + ":3478?transport=udp",
                "turn:" + TURN_HOST + ":3478?transport=tcp"
        ));
        turn.put("username", username);
        turn.put("credential", credential);

        // Flutter가 그대로 RTCPeerConnection에 넣을 형태(iceServers)로 반환
        return Map.of(
                "username", username,
                "credential", credential,
                "ttl", ttl,
                "iceServers", List.of(stun, turn)
        );
    }

    /**
     * Offer 프록시
     * [무슨 일?]
     * 1) Flutter가 createOffer()로 만든 Offer SDP를 전송하면,
     * 2) Spring은 그 바디(Map<String,Object>)를 그대로 C++ Ingress의 /offer로 전달
     * 3) C++ Ingress는 Answer SDP(+ sid)를 생성해 응답
     * 4) Spring은 받은 응답을 그대로 Flutter에 반환
     *
     * 요약: "코덱/미디어 방식 협상(SDP 교환)"을 중계할 뿐, 미디어 데이터는 만지지 않음.
     */
    @PostMapping("/signal/offer")
    public ResponseEntity<?> offer(@RequestBody Map<String,Object> body) {
        long t0 = System.currentTimeMillis();
        try {
            ResponseEntity<Map> resp = http.postForEntity(AI_OFFER_URL, body, Map.class);
            long took = System.currentTimeMillis() - t0;
            System.out.println("[SIGNAL] /offer roundtrip " + took + " ms");

            // 기대 응답(예): {"sdp":"<answer>", "type":"answer", "sid":"<uuid>"}
            return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());

        } catch (HttpStatusCodeException e) {
            String raw = e.getResponseBodyAsString();
            return ResponseEntity.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(raw);

        } catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of(
                            "error", "AI server unreachable",
                            "detail", e.getMessage(),
                            "aiOfferUrl", AI_OFFER_URL
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Signal proxy failed",
                            "detail", e.getMessage()
                    ));
        }
    }

    /**
     * Candidate 프록시 (Trickle ICE)
     * [무슨 일?]
     * 1) Flutter가 ICE 후보(내가 시도할 수 있는 IP:Port 경로)를 발견할 때마다
     *    { sid, candidate: {candidate,sdpMid,sdpMLineIndex}, endOfCandidates } 형식으로 POST
     * 2) Spring은 그대로 C++ Ingress의 /candidate로 전달
     * 3) Ingress는 해당 sid의 PeerConnection에 candidate를 추가
     *
     * 요약: "네트워크 경로 후보(ICE candidate)를 지속적으로 흘려보내는" 트리클링을 중계
     */
    @PostMapping("/signal/candidate")
    public ResponseEntity<?> candidate(@RequestBody Map<String,Object> body) {
        long t0 = System.currentTimeMillis();
        try {
            // C++ Ingress의 /candidate로 그대로 POST
            ResponseEntity<Map> resp = http.postForEntity(AI_CANDIDATE_URL, body, Map.class);
            long took = System.currentTimeMillis() - t0;
            System.out.println("[SIGNAL] /candidate roundtrip " + took + " ms");

            // 기대 응답(예): {"ok": true}
            return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());

        } catch (HttpStatusCodeException e) {
            String raw = e.getResponseBodyAsString();
            return ResponseEntity.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(raw);

        } catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of(
                            "error", "AI server unreachable",
                            "detail", e.getMessage(),
                            "aiCandidateUrl", AI_CANDIDATE_URL
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Candidate proxy failed",
                            "detail", e.getMessage()
                    ));
        }
    }

    // coturn REST auth: username(만료시각)을 HMAC-SHA1로 서명 → base64
    private static String hmacSha1Base64(String key, String msg) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
            byte[] raw = mac.doFinal(msg.getBytes());
            return Base64.getEncoder().encodeToString(raw);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
