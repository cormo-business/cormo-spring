package hello.squadfit.domain.certification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsMessage {

    private String to; // 수신번호
    private String content; // 내용
}
