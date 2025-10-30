package hello.squadfit.domain.member.entity;

import hello.squadfit.domain.PointConst;
import hello.squadfit.domain.member.Role;
import hello.squadfit.domain.common.BaseEntity;
import hello.squadfit.domain.member.request.ChangeMemberRequest;
import hello.squadfit.domain.record.entity.ExerciseRecord;
import hello.squadfit.domain.member.dto.CreateMemberDto;
import hello.squadfit.domain.report.entity.PointReport;
import hello.squadfit.domain.report.entity.Report;
import hello.squadfit.domain.video.entity.Video;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본생성자 protected로 설정하여 기본생성자 사용못하게 막기
// 바뀌는 member 정보
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

//    @Embedded
//    private MemberProfile profile;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String job;

    @Column(nullable = false)
    private Integer sedentary; // 앉아있는 시간

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer requiredExperience; // 잔여 경험치

    private Boolean subscribed; // 구독 여부

    private Integer point;

    private Long missionCount; // 미션 성공 횟수

    @Column(nullable = false)
    private Integer availableReportCount; // 레포트 신청 가능한 숫자
    
    @ElementCollection(fetch = LAZY)
    @CollectionTable(
            name = "member_health",
            joinColumns = @JoinColumn(name = "member_id")
    )
    private List<String> health = new ArrayList<>(); // 건강 테이블

    @ElementCollection(fetch = LAZY)
    @CollectionTable(
            name = "member_exercise",
            joinColumns = @JoinColumn(name = "member_id")
    )
    private List<String> exercises = new ArrayList<>(); // 운동 테이블
    
    // == 연관관계 == //
    @OneToOne(fetch = LAZY,mappedBy = "member")
    private UserEntity userEntity;

    @OneToOne(mappedBy = "member", fetch = LAZY,cascade = ALL)
    private Subscription subscription;

    @OneToMany(mappedBy = "member", cascade = ALL)
    private List<Attendance> attendances = new ArrayList<>();

    @OneToMany(mappedBy = "member" , cascade = ALL) // record 테이블에 있는 member 필드를 참조함
    private List<ExerciseRecord> exerciseRecords = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = ALL)
    private List<Video> videos = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = ALL)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = ALL)
    private List<PointReport> pointReports = new ArrayList<>();

    
//    private List<Notification> notifications = new ArrayList<>();

//    private List<Payment> payments = new ArrayList<>();

//    private List<BestRecord> bestRecords = new ArrayList<>();

    // == 연관관계 편의 메서드 == //
    public void linkSubscription(Subscription subscription) {
        this.subscription = subscription;
        if(subscription.getMember() != this){ // 중복 방지
            this.subscription.linkMember(this);
        }
    }

    public void addUser(UserEntity userEntity){
        this.userEntity = userEntity;
        userEntity.addMember(this);
        userEntity.addRole(Role.Member);
    }

    // == 생성 메서드 == //
    public static Member create(CreateMemberDto dto, UserEntity userEntity){
        Member member = new Member();
        member.nickName = dto.getNickName();
        member.level = 1;
        member.requiredExperience = 100;
        member.subscription = null;
        member.point = 0;
        member.availableReportCount = 5;
        member.subscribed = false;
        member.missionCount = 0L;

        member.health = dto.getHealth();
        member.sedentary = dto.getSedentary();
        member.job = dto.getJob();
        member.exercises = dto.getExercises();

        member.addUser(userEntity);
        return member;
    }

    // == 비즈니스 로직 == //
    /**
     * 프로필 변경
     */
    public Long changeInfo(ChangeMemberRequest request){
        this.nickName = request.getNickName();
        this.health = request.getHealth();
        this.exercises = request.getExercises();
        this.sedentary = request.getSedentary();
        this.job = request.getJob();

        return this.id;

    }

    /**
     * 출석 포인트 증가
     */
    public void increaseAttendancePoint(){
        gainExperience(PointConst.ATTENDANCE_POINT);
        point += PointConst.ATTENDANCE_POINT;

    }

    /**
     * 운동하기 포인트 증가
     */
    public void increaseExercisePoint(){
        gainExperience(PointConst.EXERCISE_POINT);
        this.point += PointConst.EXERCISE_POINT;
        System.out.println("포인트 증가");
    }

    /**
     * 코멘트 달기 포인트 감소
     */
    public void requestComment(){
        if(point < PointConst.COMMENT_POINT){
            throw new IllegalStateException("포인트가 부족하여 코멘트 신청이 불가합니다");
        }
        point -= PointConst.COMMENT_POINT;
    }

    /**
     * 레포트 신청하기
     */
    public void requestReport() {
        if (availableReportCount <= 0) {
            throw new IllegalStateException("신청 가능한 레포트 수가 없습니다.");
        }
        availableReportCount--;
    }

    /**
     * 포인트 이용한 레포트 신청하기
     */
    public void requestPointReport() {
        if(point < PointConst.REPORT_POINT){
            throw new IllegalStateException("포인트가 부족하여 레포트 신청이 불가합니다, 당신의 포인트 : " + point);
        }
        point -= PointConst.REPORT_POINT;
    }

    /**
     * 포인트 이용한 디테일한 레포트 신청하기
     */
    public void requestPointDetailReport() {
        if(point < PointConst.DETAIL_REPORT_POINT){
            throw new IllegalStateException("포인트가 부족하여 상세 레포트 신청이 불가합니다, 당신의 포인트 :" + point);
        }
        point -= PointConst.DETAIL_REPORT_POINT;
    }


    /**
     * 구독하기
     */
    public Long subscribe(LocalDateTime startDate, LocalDateTime endDate){
        if (this.subscription != null && Boolean.TRUE.equals(this.subscription.getStatus())) {
            throw new IllegalStateException("이미 구독 중입니다.");
        }
        this.subscription = Subscription.createSubscription(this, startDate, endDate);
        this.subscribed = true;
        this.linkSubscription(subscription);
        return subscription.getId();
    }
    /**
     * 구독 해지
     */
    public void cancelSubscription() {
        if (isNotExistSubscription()) {
            throw new IllegalStateException("구독한 내역이 없습니다.");
        }
        this.subscription.unsubscribe();
        this.subscribed = false;
        this.subscription = null;
    }

    /**
     * 구독 연장
     */
    public Long extendSubscription(LocalDateTime endDate) {
        if (isNotExistSubscription()) {
            throw new IllegalStateException("구독한 내역이 없습니다.");
        }
        this.subscription.continueSubscription(endDate);
        return this.subscription.getId();
    }

    /**
     * 구독 여부 확인
     */
    private boolean isNotExistSubscription() {

        return this.subscription == null || this.subscribed == false;
    }

    /**
     * 프로필 & 닉네임 변경
     */
    public void changeProfile(String nickName, List<String> health, int sedentary, String job, List<String> exercises ){
        this.nickName = nickName;
        this.health = health;
        this.sedentary = sedentary;
        this.job = job;
        this.exercises = exercises;

    }

    /**
     * 미션 성공
     */
    public void successMission(){
        this.missionCount += 1L;
    }

    // 경험치 획득
    private void gainExperience(Integer exp){
        if (exp <= 0) throw new IllegalArgumentException("경험치는 양수여야 합니다.");

        int remainExp = requiredExperience - exp;
        if(remainExp <= 0){
            levelUp(remainExp);

        }else{
            // 레벨업 못함
            requiredExperience = remainExp;
        }

    }

    // 레벨 업
    private void levelUp(int remainExp) {
        // 레벨업
        level++;
        // 새로운 필요 경험치
        requiredExperience = PointConst.INITIAL_EXPERIENCE + (level - 1) * PointConst.LEVEL_UP_POINT;
        // 남은 경험치를 다음 레벨의 필요 경험치에서 차감
        requiredExperience += remainExp;
    }
}
