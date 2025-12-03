package hello.squadfit.domain.certification.service;

import hello.squadfit.domain.certification.dto.SmsMessage;
import hello.squadfit.domain.certification.dto.SmsRequestDto;
import hello.squadfit.domain.certification.repository.SmsRepository;
import hello.squadfit.domain.certification.dto.SmsResponseDto;
import hello.squadfit.domain.certification.request.SmsVerifyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SmsService {

    @Qualifier("sensWebClient")
    private final WebClient sensWebClient;

    @Value("${NAVER_ACCESS_KEY}")
    private String accessKey;
    @Value("${NAVER_SECRET_KEY}")
    private String secretKey;
    @Value("${NAVER_SMS_ID}")
    private String serviceId;
    @Value("${NAVER_SMS_PHONE_NUMBER}")
    private String senderNumber;

    private final SmsRepository smsRepository;

    /**
     * 외부에서 호출하는 메서드: 인증번호 발송
     */
    @Transactional
    public void sendVerificationCode(String phoneNumber) {

        // 1) 6자리 랜덤 코드 생성
        String code = createRandomCode();

        // 2) 메시지 내용
        String content = "[늘액션] 인증번호 [" + code + "] 를 입력해주세요.";

        // 3) 실제 SMS 발송
        SmsResponseDto smsResponseDto = sendSms(phoneNumber, content);
        log.info("smsResponseDto requestId = {}", smsResponseDto.requestId());


        // 4) Redis에 저장, 검증 시 꺼내서 비교
        smsRepository.save(phoneNumber, code, Duration.ofMinutes(10));

    }


    /**
     * 실제 SMS API 호출
     */
    public SmsResponseDto sendSms(String phoneNumber, String content){
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uriPath = "/sms/v2/services/" + serviceId + "/messages";

        String signature = makeSignature(timestamp);

        SmsMessage message = SmsMessage.builder()
                .to(phoneNumber)
                .content(content)
                .build();

        SmsRequestDto requestDto = SmsRequestDto.of(
                senderNumber,
                content,
                List.of(message)
        );

        return sensWebClient.post()
                .uri(uriPath)
                .header("x-ncp-apigw-timestamp", timestamp)
                .header("x-ncp-iam-access-key", accessKey)
                .header("x-ncp-apigw-signature-v2", signature)
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res ->
                        res.bodyToMono(String.class).flatMap(body -> {
                            log.error("SENS 4xx ERROR: status={}, body={}", res.statusCode(), body);
                            return Mono.error(new RuntimeException("SENS 4xx: " + body));
                        })
                )
                .onStatus(HttpStatusCode::is5xxServerError, res ->
                        res.bodyToMono(String.class).flatMap(body -> {
                            log.error("SENS 5xx ERROR: status={}, body={}", res.statusCode(), body);
                            return Mono.error(new RuntimeException("SENS 5xx: " + body));
                        })
                )
                .bodyToMono(SmsResponseDto.class)
                .block();
    }

    /**
     * 서명 생성
     */
    public String makeSignature(String timestamp) {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + this.serviceId + "/messages";
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;
        String message = method +
                space +
                url +
                newLine +
                timestamp +
                newLine +
                accessKey;

        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            String encodeBase64String = Base64.encodeBase64String(rawHmac);

            return encodeBase64String;
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    /**
     * 인증번호 검증
     */
    public Boolean verifyCode(SmsVerifyRequest request) {

        String savedCode = smsRepository.find(request.getPhoneNumber());

        boolean isMatch = savedCode.equals(request.getCode());

        if(isMatch){
            // 한번 인증 후 삭제하기
            smsRepository.delete(request.getPhoneNumber());

        }
        return isMatch;
    }


    /**
     * 4자리 인증번호 생성
     */
    private String createRandomCode() {
        Random random = new Random();
        int code = random.nextInt(9000) + 1000; // 1000 ~ 9999
        return String.valueOf(code);
    }


}
