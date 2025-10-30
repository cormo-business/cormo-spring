package hello.squadfit.domain.video.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class SaveVideoRequest {

    @NotBlank
    private String title;

}
