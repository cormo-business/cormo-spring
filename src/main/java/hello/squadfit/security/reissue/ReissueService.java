package hello.squadfit.security.reissue;

import hello.squadfit.security.jwt.JWTTokenRepository;
import hello.squadfit.security.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static hello.squadfit.security.jwt.JWTExpiredMs.refreshExpiredMs;

@Slf4j
@Service
@Transactional
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final JWTTokenRepository jwtTokenRepository;

    public ReissueService(JWTUtil jwtUtil, JWTTokenRepository jwtTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.jwtTokenRepository = jwtTokenRepository;
    }

    public ReissueResponse reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 refresh 추출
        // refresh 획득
        String refresh = request.getHeader("refreshToken");


        // refresh null 이라면
        if (refresh == null) {
            return ReissueResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("refresh token null")
                    .build();
        }

        // 만료 검증
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return ReissueResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("refresh token expired")
                    .build();
        }

        // 카테고리 확인
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            return ReissueResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("invalid refresh token")
                    .build();
        }

        // 사용자 정보 추출
        String role = jwtUtil.getRole(refresh).toString();
        Long userId = jwtUtil.getUserId(refresh);
        String username = jwtUtil.getUsername(refresh);

        log.info("username = {}", username);
        log.info("refresh = {}", refresh);

        //redis에 저장되어 있는지 확인
        boolean isExist = jwtTokenRepository.matches(username, refresh);

        if (!isExist) {

            return ReissueResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("DB invalid refresh token")
                    .build();
        }
        // 새 access, refresh 발급
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L, userId);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L, userId);


        // 응답 설정
        response.setHeader("accessToken", newAccess);
        response.setHeader("refreshToken", newRefresh);
        response.setStatus(HttpStatus.OK.value());

        // Refresh 토큰 저장 DB에 기존 토큰 삭제 후 새로운 토큰 저장하기
        saveRefreshToken(username, newRefresh);

        return ReissueResponse.builder()
                .status(HttpStatus.OK)
                .message("재발급 성공")
                .build();
    }

    // refresh 토큰 redis 저장하기
    private void saveRefreshToken(String username, String refresh) {

        jwtTokenRepository.save(username, refresh, Duration.ofMillis(refreshExpiredMs));

    }

}