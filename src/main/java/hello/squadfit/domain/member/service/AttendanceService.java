package hello.squadfit.domain.member.service;

import hello.squadfit.domain.member.dto.TodayCheckDto;
import hello.squadfit.domain.member.entity.Attendance;
import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.repository.AttendanceRepository;
import hello.squadfit.domain.member.response.AttendanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttendanceService {

    private final MemberService memberService;
    private final AttendanceRepository attendanceRepository;

    // 출석하기
    @Transactional
    public Long attendance(Long memberId){
        Member member = memberService.findOne(memberId);
        
        // 오늘 출석했는지 확인하기
        if(checkAttendance(member)){
            throw new RuntimeException("오늘 중복 출석입니다.");
        }

        // 출석 하기
        Attendance attendance = Attendance.create(member);
        Attendance save = attendanceRepository.save(attendance);

        // 경험치 및 포인트 증가
        member.increaseAttendancePoint();

        return save.getId();
    }

    // 출석 조회하기
    public Page<AttendanceResponse> findAttendanceByMember(Long memberId, int page, int size){

        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "attendanceTime"));

        Page<Attendance> attendancePage = attendanceRepository.findPageAttendance(memberId, pr);

        Page<AttendanceResponse> responses = attendancePage.map((attendance -> AttendanceResponse.from(attendance)));

        return responses;

    }
    
    // 오늘 출석 했는지 확인하기
    public Boolean checkAttendance(Member findMember){

        return findMember.getAttendances().stream().anyMatch(
                attendance -> attendance.getAttendanceTime().toLocalDate().isEqual(LocalDate.now()));

    }

    // 일정 기간 동안의 출석 여부
    public List<TodayCheckDto> getWeekAttendance(Member member, LocalDate start, LocalDate end) {

        List<TodayCheckDto> result = new ArrayList<>();

        // start ~ end 까지 하루씩 증가
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            LocalDateTime dayStart = date.atStartOfDay(); // 00:00:00
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay(); // 다음날 00:00:00 (미만 조건처럼 사용)

            boolean checked = attendanceRepository
                    .existsByMemberAndAttendanceTimeBetween(member, dayStart, dayEnd);

            // 요일 한글(월, 화, 수...)로 가져오기
            String dayOfWeekKorean = date.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.KOREAN); // 예: "월", "화"

            result.add(new TodayCheckDto(
                    dayOfWeekKorean,
                    date.getDayOfMonth(),
                    checked
            ));
        }

        return result;
    }

    public int getContinuousAttendance(Member member) {
        LocalDate today = LocalDate.now();
        int count = 0;

        while (true) {
            LocalDateTime dayStart = today.atStartOfDay();
            LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();

            boolean attended = attendanceRepository
                    .existsByMemberAndAttendanceTimeBetween(member, dayStart, dayEnd);

            if (attended) {
                count++;
                today = today.minusDays(1); // 하루 뒤로
            } else {
                break;
            }
        }

        return count;
    }

}
