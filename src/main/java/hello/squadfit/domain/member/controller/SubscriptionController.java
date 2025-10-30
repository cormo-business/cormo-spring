package hello.squadfit.domain.member.controller;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.service.MemberService;
import hello.squadfit.domain.member.service.SubscriptionService;
import hello.squadfit.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final MemberService memberService;

    // 구독하기
    @PostMapping
    public ResponseEntity<Long> createSubscription(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long subscriptionId = subscriptionService.createSubscription(userDetails.getUserId());

        return ResponseEntity.ok(subscriptionId);
    }

    // 구독 해제
    @DeleteMapping
    public ResponseEntity<String> cancelSubscription(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long memberId = subscriptionService.cancelSubscription(userDetails.getUserId());

        return ResponseEntity.ok("해제성공" + memberId);
    }

    // 구독 연장하기
    @PutMapping
    public ResponseEntity<Long> extendSubscription(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long subscriptionId = subscriptionService.extendSubscription(userDetails.getUserId());

        return ResponseEntity.ok(subscriptionId);
    }

}
