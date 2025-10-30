package hello.squadfit.domain.member.controller;

import hello.squadfit.domain.PageResponse;
import hello.squadfit.domain.member.entity.Trainer;
import hello.squadfit.domain.member.request.CreateTrainerRequest;
import hello.squadfit.domain.member.response.TrainerInfoResponse;
import hello.squadfit.domain.member.service.TrainerService;
import hello.squadfit.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/trainer")
public class TrainerController {

    // todo: 트레이너 부분은 나중에 추가 테스트하기
    private final TrainerService trainerService;

    // 트레이너 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CreateTrainerRequest request, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            log.info("회원가입 오류 = {}", bindingResult);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(bindingResult.toString());
        }
        Long memberId = trainerService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberId);

    }

    /**
     * 트레이너가 작성한 코멘트 리스트 받기
     * todo: 코멘트 작성시 해야할듯?
     */
//    @GetMapping("/comments")
//    public ResponseEntity<?> getAllComments(){
//
//    }

    /**
     * 트레이너 관련 정보 받기 todo: 트레이너 정보 추가적으로 어떤거 넣을지 의논하기
     */
    @GetMapping("/info")
    public ResponseEntity<TrainerInfoResponse> getInfo(@AuthenticationPrincipal CustomUserDetails userDetails){

        TrainerInfoResponse result = trainerService.findTrainerInfo(userDetails.getUserId());

        return ResponseEntity.ok(result);

    }

    /**
     * 이름으로 트레이너 찾기 todo: 동적쿼리 이용해서 장소나 다른 정보로도 찾을수 있도록 하기
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<TrainerInfoResponse>> findTrainerByName(
            @RequestParam("name") String name,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(defaultValue = "0", name = "page") int page
    ){

        Page<TrainerInfoResponse> trainerInfoPage = trainerService.findAllByName(name, size, page);
        PageResponse<TrainerInfoResponse> result = PageResponse.from(trainerInfoPage);

        return ResponseEntity.ok(result);
    }
}
