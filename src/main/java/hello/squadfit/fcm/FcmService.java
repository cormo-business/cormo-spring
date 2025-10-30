package hello.squadfit.fcm;
import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FcmService {

    public void sendToToken(String token, String title, String body) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setChannelId("high_importance") // 앱에서 만든 채널 ID
                                .build())
                        .build())
                .putData("deeplink", "app://open/today") // 선택
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendBatch(List<String> tokens, String title, String body) {
        if (tokens.isEmpty()) return;
        MulticastMessage msg = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder().setChannelId("high_importance").build())
                        .build())
                .build();
        try {
            BatchResponse resp = FirebaseMessaging.getInstance().sendEachForMulticast(msg);
            // 실패 토큰 정리 필요: resp.getResponses() 순회하여 UNREGISTERED 처리
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToTopic(String topic, String title, String body) {
        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
