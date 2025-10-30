package hello.squadfit.security.jwt;

public interface JWTExpiredMs {

    long refreshExpiredMs = 24 * 6 * 10 * 60 * 1000L; // 24시간
    long accessExpiredMs = 10 * 60 * 1000L; // 10분

}
