package hello.squadfit.domain.certification.dto;

public record SmsResponseDto(
        String statusCode,
        String statusName,
        String requestId,
        String requestTime
){
}
