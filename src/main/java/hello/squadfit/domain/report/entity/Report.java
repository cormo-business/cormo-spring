package hello.squadfit.domain.report.entity;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.entity.Trainer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;
    private String title;
    private String content;
    private Boolean isDone; // 레포트 작성되었는지
    // todo: 파일로 추가할 예정 지금은 제목과 내용으로 간단히 만들기

    // == 연관관계 == //
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @OneToMany(mappedBy = "report", cascade = ALL, orphanRemoval = true)
    private List<VideoReport> videoReports = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // == 연관관계 편의 메서드 == //
    public void addVideoReports(VideoReport videoReport) {
        videoReports.add(videoReport);
        videoReport.addReport(this);

    }

    public void linked(Trainer trainer, Member member){
        this.trainer = trainer;
        trainer.getReports().add(this);

        this.member = member;
        member.getReports().add(this);
    }

    // == 생성 메서드 == //
    public static Report create(Trainer trainer, Member member, List<VideoReport> videoReports){
        Report report = new Report();
        report.title = "";
        report.content = "";
        report.isDone = false;

        for (VideoReport videoReport : videoReports) {
            report.addVideoReports(videoReport);
        }
        report.linked(trainer, member);

        return report;

    }

    // 리포트 작성하기
    public Long publishReport(String content, String title){
        if(this.isDone){
            throw new RuntimeException("이미 작성된 리포트입니다.");
        }
        this.content = content;
        this.title = title;
        this.isDone = true;
        return id;

    }

}
