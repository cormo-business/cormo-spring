package hello.squadfit.domain.certification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsRequestDto {

    private String type; // SMS, LMS 등
    private String contentType; // COMM(일반), AD(광고)
    private String countryCode; // "82"
    private String from; // 발신번호
    private String content; // 기본 내용(옵션)
    private List<SmsMessage> messages;

    public static SmsRequestDto of(String from, String content, List<SmsMessage> messages) {
        return SmsRequestDto.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(from)
                .content(content)
                .messages(messages)
                .build();
    }
}
