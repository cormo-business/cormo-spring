package hello.squadfit.domain.member.service;

import hello.squadfit.domain.member.dto.CreateTrainerDto;
import hello.squadfit.domain.member.dto.CreateUserDto;
import hello.squadfit.domain.member.entity.Trainer;
import hello.squadfit.domain.member.entity.UserEntity;
import hello.squadfit.domain.member.repository.TrainerRepository;
import hello.squadfit.domain.member.repository.UserRepository;
import hello.squadfit.domain.member.request.CreateTrainerRequest;
import hello.squadfit.domain.member.response.TrainerInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public Long join(CreateTrainerRequest request){

        if(trainerExist(request.getUsername())){
            throw new IllegalStateException("이미 가입되어있는 아이디입니다.");
        }

        // 유저 만들기
        UserEntity userEntity = userService.join(CreateUserDto.from(request));

        // 트레이너 만들기
        Trainer trainer = Trainer.create(CreateTrainerDto.from(request), userEntity);
        trainerRepository.save(trainer);

        return trainer.getId();
    }

    public TrainerInfoResponse findTrainerInfo(Long userId){

        Trainer trainer = findByUserId(userId);
        return new TrainerInfoResponse(trainer.getUserEntity().getUsername(), trainer.getPlace(), trainer.getName());
    }

    // 트레이너 관련 정보 받기 todo: 트레이너 정보 추가적으로 어떤거 넣을지 의논하기
    public Trainer findOne(Long trainerId){
        return trainerRepository.findById(trainerId).orElseThrow(() -> new RuntimeException("트레이너 없는데요?"));
    }
    // 트레이너 프로필 변경 todo: 트레이너 정보 추가될테니까 나중에하기


    // 이름으로 트레이너 찾기
    public Page<TrainerInfoResponse> findAllByName(String trainerName, int size, int page) {
        Page<Trainer> trainerPage = trainerRepository.findAllByName(trainerName, PageRequest.of(size, page));
        return trainerPage.map((trainer -> TrainerInfoResponse.from(trainer)));
    }

    public Trainer findByUserId(Long userId){
        return trainerRepository.findOneByUserEntity_Id(userId);
    }


    // 아이디 이용해서 트레이너 기존에 존재하는지 확인하는 메서드
    private boolean trainerExist(String username) {
        return userRepository.existsByUsername(username);
    }

}
