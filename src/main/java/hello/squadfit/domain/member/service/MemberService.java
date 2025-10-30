package hello.squadfit.domain.member.service;

import hello.squadfit.domain.member.dto.CreateMemberDto;
import hello.squadfit.domain.member.dto.CreateUserDto;
import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.entity.UserEntity;
import hello.squadfit.domain.member.repository.MemberRepository;
import hello.squadfit.domain.member.repository.UserRepository;
import hello.squadfit.domain.member.request.ChangeMemberRequest;
import hello.squadfit.domain.member.request.CreateMemberRequest;
import hello.squadfit.security.jwt.JWTTokenRepository;
import hello.squadfit.security.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

import static hello.squadfit.security.jwt.JWTExpiredMs.accessExpiredMs;
import static hello.squadfit.security.jwt.JWTExpiredMs.refreshExpiredMs;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final JWTTokenRepository jwtTokenRepository;

    @Transactional
    public Long join(CreateMemberRequest request, HttpServletResponse response){

        if(existsMemberByUsername(request.getUsername())){
            throw new IllegalStateException("이미 가입되어있는 아이디입니다.");
        }

        // user 만들기
        UserEntity userEntity = userService.join(CreateUserDto.from(request));
        
        // member 만들기
        Member member = Member.create(CreateMemberDto.from(request), userEntity);
        Member save = memberRepository.save(member);

        // 토큰 생성
        String access = jwtUtil.createJwt("access", userEntity.getUsername(), userEntity.getRole().toString(), accessExpiredMs, userEntity.getId());
        String refresh = jwtUtil.createJwt("refresh", userEntity.getUsername(), userEntity.getRole().toString(), refreshExpiredMs, userEntity.getId());

        // 응답 설정
        response.setHeader("accessToken", access);
        response.setHeader("refreshToken", refresh);
        response.setStatus(HttpStatus.OK.value());

        saveRefreshToken(userEntity.getUsername(), refresh);

        return save.getId();
    }

    @Transactional
    // 유저 정보 변경하기
    public Long changeMemberInfo(ChangeMemberRequest request, Long userId) {
        Member member = memberRepository.findByUserEntity_Id(userId).orElseThrow(() -> new RuntimeException("해당하는 멤버가 없습니다"));

        return member.changeInfo(request);
    }

    // refresh 토큰 redis 저장하기
    private void saveRefreshToken(String username, String refresh) {

        jwtTokenRepository.save(username, refresh, Duration.ofMillis(refreshExpiredMs));

    }

    // memberId 존재하는지 확인
    public boolean existsMemberByMemberId(Long memberId){
        return memberRepository.existsMemberById(memberId);
    }

    // username 유저 존재하는지 확인하기
    public boolean existsMemberByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public Member findOne(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("멤버 없는데유?"));
    }

    public Member findOneByUserId(Long userId){
        return memberRepository.findByUserEntity_Id(userId).orElseThrow(() -> new RuntimeException("유저 없는데유?"));
    }

}
