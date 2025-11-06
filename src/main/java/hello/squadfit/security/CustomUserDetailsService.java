package hello.squadfit.security;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.entity.UserEntity;
import hello.squadfit.domain.member.repository.MemberRepository;
import hello.squadfit.domain.member.repository.UserRepository;
import hello.squadfit.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserEntity> userData = userRepository.findByUsername(username);

        if(userData.isEmpty()){
            return null;
        }
        Member member = memberService.findOneByUserId(userData.get().getId());

        return new CustomUserDetails(userData.get(), member.getNickName());
    }
}
