package hello.squadfit.domain.member.service;

import hello.squadfit.domain.member.dto.CreateUserDto;
import hello.squadfit.domain.member.entity.UserEntity;
import hello.squadfit.domain.member.repository.UserRepository;
import hello.squadfit.domain.member.request.ChangeUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    // 회원가입 -> 멤버, 트레이너 서비스단으로 이동
    public UserEntity join(CreateUserDto dto){

        CreateUserDto userDto = dto.encodePassword(bCryptPasswordEncoder.encode(dto.getPassword()));

        UserEntity userEntity = UserEntity.create(userDto);
        return userRepository.save(userEntity);
    }

    @Transactional
    // 정보 변경
    public Long changeInfo(ChangeUserRequest request, Long userId){
        UserEntity findUser = findOne(userId);
        findUser.changeProfile(request);
        return findUser.getId();
    }
    
    // 비밀번호 찾기(초기화)

    // 로그인?

    public UserEntity findOne(Long userId){
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 없음"));
    }
}
