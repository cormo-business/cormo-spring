package hello.squadfit.domain.member.request;

public record CheckedEmailRequest(
        String email,
        String code
) {
}
