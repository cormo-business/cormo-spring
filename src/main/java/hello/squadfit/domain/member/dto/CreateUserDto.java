package hello.squadfit.domain.member.dto;

import hello.squadfit.domain.member.request.CreateUserRequest;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateUserDto {

    private String username;
    private String password;
    private String birth;
    private String gender;

    public static <T extends CreateUserRequest> CreateUserDto from(T request) {

        return CreateUserDto.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .birth(request.getBirth())
                    .gender(request.getGender())
                    .build();
    }

    public CreateUserDto encodePassword(String encodePassword){
        return CreateUserDto.builder()
                    .username(this.username)
                    .password(encodePassword)
                    .birth(this.birth)
                    .gender(this.getGender())
                    .build();
    }
}