package hello.squadfit.domain.report.response;

import hello.squadfit.domain.report.entity.PointReport;
import lombok.*;

@Getter @Builder @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointReportResponse {

    private Long id;
    private String title;
    private String content;

    public static PointReportResponse entityToResponse(PointReport pointReport){
        return PointReportResponse.builder()
                .id(pointReport.getId())
                .title(pointReport.getTitle())
                .content(pointReport.getContent())
                .build();
    }
}
