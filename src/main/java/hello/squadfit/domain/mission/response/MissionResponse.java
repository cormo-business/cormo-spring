package hello.squadfit.domain.mission.response;

import hello.squadfit.domain.mission.entity.Mission;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MissionResponse {
    private String title;
    private String content;

    public static MissionResponse entityToResponse(Mission mission){

        return MissionResponse.builder()
                .content(mission.getContent())
                .title(mission.getTitle())
                .build();

    }
}
