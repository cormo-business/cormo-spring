package hello.squadfit.domain.member.response;

import hello.squadfit.domain.member.entity.Trainer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @AllArgsConstructor
public class TrainerInfoResponse {

    private String username;
    private String place;
    private String name;
    // todo: 코멘트들, 리포트들 추가할건가? 아니면 다른 api 사용할건가? 고민되네

    public static TrainerInfoResponse from(Trainer trainer){
        return new TrainerInfoResponse(trainer.getUserEntity().getUsername(), trainer.getPlace(), trainer.getName());
    }

}
