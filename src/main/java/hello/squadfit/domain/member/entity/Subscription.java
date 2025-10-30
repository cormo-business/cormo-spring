package hello.squadfit.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 구독
public class Subscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;

    private Boolean status; // 구독 내역을 봐야해서
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer count; // 연장횟수
//    private Long memberId; // 구독 내역 확인을 위한 멤버

    // == 연관관계 == //
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    // == 생성 메서드 == //
    public static Subscription createSubscription(Member member, LocalDateTime startDate, LocalDateTime endDate){
        Subscription subscription = new Subscription();
        subscription.status = true;
        subscription.startDate = startDate;
        subscription.endDate = endDate;
//        member.linkSubscription(subscription);
//        subscription.member = member;
        subscription.count = 0;
//        subscription.memberId = member.getId();
        return subscription;
    }

    // == 비즈니스 로직 == //
    public void linkMember(Member member){
        this.member = member;
    }

    /**
     * 구독 해지
     */
    public void unsubscribe(){
        if(this.status == null || this.status == false){
            throw new IllegalStateException("구독한적이 없습니다.");
        }
        this.status = false;
        this.member = null;
    }

    /**
     * 구독 연장
     */
    public void continueSubscription(LocalDateTime endDate){
        if(this.status == null || this.status == false){
            throw new IllegalStateException("구독한적이 없습니다.");
        }
        this.endDate = endDate;
        count++;
    }

}
