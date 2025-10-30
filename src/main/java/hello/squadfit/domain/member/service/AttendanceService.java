package hello.squadfit.domain.member.service;

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

        // 출석 하기 -> 변경 감지로 엔티티 저장 됨
        Attendance attendance = Attendance.create(member);

        // 경험치 및 포인트 증가
        member.increaseAttendancePoint();

        return attendance.getId();
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
}
