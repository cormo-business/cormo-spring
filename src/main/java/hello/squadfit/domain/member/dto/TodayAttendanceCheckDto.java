package hello.squadfit.domain.member.dto;

public record TodayAttendanceCheckDto(
        String dayOfWeek,
        int day,
        Boolean check
) {}