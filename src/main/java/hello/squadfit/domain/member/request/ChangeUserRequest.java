package hello.squadfit.domain.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangeUserRequest {

    @NotBlank
    private String birth;

}
