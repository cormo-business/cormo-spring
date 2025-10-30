package hello.squadfit.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class JWTTokenRepository {

    private final StringRedisTemplate redis;

    private final String PREFIX = "jwt:";

    // JWT 저장하기
    public void save(String username, String token, Duration ttl){
        redis.opsForValue().set(PREFIX + username, token, ttl);
    }

    // JWT 삭제하기
    public void delete(String username){
        redis.delete(PREFIX + username);
    }

    // JWT 조회하기
    public String find(String username){
        return redis.opsForValue().get(PREFIX + username);
    }

    // JWT 존재하는지 확인하기
    public boolean matches(String username, String token) {
        String stored = redis.opsForValue().get(PREFIX + username);
        return java.util.Objects.equals(stored, token);
    }

    // 존재여부 확인
    public boolean exists(String username) {
        return Boolean.TRUE.equals(redis.hasKey(PREFIX + username));
    }
}
