package hello.squadfit.security;

import hello.squadfit.domain.member.entity.UserEntity;
import hello.squadfit.domain.member.repository.UserRepository;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserEntity> userData = userRepository.findByUsername(username);

        if(userData.isEmpty()){
            return null;
        }

        return new CustomUserDetails(userData.get());
    }
}
