package hello.squadfit.domain.video.controller;

import hello.squadfit.domain.video.request.SaveVideoRequest;
import hello.squadfit.domain.video.response.VideoResponse;
import hello.squadfit.domain.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class VideoController {

    private final VideoService videoService;

    // 비디오 서버에서 저장하기
    @PostMapping("/{memberId}/{recordId}")
    public ResponseEntity<Long> saveByServer(
            @PathVariable("memberId") Long memberId,
            @PathVariable("recordId") Long recordId,
            @RequestBody SaveVideoRequest request
    ){
        Long result = videoService.saveByServer(memberId, recordId, request);

        return ResponseEntity.ok(result);
    }

    // 코멘트 가능한 영상들 불러오기
    @GetMapping("/comment")
    public ResponseEntity<?> findCommentableVideo(){

        List<VideoResponse> commentableVideo = videoService.findCommentableVideo();
        return ResponseEntity.ok(commentableVideo);
    }


}
