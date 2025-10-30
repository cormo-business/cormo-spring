package hello.squadfit.domain.member.response;

import lombok.*;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginTrainerResponse {

    private String name;
    private String place;

}
