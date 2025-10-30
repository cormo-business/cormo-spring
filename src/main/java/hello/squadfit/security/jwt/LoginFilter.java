package hello.squadfit.security.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import hello.squadfit.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;

import static hello.squadfit.security.jwt.JWTExpiredMs.accessExpiredMs;
import static hello.squadfit.security.jwt.JWTExpiredMs.refreshExpiredMs;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final JWTTokenRepository jwtTokenRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            ServletInputStream inputStream = request.getInputStream();
            LoginDTO loginDTO = objectMapper.readValue(inputStream, LoginDTO.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword(), null);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        // 유저 정보
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String role = userDetails.getRole().toString();
        String username = userDetails.getUsername();
        Long userId = userDetails.getUserId();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", username, role, accessExpiredMs, userId);
        String refresh = jwtUtil.createJwt("refresh", username, role, refreshExpiredMs, userId);

        // 응답 설정
//        response.setHeader("Authorization", "Bearer " + access);
        response.setHeader("accessToken", access);
        response.setHeader("refreshToken", refresh);
//        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        saveRefreshToken(username, refresh);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");

        response.setStatus(401);
    }

    // refresh 토큰 redis 저장하기
    private void saveRefreshToken(String username, String refresh) {

        jwtTokenRepository.save(username, refresh, Duration.ofMillis(refreshExpiredMs));

    }
}
