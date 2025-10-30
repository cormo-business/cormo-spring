package hello.squadfit.fcm;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push")
public class PushTestController {
    private final FcmService fcm;

    public PushTestController(FcmService fcm) {
        this.fcm = fcm;
    }

    @PostMapping("/test/{token}")
    public void test(@PathVariable String token) {
        fcm.sendToToken(token, "테스트", "테스트 알림입니다.");
    }
}
