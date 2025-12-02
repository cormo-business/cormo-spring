package hello.squadfit.domain.certification.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SmsVerifyRequest {
    
    @NotBlank
    private String phoneNumber;
    
    @NotBlank
    private String code; // 사용자가 입력한 인증번호
}
