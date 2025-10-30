package hello.squadfit.domain.report.entity;

import hello.squadfit.domain.video.entity.Video;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_report_id")
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    // == 연관관계 == //
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "video_id")
    private Video video;


    // == 연관관계 편의 메서드 == //
    public void addVideo(Video video){
        this.video = video;
        video.getVideoReports().add(this);
    }

    public void addReport(Report report) {
        this.report = report;
    }


    // == 생성자 메서드 == //
    public static VideoReport create(Video video){
        VideoReport videoReport = new VideoReport();

        videoReport.addVideo(video);

        return videoReport;
    }

    // == 비즈니스 로직 == //

}
