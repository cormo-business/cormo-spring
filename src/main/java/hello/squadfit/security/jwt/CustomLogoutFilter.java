package hello.squadfit.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final JWTTokenRepository repository;

    public CustomLogoutFilter(JWTUtil jwtUtil, JWTTokenRepository repository) {
        this.jwtUtil = jwtUtil;
        this.repository = repository;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 로그아웃 요청인지 확인하는 절차
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 획득
        String refresh = request.getHeader("refreshToken");

        // 토큰 없을 때
        if(refresh == null){
            response.getWriter().print("refresh 토큰 없음");
            filterChain.doFilter(request, response);

            return;
        }

        // 토큰이 refresh 인지 확인 (카테고리 이용)
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {

            response.getWriter().print("refresh 토큰 아님");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            response.getWriter().print("refresh token 만료됨");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //redis에 저장되어 있는지 확인
        String username = jwtUtil.getUsername(refresh);
        boolean isExist = repository.matches(username, refresh);
        
        if (!isExist) {

            //response status code
            response.getWriter().print("refresh 토큰 없음");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행
        //Refresh 토큰 redis에서 제거
        repository.delete(refresh);

        response.setStatus(HttpServletResponse.SC_OK);
    }
}