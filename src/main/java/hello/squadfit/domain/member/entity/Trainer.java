package hello.squadfit.domain.member.entity;

import hello.squadfit.domain.member.Role;
import hello.squadfit.domain.member.dto.CreateTrainerDto;
import hello.squadfit.domain.member.request.ChangeTrainerRequest;
import hello.squadfit.domain.report.entity.Report;
import hello.squadfit.domain.video.entity.Comment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

@Entity @Getter
@Table(name = "trainer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trainer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_id")
    private Long id;

//    @Embedded
//    private MemberProfile profile;

    @Column
    private String place; // 체육관 장소

    private String name;
//    @Column(nullable = true)
//    @Enumerated(EnumType.STRING)
//    private Role role;


    // == 연관관계 == //
    @OneToMany(mappedBy = "trainer", cascade = ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "trainer", cascade = ALL)
    private List<Report> reports;

    @OneToOne(fetch = LAZY, mappedBy = "trainer")
    private UserEntity userEntity;

    // == 연관관계 편의 메서드 == //
    private void addUser(UserEntity userEntity) {
        this.userEntity = userEntity;
        userEntity.addTrainer(this);
        userEntity.addRole(Role.Trainer);
    }


    // == 생성 메서드 == //
    public static Trainer create(CreateTrainerDto dto, UserEntity userEntity){
        Trainer trainer = new Trainer();
        trainer.place = dto.getPlace();
        trainer.name = dto.getName();

        trainer.addUser(userEntity);

        return trainer;
    }


    // == 비즈니스로직 == //
    /**
     * 프로필 변경
     */
    public void changeInfo(ChangeTrainerRequest request){
        this.place = request.getPlace();
    }

}
