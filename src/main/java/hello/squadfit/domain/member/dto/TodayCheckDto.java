package hello.squadfit.domain.member.dto;

public record TodayCheckDto(
        String dayOfWeek,
        int day,
        Boolean check
) {}