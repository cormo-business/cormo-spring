package hello.squadfit.fcm;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PushScheduler {

    private final DeviceTokenRepository repo;
    private final FcmService fcm;

    public PushScheduler(DeviceTokenRepository repo, FcmService fcm) {
        this.repo = repo;
        this.fcm = fcm;
    }

    @Scheduled(cron = "0 30 12 * * *", zone = "Asia/Seoul")
    public void at1230() { send("점심 알림", "12:30 알림입니다."); }

    @Scheduled(cron = "0 30 17 * * *", zone = "Asia/Seoul")
    public void at1730() { send("퇴근 전 알림", "17:30 알림입니다."); }

    private void send(String title, String body) {
        List<String> tokens = repo.findByActiveTrue().stream().map(deviceToken -> deviceToken.getToken()).distinct().toList();
        for (int i = 0; i < tokens.size(); i += 500) { // 최대 500개씩 배치로 보냄
            fcm.sendBatch(tokens.subList(i, Math.min(tokens.size(), i + 500)), title, body);
        }
    }
}