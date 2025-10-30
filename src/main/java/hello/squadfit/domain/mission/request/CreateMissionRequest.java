package hello.squadfit.domain.mission.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CreateMissionRequest {

    private String title;
    private String content;
}
