package hello.squadfit.domain.member.repository;

import hello.squadfit.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsMemberById(Long memberId);

    Optional<Member> findByUserEntity_Id(Long userId);
}
