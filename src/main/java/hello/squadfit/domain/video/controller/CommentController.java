package hello.squadfit.domain.video.controller;

import hello.squadfit.domain.video.entity.Comment;
import hello.squadfit.domain.video.request.CommentRequest;
import hello.squadfit.domain.video.response.CommentResponse;
import hello.squadfit.domain.video.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    // 코멘트 요청하기
    @PostMapping("/member/{memberId}/{videoId}")
    public ResponseEntity<Long> requestComment(@PathVariable("memberId") Long memberId, @PathVariable("videoId") Long videoId){
        commentService.requestComment(memberId, videoId);
        return ResponseEntity.ok(videoId);
    }

    // 코멘트 작성하기
    @PostMapping("/trainer/{videoId}/{trainerId}")
    public ResponseEntity<Long> createComment(
            @PathVariable("videoId") Long videoId,
            @PathVariable("trainerId") Long trainerId,
            @RequestBody CommentRequest request
    ){
        Long video = commentService.createComment(videoId, trainerId, request.getContent());

        return ResponseEntity.ok(video);

    }

    // 코멘트 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentRequest request
    ){

        Comment comment = commentService.updateComment(commentId, request.getContent());
        return ResponseEntity.ok(comment.getContent());
    }

    // 코멘트 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Long> deleteComment(@PathVariable("commentId") Long commentId){
        Long comment = commentService.deleteComment(commentId);

        return ResponseEntity.ok(comment);
    }

    // 비디오 코멘트 불러오기
    @GetMapping("/video/{videoId}")
    public ResponseEntity<Page<CommentResponse>> findCommentByVideo(
            @PathVariable("videoId") Long videoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<CommentResponse> result = commentService.findByVideo(videoId, size, page);

        return ResponseEntity.ok(result);

    }

    // 트레이너의 코멘트 불러오기
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<Page<CommentResponse>> findCommentByTrainer(
            @PathVariable("trainerId") Long trainerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<CommentResponse> result = commentService.findByTrainer(trainerId, size, page);

        return ResponseEntity.ok(result);
    }

}
