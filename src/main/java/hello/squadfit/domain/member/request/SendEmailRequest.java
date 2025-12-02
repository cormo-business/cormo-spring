package hello.squadfit.domain.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendEmailRequest(
        @NotBlank
        @Email
        String email
){ }
