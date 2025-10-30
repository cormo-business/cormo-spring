package hello.squadfit.fcm;// DeviceToken.java

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity @Getter @Setter
public class DeviceToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Column(length = 255, unique = true)
    private String token;

//    private String platform;   // android / ios
//    private String timezone;   // e.g., Asia/Seoul
    private Boolean active = true;

    private Instant lastSeenAt; // 앱이 마지막으로 Firebase 서버에 연결된 시점 -> 이게 오래되면 삭제 가능

    // 매일 특정 시각들(HH:mm)을 분 단위로 저장: 예) 09:00 -> 540
    @ElementCollection
    private List<Integer> minutesOfDay; // 0~1439

    // getters/setters ...
}
