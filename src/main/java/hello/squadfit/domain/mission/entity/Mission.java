package hello.squadfit.domain.mission.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;
    private String content;
    private String title;

    public static Mission create(String content, String title){
        Mission mission = new Mission();
        mission.content = content;
        mission.title = title;

        return mission;
    }

}
