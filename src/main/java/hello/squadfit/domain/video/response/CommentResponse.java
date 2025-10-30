package hello.squadfit.domain.video.response;

import hello.squadfit.domain.video.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;
    private Long videoId;
    private LocalDateTime updateAt;
    private String content;
    private String trainerName;
    private Long trainerId;

    public static CommentResponse entityToResponse(Comment comment){

        return CommentResponse.builder()
                .commentId(comment.getId())
                .videoId(comment.getVideo().getId())
                .updateAt(comment.getUpdatedAt())
                .content(comment.getContent())
                .trainerName(comment.getTrainer().getName())
                .trainerId(comment.getTrainer().getId())
                .build();
    }

}
