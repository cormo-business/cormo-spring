package hello.squadfit.domain.certification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class EmailRepository {

    private final StringRedisTemplate redis;

    private final String PREFIX = "email:";

    // email 저장하기
    public void save(String email, String CertificationNumber, Duration ttl){
        redis.opsForValue().set(PREFIX + email, CertificationNumber, ttl);
    }

    // email 삭제하기
    public void delete(String email){
        redis.delete(PREFIX + email);
    }

    // email 조회하기
    public String find(String email){
        System.out.println("[REDIS-FIND] key=" + PREFIX + ", value=" + email);
        return redis.opsForValue().get(PREFIX + email);
    }

}
