package hello.squadfit.domain.certification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class SmsRepository {

    private final StringRedisTemplate redis;

    private final String PREFIX = "sms:";

    // sms 저장하기
    public void save(String sms, String CertificationNumber, Duration ttl){
        redis.opsForValue().set(PREFIX + sms, CertificationNumber, ttl);
    }

    // sms 삭제하기
    public void delete(String sms){
        redis.delete(PREFIX + sms);
    }

    // sms 조회하기
    public String find(String sms){
        System.out.println("[REDIS-FIND] key=" + PREFIX + ", value=" + sms);
        return redis.opsForValue().get(PREFIX + sms);
    }

}
