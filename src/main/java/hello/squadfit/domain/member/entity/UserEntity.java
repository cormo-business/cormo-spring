package hello.squadfit.domain.member.entity;

import hello.squadfit.domain.member.Role;
import hello.squadfit.domain.member.dto.CreateUserDto;
import hello.squadfit.domain.member.request.ChangeUserRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username; // 전화번호로

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String birth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Role role;

    // == 연관관계 == //
    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(nullable = true, name = "member_id")
    private Member member;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(nullable = true, name = "trainer_id")
    private Trainer trainer;

    // == 연관관계 편의 메서드 == //
    public void addMember(Member member){
        this.member = member;
    }
    public void addTrainer(Trainer trainer){
        this.trainer = trainer;
    }
    public void addRole(Role role){this.role = role;}

    // == 생성 메서드 == //
    public static UserEntity create(CreateUserDto dto){
        UserEntity userEntity = new UserEntity();

        userEntity.username = dto.getUsername();
        userEntity.password = dto.getPassword();
        userEntity.birth = dto.getBirth();
        userEntity.gender = dto.getGender();

        return userEntity;
    }

    public static UserEntity createByJwt(String username, Role role, Long userId){
        UserEntity userEntity = new UserEntity();
        userEntity.id = userId;
        userEntity.username = username;
        userEntity.role = role;
        return userEntity;
    }

    /**
     * 정보 변경
     */
    public void changeProfile(ChangeUserRequest request){

        this.birth = request.getBirth();
    }

}
