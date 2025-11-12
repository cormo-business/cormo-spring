package hello.squadfit.fcm;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FcmService {

    private final DeviceTokenRepository repo;

    public FcmService(DeviceTokenRepository repo) {
        this.repo = repo;
    }

    private AndroidConfig androidHighWithChannel() {
        return AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH) // 우선순위 높이기
                .setTtl(3600 * 1000)
                .setNotification(AndroidNotification.builder()
                        .setChannelId("default") // Android 채널 ID (앱에서 만든 것과 동일)
                        .setSound("default") // 기본 사운드
                        .setClickAction("OPEN_MAIN") // 클릭 시 인텐트 액션 매칭
                        .build())
                .build();
    }

    /** 단건 전송 */
    public String sendToToken(String token, String title, String body, Map<String, String> data) throws Exception {
        Message msg = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .putAllData(safeData(data))
                .setAndroidConfig(androidHighWithChannel())
                .build();
        return FirebaseMessaging.getInstance().send(msg);
    }

    /** 여러 토큰 전송: send() 루프 (배치 엔드포인트 미사용) */
    public SendResult sendToTokens(List<String> tokens, String title, String body, Map<String, String> data) {
        if (tokens == null || tokens.isEmpty()) return SendResult.empty();

        List<String> cleaned = tokens.stream()
                .filter(t -> t != null && !t.isBlank())
                .distinct()
                .collect(Collectors.toList());

        List<String> okIds = new ArrayList<>();
        Map<String, String> failures = new LinkedHashMap<>();
        List<String> toDeactivate = new ArrayList<>();

        Map<String, String> payload = safeData(data);
        Notification noti = Notification.builder().setTitle(title).setBody(body).build();

        for (String token : cleaned) {
            Message msg = Message.builder()
                    .setToken(token)
                    .setNotification(noti)
                    .putAllData(payload)
                    .setAndroidConfig(androidHighWithChannel())
                    .build();
            try {
                String id = FirebaseMessaging.getInstance().send(msg);
                okIds.add(id);
            } catch (FirebaseMessagingException e) {
                failures.put(token, e.getMessage());
                MessagingErrorCode code = e.getMessagingErrorCode();
                if (code == MessagingErrorCode.UNREGISTERED
                        || code == MessagingErrorCode.INVALID_ARGUMENT
                        || code == MessagingErrorCode.SENDER_ID_MISMATCH) {
                    toDeactivate.add(token);
                }
            } catch (Exception e) {
                failures.put(token, e.getMessage());
            }
        }

        // 무효 토큰 비활성화(선택)
        if (!toDeactivate.isEmpty()) {
            List<DeviceToken> dead = repo.findAllByTokenIn(toDeactivate);
            dead.forEach(dt -> dt.setActive(false));
            repo.saveAll(dead);
        }

        return new SendResult(okIds.size(), failures.size(), okIds, failures);
    }

    /** 토픽 전송 (단건 send) */
    public String sendToTopic(String topic, String title, String body, Map<String, String> data) throws Exception {
        Message msg = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .putAllData(safeData(data))
                .setAndroidConfig(androidHighWithChannel())
                .build();
        return FirebaseMessaging.getInstance().send(msg);
    }

    /** 조건식 전송 (단건 send) */
    public String sendByCondition(String condition, String title, String body, Map<String, String> data) throws Exception {
        Message msg = Message.builder()
                .setCondition(condition)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .putAllData(safeData(data))
                .setAndroidConfig(androidHighWithChannel())
                .build();
        return FirebaseMessaging.getInstance().send(msg);
    }

    private Map<String, String> safeData(Map<String, String> data) {
        return (data == null) ? Map.of() : data;
    }

    /** 결과 요약 DTO */
    public static class SendResult {
        public final int successCount;
        public final int failureCount;
        public final List<String> messageIds;
        public final Map<String, String> failures;

        public SendResult(int successCount, int failureCount, List<String> messageIds, Map<String, String> failures) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.messageIds = messageIds;
            this.failures = failures;
        }

        public static SendResult empty() {
            return new SendResult(0, 0, List.of(), Map.of());
        }
    }
}
