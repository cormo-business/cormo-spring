package hello.squadfit.domain.member.service;

import hello.squadfit.domain.member.dto.HomeInitResponse;
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

        // 필요한 값 추출
        String nickName = member.getNickName();
        Integer level = member.getLevel();
        Integer point = member.getPoint();
        Integer requiredExperience = member.getRequiredExperience();
        int size = member.getAttendances().size();

        // 출석 여부
        Boolean checkAttendance = attendanceService.checkAttendance(member);

        // DTO 생성
        return new HomeInitResponse(
                nickName, level, point, requiredExperience, size, "몰라", userId, checkAttendance
        );
    }
}
