package hello.squadfit.fcm;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotifyScheduler {

    private final FcmService fcm;
    private final DeviceTokenRepository repo;

    public NotifyScheduler(FcmService fcm, DeviceTokenRepository repo) {
        this.fcm = fcm;
        this.repo = repo;
    }

    // 매일 11:30 (Asia/Seoul)
    @Scheduled(cron = "0 30 11 * * *", zone = "Asia/Seoul")
    public void notifyAllAt1130() throws Exception {
        var tokens = repo.findAllByActiveTrue().stream().map(t -> t.getToken()).toList();
        fcm.sendToTokens(tokens, "점심 알림", "11:30! 점심/일정 확인하세요.", null);
    }

    // 매일 17:30 (Asia/Seoul)
    @Scheduled(cron = "0 30 17 * * *", zone = "Asia/Seoul")
    public void notifyAllAt1730() throws Exception {
        var tokens = repo.findAllByActiveTrue().stream().map(t -> t.getToken()).toList();
        fcm.sendToTokens(tokens, "퇴근 전 알림", "17:30! 운동/출석 마무리하세요.", null);
    }
}