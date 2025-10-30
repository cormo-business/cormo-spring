package hello.squadfit.domain.video.service;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.service.MemberService;
import hello.squadfit.domain.record.entity.ExerciseRecord;
import hello.squadfit.domain.record.service.RecordService;
import hello.squadfit.domain.video.dto.SaveVideoDto;
import hello.squadfit.domain.video.entity.Video;
import hello.squadfit.domain.video.entity.VideoVisibility;
import hello.squadfit.domain.video.repository.VideoRepository;
import hello.squadfit.domain.video.request.SaveVideoRequest;
import hello.squadfit.domain.video.response.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VideoService {

    private final MemberService memberService;
    private final RecordService recordService;
    private final VideoRepository videoRepository;

    /**
     * 서버에서 영상 저장하기
     */
    public Long saveByServer(Long memberId, Long recordId, SaveVideoRequest saveVideoRequest){
        Member findMember = memberService.findOne(memberId);
        ExerciseRecord findRecord = recordService.getRecord(recordId).orElseThrow(() -> new RuntimeException("기록이 없어용"));

        if(!findRecord.getMember().equals(findMember)){
            throw new RuntimeException("당신 기록 맞아?!");
        }

        String key = UUID.randomUUID().toString();

        // todo: s3파일 업로드 로직 구성하기
//        // 2) 파일 검증
//        if (file.isEmpty()) throw new IllegalArgumentException("빈 파일");
//        if (!file.getContentType().startsWith("video/")) throw new IllegalArgumentException("비디오만 허용");
//
//        // 3) 저장 키 생성
//        String key = "videos/%s/%s".formatted(LocalDate.now(), UUID.randomUUID() + "_" + file.getOriginalFilename());
//
//        // 4) 스토리지 업로드 (S3 예시)
//        ObjectMetadata meta = new ObjectMetadata();
//        meta.setContentLength(file.getSize());
//        meta.setContentType(file.getContentType());
//        s3.putObject(new PutObjectRequest(bucket, key, file.getInputStream(), meta));

        Video video = Video.create(findMember, findRecord,
                SaveVideoDto.builder()
                        .title(saveVideoRequest.getTitle())
                        .key(key)
                        .build()
        );

        Video saveVideo = videoRepository.save(video);

        return saveVideo.getId();

    }

    // 코멘트 가능한 영상들 불러오기
    public List<VideoResponse> findCommentableVideo() {

        List<Video> videos = videoRepository.findByVisibility(VideoVisibility.PUBLIC);
        List<VideoResponse> list = videos.stream().map((video) -> VideoResponse.entityToResponse(video)).toList();

        return list;

    }

    public Video findOne(Long videoId) {

        return videoRepository.findById(videoId).orElseThrow(()-> new RuntimeException("비디오 없는데요?"));

    }



    /**
     * 클라이언트에서 영상 저장 후 링크만 받기
     * 1. 저장할 키와 URL 건네주기
     * 2. 클라이언트에서 영상 업로드 완료시 api 받기
     */
//    public void saveByClient(Long memberId, Long recordId, sveDto req){
//        Member findMember = memberService.findOne(memberId).orElseThrow(() -> new RuntimeException("멤버가 없어유"));
//        ExerciseRecord findRecord = recordService.getRecord(recordId).orElseThrow(() -> new RuntimeException("기록이 없어용"));
//
//        // 스토리지 키 생성
//        String key = keyFactory.newKey(req.getTitle(), "video/mp4"); // 예: videos/2025/08/uuid.mp4
//        String uploadUrl = presigner.presignPutUrl(key, req.getContentType());
//
//        // PENDING 비디오 미리 생성
//        Video video = Video.create(findMember, findRecord, req.getTitle(), key, req.getVisibility());
//        videoRepository.save(video);
//
//
////    }
//
//    @Getter
//    public static class sveDto{
//        private String title;
//        private VideoVisibility visibility;
//        private String contentType;
//    }

}
