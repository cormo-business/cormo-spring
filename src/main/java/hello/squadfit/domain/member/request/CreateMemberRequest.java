package hello.squadfit.domain.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateMemberRequest extends CreateUserRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String birth;
    @NotBlank
    private String gender;
    @NotBlank
    private String nickName;
    @NotBlank
    private String job; // 초등, 중, 고등, 대학 직장, 무직 -> 영어로
    @NotNull
    private Integer sedentary; // 앉아있는 시간 -> 숫자로
    @NotNull
    private List<String> health; // 건강 -> 리스트형식
    @NotNull
    private List<String> exercises; // 평소 하는 운동
}
