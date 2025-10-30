package hello.squadfit.domain.member.response;

import hello.squadfit.domain.member.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class AttendanceResponse {

    private LocalDateTime attendanceTime;
    private String memberName;

    public static AttendanceResponse from(Attendance attendance){
        return new AttendanceResponse(attendance.getAttendanceTime(), attendance.getMember().getNickName()); // 1 -> 1 -> 1 이라서 N+1 문제 없을듯?
    }

}
