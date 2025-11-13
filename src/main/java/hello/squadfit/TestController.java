package hello.squadfit;

import hello.squadfit.domain.member.service.MemberService;
import hello.squadfit.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    private final MemberService memberService;

    public TestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/api/test")
    public Long test1(@AuthenticationPrincipal CustomUserDetails userDetails){
      log.info("id = {}", userDetails.getUserId());

      return userDetails.getUserId();

    }

    @PostMapping("/api/test")
    public TestResponse test2(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TestRequest request
            ){
        log.info("id = {}", userDetails.getUserId());

        return new TestResponse(userDetails.getUserId());
    }

    record TestRequest(
      String nickname
    ){}

    record TestResponse(
            Long memberId
    ){}
}
