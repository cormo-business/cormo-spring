package hello.squadfit.domain.mission.controller;

import hello.squadfit.domain.mission.request.CreateMissionRequest;
import hello.squadfit.domain.mission.response.MissionResponse;
import hello.squadfit.domain.mission.service.MissionService;
import hello.squadfit.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission")
public class MissionController {

    private final MissionService missionService;

    // 미션 성공
    @PutMapping
    public ResponseEntity<Long> successMission(@AuthenticationPrincipal CustomUserDetails userDetails){

        Long successMission = missionService.successMission(userDetails.getUserId());

        return ResponseEntity.ok(successMission);

    }

    // 미션 받아오기
    @GetMapping("/{memberId}")
    public ResponseEntity<MissionResponse> getMission(@PathVariable("memberId") Long memberId){
        MissionResponse mission = missionService.mission(memberId);

        return ResponseEntity.ok(mission);
    }

    // 미션 만들기
    @PostMapping
    public ResponseEntity<Long> createMission(CreateMissionRequest request){

        Long missionId = missionService.createMission(request);

        return ResponseEntity.ok(missionId);

    }

    // 미션 확인
    @GetMapping("/{missionId}")
    public ResponseEntity<MissionResponse> findMission(@PathVariable("missionId") Long missionId){
        MissionResponse result = missionService.findMissionById(missionId);

        return ResponseEntity.ok(result);
    }

}
