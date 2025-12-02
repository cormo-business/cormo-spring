package hello.squadfit.domain.certification.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SmsSendRequest {

    @NotBlank
    private String phoneNumber;  // 수신자 번호
}
