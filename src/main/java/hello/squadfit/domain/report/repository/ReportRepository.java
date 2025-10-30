package hello.squadfit.domain.report.repository;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findAllByMember(Member member, PageRequest of);
}
