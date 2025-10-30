package hello.squadfit.domain.video.response;

import hello.squadfit.domain.video.entity.Video;
import lombok.*;

@Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VideoResponse {

    private Long videoId;
    private String title;
    private String saveKey;

    public static VideoResponse entityToResponse(Video video){
        VideoResponse videoResponse = new VideoResponse();
        videoResponse.videoId = video.getId();
        videoResponse.title = video.getTitle();
        videoResponse.saveKey = video.getSaveKey();

        return videoResponse;
    }

}
