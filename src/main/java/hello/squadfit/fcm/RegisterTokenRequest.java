package hello.squadfit.fcm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RegisterTokenRequest(
        @NotBlank String userId,
        @NotBlank String token,
        @NotBlank String timezone,
//        @NotBlank String platform,
        @NotEmpty List<String> scheduleTimes // ["HH:mm", ...]
) {}