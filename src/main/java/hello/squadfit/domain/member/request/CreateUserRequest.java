package hello.squadfit.domain.member.request;

import lombok.Getter;

// abstract(추상클래스) = 직접 인스턴스를 만들 수 없는 클래스
@Getter
public abstract class CreateUserRequest {

    String username;
    String password;
    String birth;
    String phone;
    String name;
    String gender;

}
