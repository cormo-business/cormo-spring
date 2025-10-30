package hello.squadfit.domain.video.dto;

import hello.squadfit.domain.video.entity.VideoVisibility;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class SaveVideoDto {
    private String title;
    private String key;
}
