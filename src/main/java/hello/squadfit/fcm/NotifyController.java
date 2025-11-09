package hello.squadfit.fcm;


import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notify")
public class NotifyController {

    private final FcmService fcm;
    private final DeviceTokenRepository repo;

    public NotifyController(FcmService fcm, DeviceTokenRepository repo) {
        this.fcm = fcm;
        this.repo = repo;
    }

    public static class NotifyReq {
        @NotBlank
        public String title;
        @NotBlank
        public String body;
        public Map<String, String> data;
    }
    @PostMapping("/all")
    public ResponseEntity<?> notifyAll(@RequestBody NotifyReq req) {
        var tokens = repo.findAllByActiveTrue().stream().map(t -> t.getToken()).toList();
        var result = fcm.sendToTokens(tokens, req.title, req.body, req.data);
        return ResponseEntity.ok(Map.of(
                "success", result.successCount,
                "failed", result.failureCount
        ));
    }

    @PostMapping("/excluding/{userId}")
    public ResponseEntity<?> notifyOthers(@PathVariable Long userId, @RequestBody NotifyReq req) {
        var tokens = repo.findAllByUserIdNotAndActiveTrue(userId).stream().map(t -> t.getToken()).toList();
        Map<String, String> merged = new java.util.HashMap<>();
        if (req.data != null) merged.putAll(req.data);
        merged.put("senderId", String.valueOf(userId));
        var result = fcm.sendToTokens(tokens, req.title, req.body, merged);
        return ResponseEntity.ok(Map.of("success", result.successCount, "failed", result.failureCount));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> notifyUser(@PathVariable Long userId, @RequestBody NotifyReq req) {
        var tokens = repo.findAllByUserIdAndActiveTrue(userId).stream().map(t -> t.getToken()).toList();
        if (tokens.isEmpty()) return ResponseEntity.badRequest().body("No tokens for user: " + userId);
        var result = fcm.sendToTokens(tokens, req.title, req.body, req.data);
        return ResponseEntity.ok(Map.of("success", result.successCount, "failed", result.failureCount));
    }

    @PostMapping("/topic/{topic}")
    public ResponseEntity<?> notifyTopic(@PathVariable String topic, @RequestBody NotifyReq req) throws Exception {
        String messageId = fcm.sendToTopic(topic, req.title, req.body, req.data);
        return ResponseEntity.ok(Map.of("messageId", messageId));
    }

//
//
//    // (2) 공지 형식으로 일회성 전체 발송
//    @PostMapping("/all")
//    public ResponseEntity<String> notifyAll(@RequestBody NotifyReq req) throws Exception {
//        var tokens = repo.findAllByActiveTrue().stream().map(t -> t.getToken()).toList();
//        fcm.sendToTokens(tokens, req.title, req.body, req.data);
//        return ResponseEntity.ok("OK");
//    }
//
//    // (3) 자신을 제외한 다른 유저들에게 발송
//    @PostMapping("/excluding/{userId}")
//    public ResponseEntity<String> notifyOthers(@PathVariable Long userId, @RequestBody NotifyReq req) throws Exception {
//        var tokens = repo.findAllByUserIdNotAndActiveTrue(userId).stream().map(t -> t.getToken()).toList();
//        Map<String, String> merged = new java.util.HashMap<>();
//        if (req.data != null) merged.putAll(req.data);
//        merged.put("senderId", String.valueOf(userId));
//        fcm.sendToTokens(tokens, req.title, req.body, merged);
//        return ResponseEntity.ok("OK");
//    }
//
//    // (4) 특정 사람(유저)에게 발송
//    @PostMapping("/user/{userId}")
//    public ResponseEntity<String> notifyUser(@PathVariable Long userId, @RequestBody NotifyReq req) throws Exception {
//        var tokens = repo.findAllByUserIdAndActiveTrue(userId).stream().map(t -> t.getToken()).toList();
//        if (tokens.isEmpty()) return ResponseEntity.badRequest().body("No tokens for user: " + userId);
//        fcm.sendToTokens(tokens, req.title, req.body, req.data);
//        return ResponseEntity.ok("OK");
//    }
//
//    // (5) 특정 토픽 구독자에게만 발송
//    @PostMapping("/topic/{topic}")
//    public ResponseEntity<String> notifyTopic(@PathVariable String topic, @RequestBody NotifyReq req) throws Exception {
//        fcm.sendToTopic(topic, req.title, req.body, req.data);
//        return ResponseEntity.ok("OK");
//    }
}