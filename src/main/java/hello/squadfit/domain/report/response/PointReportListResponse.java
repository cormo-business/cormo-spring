package hello.squadfit.domain.report.response;

import hello.squadfit.domain.report.entity.PointReport;
import lombok.*;

@Getter @Builder @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointReportListResponse {

    private Long id;
    private String title;
    private String titleImg;

    public static PointReportListResponse entityToResponse(PointReport pointReport){
        return PointReportListResponse.builder()
                .id(pointReport.getId())
                .title(pointReport.getTitle())
                .titleImg("이미지")
                .build();
    }

}
