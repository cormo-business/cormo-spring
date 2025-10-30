package hello.squadfit.domain.member.repository;

import hello.squadfit.domain.member.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query(
            value = "select a from Attendance a join fetch a.member m where m.id = :memberId",
            countQuery = "select count(a) from Attendance a where a.member.id = : memberId"
    )
    Page<Attendance> findPageAttendance(@Param("memberId") Long memberId, Pageable pageable);
}
