package hello.squadfit.domain.record.repository;

import hello.squadfit.domain.record.entity.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<ExerciseRecord, Long> {

    List<ExerciseRecord> findAllByMemberId(Long memberId);

    Optional<ExerciseRecord> findByMemberIdAndId(Long memberId, Long recordID);

}
