package hello.squadfit.domain.member.service;

import hello.squadfit.domain.member.dto.TodayAttendanceCheckDto;
import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.response.HomeInitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InitService {

    private final MemberService memberService;
    private final AttendanceService attendanceService;

    public HomeInitResponse initHome(Long userId){

        Member member = memberService.findOneByUserId(userId);
        String nickName = member.getNickName();
        Integer level = member.getLevel();
        Integer point = member.getPoint();
        Integer requiredExperience = member.getRequiredExperience();
        int size = member.getAttendances().size();

        Boolean checkAttendance = attendanceService.checkAttendance(member);
        int continuousAttendance = attendanceService.getContinuousAttendance(member);

        WeekDto thisWeekRange = getThisWeekRange();
        List<TodayAttendanceCheckDto> weekAttendance = attendanceService.getWeekAttendance(member, thisWeekRange.start, thisWeekRange.end);


        return new HomeInitResponse(
                nickName, level, point, requiredExperience, size, "몰라",
                userId, checkAttendance, continuousAttendance, weekAttendance
        );
    }

    /**
     * 이번주의 범위 지정
     * "start": "2025-11-24",
     * "end": "2025-11-30"
     * @return
     */
    public WeekDto getThisWeekRange() {
        LocalDate today = LocalDate.now();

        LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end   = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return new WeekDto(start, end);
    }
    public record WeekDto(LocalDate start, LocalDate end) {}




}
