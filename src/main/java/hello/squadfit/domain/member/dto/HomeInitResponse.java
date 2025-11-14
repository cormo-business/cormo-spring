package hello.squadfit.domain.member.dto;

public record HomeInitResponse(
        String nickname,
        int level,
        int point,
        int levelProgress,
        int attendanceNum,
        String profilePath,
        Long userId,
        boolean checkAttendance
) {}
