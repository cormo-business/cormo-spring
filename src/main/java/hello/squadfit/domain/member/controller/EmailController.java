package hello.squadfit.domain.member.controller;

import hello.squadfit.domain.member.request.CheckedEmailRequest;
import hello.squadfit.domain.member.request.SendEmailRequest;
import hello.squadfit.domain.member.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    // 메일 인증 보내기
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(
            @RequestBody SendEmailRequest request
    ){

        Boolean result = emailService.sendEmail(request);

        return ResponseEntity.ok(result);

    }

    // 메일 확인하기
    @PostMapping("/check")
    public ResponseEntity<?> checkedEmail(@RequestBody CheckedEmailRequest request){

        Boolean result = emailService.checkedEmail(request);

        return ResponseEntity.ok(result);

    }
}
