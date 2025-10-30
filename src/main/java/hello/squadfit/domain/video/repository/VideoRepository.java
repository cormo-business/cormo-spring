package hello.squadfit.domain.video.repository;

import hello.squadfit.domain.video.entity.Video;
import hello.squadfit.domain.video.entity.VideoVisibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByVisibility(VideoVisibility visibility);
}
