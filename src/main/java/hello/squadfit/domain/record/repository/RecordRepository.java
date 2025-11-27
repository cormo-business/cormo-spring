package hello.squadfit.domain.record.repository;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.record.entity.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<ExerciseRecord, Long> {

    List<ExerciseRecord> findAllByMemberId(Long memberId);

    Optional<ExerciseRecord> findByMemberIdAndId(Long memberId, Long recordID);

    @Query("""
        select count(er)
        from ExerciseRecord er
        where er.member = :member
          and er.recordDate between :start and :end
    """)
    long countTodayRecord(
            @Param("member") Member member,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
