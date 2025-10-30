package hello.squadfit.domain.report.repository;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.report.entity.PointReport;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PointReportRepository extends JpaRepository<PointReport, Long> {
    Page<PointReport> findByMember(Member member, PageRequest pageRequest);
}
