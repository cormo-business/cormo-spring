package hello.squadfit.domain.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Builder
public class CreateTrainerRequest extends CreateUserRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String birth;
    @NotBlank
    private String name;
    @NotBlank
    private String place;
}
