package hello.squadfit.domain.member.response.mapper;

import hello.squadfit.domain.member.response.SingleRecordResponse;
import hello.squadfit.domain.record.entity.ExerciseRecord;

public class SingleRecordResponseMapper {
    public static SingleRecordResponse entityToDto(ExerciseRecord record){

        return SingleRecordResponse.builder()
                .exerciseRecordId(record.getId())
                .exerciseName(record.getExerciseType().getName())
                .weight(record.getWeight())
                .repeatNumber(record.getRepeat())
                .volume(record.getWeight() * record.getRepeat())
                .successNumber(record.getSuccessNumber())
                .failNumber(record.getFailNumber())
                .category(record.getExerciseType().getCategory())
                .build();
    }
}
