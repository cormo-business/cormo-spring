package hello.squadfit.domain.certification.request;

public record CheckedEmailRequest(
        String email,
        String code
) {
}
