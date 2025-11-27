package hello.squadfit.domain.member.response;

import hello.squadfit.domain.member.dto.TodayAttendanceCheckDto;

import java.util.List;

public record HomeInitResponse(
        String nickname,
        int level,
        int point,
        int levelProgress,
        int attendanceNum,
        String profilePath,
        Long userId,
        boolean checkAttendance,
        int continuousAttendance,
        List<TodayAttendanceCheckDto> weekAttendance,
        int todayRecordNum
) {

}