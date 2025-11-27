package hello.squadfit.domain.member.response;

import hello.squadfit.domain.member.dto.TodayCheckDto;

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
        List<TodayCheckDto> weekAttendance,
        int todayRecordNum,
        List<TodayCheckDto> weekRecords

) {

}