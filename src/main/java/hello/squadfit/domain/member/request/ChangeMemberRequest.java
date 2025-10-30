package hello.squadfit.domain.member.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class ChangeMemberRequest {

    private String nickName;

    private String job;

    private Integer sedentary;

    private List<String> health;

    private List<String> exercises;

}
