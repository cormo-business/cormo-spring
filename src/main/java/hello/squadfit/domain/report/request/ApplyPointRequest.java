package hello.squadfit.domain.report.request;

import lombok.Getter;

import java.util.List;

@Getter
public class ApplyPointRequest {

    private List<Long> videoIds;
    private Boolean isDetail;
}
