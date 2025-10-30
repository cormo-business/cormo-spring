package hello.squadfit.domain.record.controller;

import hello.squadfit.domain.record.ExerciseCategory;
import hello.squadfit.domain.record.entity.ExerciseType;
import hello.squadfit.domain.record.repository.ExerciseTypeRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercise-type")
public class ExerciseTypeController {

    private final ExerciseTypeRepository exerciseTypeRepository;

    @PostMapping
    public ResponseEntity<Long> saveExercise(@RequestBody ExerciseDto exerciseDto){

        Long result = exerciseTypeRepository.findByName(exerciseDto.getExerciseName())
                .map(exerciseType -> exerciseType.getId())
                .orElseGet(() -> {
                    ExerciseType newType = ExerciseType.createExerciseType(exerciseDto.getExerciseName(), exerciseDto.getCategory());
                    return exerciseTypeRepository.save(newType).getId();
                });

        return ResponseEntity.ok(result);
    }

    @GetMapping("{exerciseName}")
    public ResponseEntity<Long> findExerciseType(@PathVariable String exerciseName){
        ExerciseType exerciseType = exerciseTypeRepository.findByName(exerciseName).orElseThrow(() -> new RuntimeException("운동이름 잘못됐는데?"));

        return ResponseEntity.ok(exerciseType.getId());
    }

    // todo: 이거 어떻게 할거?
    @Getter
    public static class ExerciseDto{
        private String exerciseName;
        private ExerciseCategory category;
    }
}
