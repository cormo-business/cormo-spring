package hello.squadfit.security.jwt;


import hello.squadfit.domain.member.Role;
import hello.squadfit.domain.member.entity.UserEntity;
import hello.squadfit.security.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    private static final String[] whitelist = {
            "/login","/","/api/member/register", "/api/trainer/register",
            "/swagger-ui/**", "/swagger-ui.html","/v3/api-docs/**", "/api/member/exists",
            "/turn/credentials", "/signal/offer", "/signal/candidate"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return PatternMatchUtils.simpleMatch(whitelist, request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("accessToken");

        // 토큰 없을 때
        if(token == null){
            filterChain.doFilter(request, response);

            return;
        }

//        String token = accessToken.split(" ")[1];

        // 토큰이 access인지 확인
        String category = jwtUtil.getCategory(token);
        if(!category.equals("access")){

            response.getWriter().print("access 토큰 아님");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        // 토큰 만료되었을 때
        try{
            jwtUtil.isExpired(token);
        }catch (ExpiredJwtException e){

            response.getWriter().print("access token 만료됨");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        // 정상 일 때
        String username = jwtUtil.getUsername(token);
        String nickName = jwtUtil.getNickName(token);
        Long userId = jwtUtil.getUserId(token);
        Role role = jwtUtil.getRole(token);

        UserEntity userData = UserEntity.createByJwt(username, role, userId);

        CustomUserDetails customUserDetails = new CustomUserDetails(userData);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }

}