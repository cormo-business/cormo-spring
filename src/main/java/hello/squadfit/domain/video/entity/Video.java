package hello.squadfit.domain.video.entity;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.record.entity.ExerciseRecord;
import hello.squadfit.domain.report.entity.VideoPointReport;
import hello.squadfit.domain.report.entity.VideoReport;
import hello.squadfit.domain.video.dto.SaveVideoDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity @Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String saveKey; // 저장 장소

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VideoVisibility visibility; // PUBLIC, PRIVATE

    // == 연관관계 == //
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "record_id", unique = true, nullable = false)
    private ExerciseRecord record;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "video", cascade = ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "video", cascade = ALL, orphanRemoval = true)
    private List<VideoReport> videoReports = new ArrayList<>();

    @OneToMany(mappedBy = "video", cascade = ALL)
    private List<VideoPointReport> videoPointReports = new ArrayList<>();


    // == 연관관계 편의메서드 == //
    public void addMember(Member member){
        this.member = member;
        member.getVideos().add(this);
    }

    public void addRecord(ExerciseRecord record){
        this.record = record;
        // 역방향 세팅 (반대편에서 재귀 호출하지 않도록 주의)
        if (record.getVideo() != this) {
            record.linkVideo(this);
        }
    }

    // == 생성 메서드 == //
    public static Video create(Member member, ExerciseRecord record, SaveVideoDto dto){
        Video video = new Video();
        video.title = dto.getTitle();
        video.saveKey = dto.getKey();
        video.visibility = VideoVisibility.PRIVATE;

        video.addMember(member);
        video.addRecord(record);

        return video;

    }

    // == 비즈니스 로직 == //

    /**
     * 동영상 타이틀 변경 todo:
     */
    public void renameTitle(String title){
        this.title = title;
    }


    // 코멘트 신청하기
    public void requestComment(Member member) {
        if(visibility.equals(VideoVisibility.PUBLIC)){
            throw new RuntimeException("이미 신청했는데?");
        }
//         member.requestComment(); // 포인트 감소

        visibility = VideoVisibility.PUBLIC;
    }

    // 코멘트 취소하기
    public void cancelComment(){
        if(!visibility.equals(VideoVisibility.PRIVATE)){
            throw new RuntimeException("신청도 안했는데?");
        }
        visibility = VideoVisibility.PRIVATE;
    }
}
