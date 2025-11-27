package hello.squadfit.domain.member.service;

import hello.squadfit.domain.member.dto.TodayCheckDto;
import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.response.HomeInitResponse;
import hello.squadfit.domain.record.service.RecordService;
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
    private final RecordService recordService;

    public HomeInitResponse initHome(Long userId){

        Member member = memberService.findOneByUserId(userId);
        String nickName = member.getNickName();
        Integer level = member.getLevel();
        Integer point = member.getPoint();
        Integer requiredExperience = member.getRequiredExperience();
        int size = member.getAttendances().size();

        // 출석 여부
        Boolean checkAttendance = attendanceService.checkAttendance(member);
        // 연속 출석 수 -> 필요없을지도
        int continuousAttendance = attendanceService.getContinuousAttendance(member);
        
        // 이번주 출석 관련 정보들
        WeekDto thisWeekRange = getThisWeekRange();
        List<TodayCheckDto> weekAttendance = attendanceService.getWeekAttendance(member, thisWeekRange.start, thisWeekRange.end);

        // 이번주 운동 횟수
        List<TodayCheckDto> weekRecords = recordService.getWeekRecord(member,thisWeekRange.start, thisWeekRange.end);

        // 오늘 운동 몇번했는지
        int todayRecordNum = recordService.getCountTodayRecord(member);

        return new HomeInitResponse(
                nickName, level, point, requiredExperience, size, "몰라",
                userId, checkAttendance, continuousAttendance, weekAttendance, todayRecordNum, weekRecords
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
