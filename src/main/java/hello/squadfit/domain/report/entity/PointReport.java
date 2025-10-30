package hello.squadfit.domain.report.entity;

import hello.squadfit.domain.PointConst;
import hello.squadfit.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_report_id")
    private Long id;

    private String title;
    private String content;

    @Column(nullable = false)
    private Boolean isDone;
    @Column(nullable = false)
    private Boolean isDetail;

    // == 연관관계 == //
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "pointReport", cascade = ALL)
    private List<VideoPointReport> videoPointReports = new ArrayList<>();

    // == 연관관계 편의 메서드 == //
    private void addMember(Member member){
        member.getPointReports().add(this);
    }
    private void addVideoPointReport(List<VideoPointReport> videoPointReports){
        for (VideoPointReport videoPointReport : videoPointReports) {
            this.videoPointReports.add(videoPointReport);
            videoPointReport.addPointReport(this);
        }
    }

    // == 생성자 메서드 == //
    public static PointReport create(Member member, boolean isDetail, List<VideoPointReport> videoPointReports){
        PointReport pointReport = new PointReport();
        pointReport.title = "";
        pointReport.content = "";
        pointReport.isDone = false;
        pointReport.isDetail = isDetail;
        usePoint(member, isDetail); // 포인트 사용

        pointReport.addVideoPointReport(videoPointReports);

        pointReport.addMember(member);

        return pointReport;
    }



    // == 비지니스 메서드 == //
    /**
     * 레포트 작성하기
     */
    public PointReport write(String title, String content){
        this.title = title;
        this.content = content;
        isDone = true;
        return this;
    }

    // 포인트 사용하기
    private static void usePoint(Member member, boolean isDetail) {
        if(isDetail){
            member.requestPointDetailReport();
        }else {
            member.requestPointReport();
        }
    }

}
