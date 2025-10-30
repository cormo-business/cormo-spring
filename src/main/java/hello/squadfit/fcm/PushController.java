package hello.squadfit.fcm;


import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/push")
public class PushController {

    private final DeviceTokenRepository repo;

    public PushController(DeviceTokenRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterTokenRequest req) {
        // "HH:mm" -> 분 변환
        List<Integer> minutes = req.scheduleTimes().stream()
                .map(s -> {
                    final var parts = s.split(":");
                    int h = Integer.parseInt(parts[0]);
                    int m = Integer.parseInt(parts[1]);
                    return h * 60 + m;
                })
                .distinct().sorted().toList();

        DeviceToken dt = repo.findAll().stream()
                .filter(d -> d.getToken().equals(req.token()))
                .findFirst()
                .orElse(new DeviceToken());

        dt.setUserId(req.userId());
        dt.setToken(req.token());
//        dt.setPlatform(req.platform());
//        dt.setTimezone(req.timezone());
        dt.setActive(true);
        dt.setLastSeenAt(Instant.now());
        dt.setMinutesOfDay(minutes);

        repo.save(dt);
    }
}