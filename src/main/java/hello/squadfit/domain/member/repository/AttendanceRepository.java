package hello.squadfit.domain.member.repository;

import hello.squadfit.domain.member.entity.Attendance;
import hello.squadfit.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query(
            value = "select a from Attendance a join fetch a.member m where m.id = :memberId",
            countQuery = "select count(a) from Attendance a where a.member.id = : memberId"
    )
    Page<Attendance> findPageAttendance(@Param("memberId") Long memberId, Pageable pageable);


    // 특정 회원이 그 날(하루)에 출석했는지 여부
    boolean existsByMemberAndAttendanceTimeBetween(
            Member member,
            LocalDateTime start,
            LocalDateTime end
    );
}
