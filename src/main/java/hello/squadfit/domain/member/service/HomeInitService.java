package hello.squadfit.domain.member.service;

import hello.squadfit.domain.member.response.HomeInitResponse;
import hello.squadfit.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeInitService {
    private final MemberService memberService;
    private final AttendanceService attendanceService;

    public HomeInitResponse homeInit(Long userId) {
        // Member 조회
        Member member = memberService.findOneByUserId(userId);

        // 출석 여부
        Boolean checkAttendance = attendanceService.checkAttendance(member);

        // DTO 생성
        return HomeInitResponse.from(member, checkAttendance);
    }
}
