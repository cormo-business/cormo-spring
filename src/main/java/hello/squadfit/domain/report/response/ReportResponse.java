package hello.squadfit.domain.report.response;


import hello.squadfit.domain.member.entity.Trainer;
import hello.squadfit.domain.report.entity.Report;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
public class ReportResponse {

    private String title;
    private String content;
    private Long reportId;
    private String trainerName;
    private Long trainerId;

    public static ReportResponse entityToRequest(Report report){
        ReportResponse reportResponse = new ReportResponse();
        reportResponse.title = report.getTitle();
        reportResponse.content = report.getContent();
        reportResponse.reportId = report.getId();

        Trainer trainer = report.getTrainer();
        reportResponse.trainerId = trainer.getId();
        reportResponse.trainerName = trainer.getName();

        return reportResponse;
    }
}
