package hello.squadfit.domain.member.service;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.entity.Subscription;
import hello.squadfit.domain.member.entity.UserEntity;
import hello.squadfit.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final MemberService memberService;
    private final UserService userService;

    // 구독하기
    public Long createSubscription(Long userId){

        Member member = memberService.findOneByUserId(userId);

        Member findMember = memberService.findOne(member.getId());

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(1);

        return findMember.subscribe(startDate, endDate);

    }

    // 해제하기
    public Long cancelSubscription(Long userId){

        Member member = memberService.findOneByUserId(userId);

        Member findMember = memberService.findOne(member.getId());

        findMember.cancelSubscription();

        return member.getId();
    }

    // 연장하기
    public Long extendSubscription(Long userId){
        Member member = memberService.findOneByUserId(userId);

        Member findMember = memberService.findOne(member.getId());

        Subscription subscription = findMember.getSubscription();

        // endDate 계산하기 위해 nullPointException 처리
        if(subscription == null){
            throw new RuntimeException("구독 한거 맞아?");
        }

        LocalDateTime originalEndDate = subscription.getEndDate();
        LocalDateTime endDate = originalEndDate.plusMonths(1);

        return findMember.extendSubscription(endDate);
    }

}
