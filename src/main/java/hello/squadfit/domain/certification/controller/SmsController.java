package hello.squadfit.domain.certification.controller;

import hello.squadfit.domain.certification.request.SmsSendRequest;
import hello.squadfit.domain.certification.request.SmsVerifyRequest;
import hello.squadfit.domain.certification.service.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody @Valid SmsSendRequest request) {
        smsService.sendVerificationCode(request.getPhoneNumber());

        return ResponseEntity.ok().build();
    }

    // SMS 확인하기
    @PostMapping("/check")
    public ResponseEntity<?> check(@RequestBody SmsVerifyRequest request){

        Boolean result = smsService.verifyCode(request);

         if (!result) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);

         return ResponseEntity.ok(true);

    }
}