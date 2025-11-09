package hello.squadfit.fcm;// DeviceToken.java

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;



@Entity @Getter @Setter
@Table(name = "device_tokens")
public class DeviceToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(unique = true, nullable = false, length = 2048)
    private String token;

    private String platform = "android";

    private Boolean active = true;

    private LocalDateTime lastSeenAt = LocalDateTime.now();

    protected DeviceToken() {}

    public DeviceToken(Long userId, String token, String platform) {
        this.userId = userId;
        this.token = token;
        this.platform = platform != null ? platform : "android";
        this.active = true;
        this.lastSeenAt = LocalDateTime.now();
    }

}