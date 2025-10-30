package hello.squadfit.domain.member.dto;

import hello.squadfit.domain.member.request.CreateTrainerRequest;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateTrainerDto {

    private String place; // 체육관 장소
    private String name;

    public static CreateTrainerDto from(CreateTrainerRequest request){

        CreateTrainerDto createTrainerDto = new CreateTrainerDto();
        createTrainerDto.place = request.getPlace();
        createTrainerDto.name = request.getName();

        return createTrainerDto;
    }

}
