package hello.squadfit.domain.video.entity;

import hello.squadfit.domain.member.entity.Trainer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity @Getter
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt; // 수정시간

    @Column(nullable = false)
    private String content;

    // == 연관관계 == //
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    // == 연관관계 편의 메서드 == //
    public void addVideo(Video video){
        this.video = video;
        video.getComments().add(this);
    }

    public void addTrainer(Trainer trainer){
        this.trainer = trainer;
        trainer.getComments().add(this);
    }

    // == 생성 메서드 == //
    public static Comment create(Video video, Trainer trainer, String content){
        Comment comment = new Comment();
        comment.content = content;

        comment.addTrainer(trainer);
        comment.addVideo(video);
        return comment;
    }

    // == 비즈니스 로직 == //


    // 코멘트 수정
    public Comment updateComment(String content){
        this.content = content;
        return this;
    }

}
