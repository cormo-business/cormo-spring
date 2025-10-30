package hello.squadfit.domain.record.entity;

import hello.squadfit.domain.record.ExerciseCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseType {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_type_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ExerciseCategory category;

    public static ExerciseType createExerciseType(String name, ExerciseCategory category){
        ExerciseType exerciseType = new ExerciseType();
        exerciseType.name = name;
        exerciseType.category = category;
        return exerciseType;
    }

}
