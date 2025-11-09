package hello.squadfit.fcm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findAllByActiveTrue();
    List<DeviceToken> findAllByUserIdAndActiveTrue(Long userId);
    List<DeviceToken> findAllByUserIdNotAndActiveTrue(Long userId);
    Optional<DeviceToken> findByToken(String token);

    List<DeviceToken> findAllByTokenIn(List<String> toDeactivate);
}