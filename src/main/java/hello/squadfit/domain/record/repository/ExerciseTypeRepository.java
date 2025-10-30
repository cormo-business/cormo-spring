package hello.squadfit.domain.record.repository;

import hello.squadfit.domain.record.entity.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Long> {
    Optional<ExerciseType> findByName(String name);
}
