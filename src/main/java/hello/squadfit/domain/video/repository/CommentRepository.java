package hello.squadfit.domain.video.repository;

import hello.squadfit.domain.member.entity.Trainer;
import hello.squadfit.domain.video.entity.Comment;
import hello.squadfit.domain.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByVideo(Video findVideo, PageRequest of);

    Page<Comment> findByTrainer(Trainer findTrainer, PageRequest of);
}
