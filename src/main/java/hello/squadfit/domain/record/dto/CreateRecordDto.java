package hello.squadfit.domain.record.dto;

import hello.squadfit.domain.record.entity.ExerciseType;
import hello.squadfit.domain.member.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRecordDto {

    private Member member;
    private Integer repeat;
    private Integer weight;
    private Integer successNumber;
    private Integer failNumber;
    private ExerciseType exerciseType;

}
