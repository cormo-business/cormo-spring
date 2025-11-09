package hello.squadfit.fcm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterReq {
    @NotNull
    public Long userId;
    @NotBlank
    public String token;
    public String platform = "android";
}