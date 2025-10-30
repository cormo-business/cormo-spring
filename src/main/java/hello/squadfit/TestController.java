package hello.squadfit;

import hello.squadfit.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @GetMapping("/api/test")
    public Long testPro(@AuthenticationPrincipal CustomUserDetails userDetails){
      log.info("id = {}", userDetails.getUserId());

      return userDetails.getUserId();

    }
}
