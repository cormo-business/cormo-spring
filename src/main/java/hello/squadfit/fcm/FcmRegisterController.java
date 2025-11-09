package hello.squadfit.fcm;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fcm")
public class FcmRegisterController {

    private final DeviceTokenRepository repo;

    public FcmRegisterController(DeviceTokenRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Validated @RequestBody RegisterReq req) {
        var existing = repo.findByToken(req.token).orElse(null);
        if (existing == null) {
            repo.save(new DeviceToken(req.userId, req.token, req.platform));
        } else {
            existing.setUserId(req.userId);
            existing.setActive(true);
            existing.setLastSeenAt(java.time.LocalDateTime.now());
            repo.save(existing);
        }
        return ResponseEntity.ok().build();
    }
}