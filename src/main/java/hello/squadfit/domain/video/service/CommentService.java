package hello.squadfit.domain.video.service;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.entity.Trainer;
import hello.squadfit.domain.member.service.MemberService;
import hello.squadfit.domain.member.service.TrainerService;
import hello.squadfit.domain.video.entity.Comment;
import hello.squadfit.domain.video.entity.Video;
import hello.squadfit.domain.video.entity.VideoVisibility;
import hello.squadfit.domain.video.repository.CommentRepository;
import hello.squadfit.domain.video.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final VideoService videoService;
    private final TrainerService trainerService;
    private final MemberService memberService;

    // 코멘트 요청하기
    @Transactional
    public void requestComment(Long memberId, Long videoId) {
        Member findMember = memberService.findOne(memberId);
        Video video = videoService.findOne(videoId);

        if(!video.getMember().getId().equals(findMember.getId())){
            throw new RuntimeException("당신 이 비디오 주인 맞아?");
        }
        if(video.getVisibility().equals(VideoVisibility.PUBLIC)){
            throw new RuntimeException("이미 요청했는데?");
        }
        video.requestComment(findMember);


    }
    
    // todo: 코멘트 취소하기

    // 코멘트 작성하기
    @Transactional
    public Long createComment(Long videoId, Long trainerId, String content){

        Video video = videoService.findOne(videoId);
        Trainer trainer = trainerService.findOne(trainerId);

        if(video.getVisibility().equals(VideoVisibility.PRIVATE)){
            throw new RuntimeException("코멘트 신청한걸 작성하세요");
        }

        Comment.create(video, trainer, content );

        return video.getId();
    }

    // 코멘트 변경하기
    @Transactional
    public Comment updateComment(Long commentId, String content){
        // todo: 트레이너 맞닌지 확인

        Comment comment = findOne(commentId);
        return comment.updateComment(content);

    }

    // 코멘트 삭제하기
    @Transactional
    public Long deleteComment(Long commentId){
        // todo: 트레이너 맞는지 확인

        Comment comment = findOne(commentId);
        commentRepository.delete(comment);

        return commentId;
    }

    public Page<CommentResponse> findByVideo(Long videoId, int size, int page) {

        Video findVideo = videoService.findOne(videoId);

        Page<Comment> comments = commentRepository.findByVideo(findVideo, PageRequest.of(page, size));

        return comments.map(comment -> CommentResponse.entityToResponse(comment));

    }

    // 비디오 코멘트 불러오기
    public Page<CommentResponse> findByTrainer(Long trainerId, int size, int page) {

        Trainer findTrainer = trainerService.findOne(trainerId);

        Page<Comment> comments = commentRepository.findByTrainer(findTrainer, PageRequest.of(page, size));
        return comments.map(comment -> CommentResponse.entityToResponse(comment));

    }
    // 트레이너의 코멘트 불러오기

    public Comment findOne(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("코멘트 없는데유?"));
    }

}
