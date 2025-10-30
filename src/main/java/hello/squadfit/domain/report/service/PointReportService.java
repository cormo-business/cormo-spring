package hello.squadfit.domain.report.service;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.service.MemberService;
import hello.squadfit.domain.report.entity.PointReport;
import hello.squadfit.domain.report.entity.VideoPointReport;
import hello.squadfit.domain.report.repository.PointReportRepository;
import hello.squadfit.domain.report.request.ApplyPointRequest;
import hello.squadfit.domain.report.response.PointReportListResponse;
import hello.squadfit.domain.report.response.PointReportResponse;
import hello.squadfit.domain.video.entity.Video;
import hello.squadfit.domain.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointReportService {

    private final PointReportRepository pointReportRepository;
    private final MemberService memberService;
    private final VideoService videoService;

    // 포인트 레포트 신청
    @Transactional
    public Long requestPointReport(Long memberId, ApplyPointRequest applyPointRequest){

        Member findMember = memberService.findOne(memberId);
        findMember.requestPointReport();

        // VideoPointReport 매핑 테이블 만들기
        List<VideoPointReport> videoPointReports = new ArrayList<>();
        for (Long videoId : applyPointRequest.getVideoIds()) {
            Video video = videoService.findOne(videoId);
            if(!video.getMember().getId().equals(findMember.getId())){
                throw new RuntimeException("이 비디오 주인 맞아?");
            }
            videoPointReports.add(VideoPointReport.create(video)) ;
        }

        // PointReport 만들기
        PointReport pointReport = PointReport.create(findMember, applyPointRequest.getIsDetail(), videoPointReports);
        PointReport save = pointReportRepository.save(pointReport);
        return save.getId();

    }

    // 포인트 레포트 작성
    @Transactional
    public Long writePointReport(Long pointReportId){

        // todo: 멤버 확인하는 절차

        PointReport pointReport = findOne(pointReportId);
        if(pointReport.getIsDetail()){
            // todo: AI 이용해서 디테일한 응답받기
            //
            String title = "타이틀";
            String content = "컨텐츠";
            PointReport write = pointReport.write(title, content);
            return write.getId();
        }else{
            // todo: AI 이용해서 그냥 응답 받기

            String title = "디테일 타이틀";
            String content = "디테일 컨텐츠";
            PointReport write = pointReport.write(title, content);
            return write.getId();
        }

    }

    // 레포트 전체 조회
    public Page<PointReportListResponse> findAllPointReport(Long memberId, int size, int page){
        Member findMember = memberService.findOne(memberId);

        Page<PointReport> pointReports = pointReportRepository.findByMember(findMember, PageRequest.of(page, size));
        Page<PointReportListResponse> result = pointReports.map((pointReport -> PointReportListResponse.entityToResponse(pointReport)));
        return result;

    }

    // 레포트 디테일 조회
    public PointReportResponse findDetailPointReport(Long pointReportId){
        // todo:member 확인하기
        PointReport findPointReport = findOne(pointReportId);
        PointReportResponse pointReportResponse = PointReportResponse.entityToResponse(findPointReport);

        return pointReportResponse;

    }

    @Transactional
    // 레포트 삭제
    public void deletePointReport(Long pointReportId){
        PointReport findPointReport = findOne(pointReportId);

        pointReportRepository.delete(findPointReport);
    
    }

    // 포인트 레포트 찾기
    public PointReport findOne(Long pointReportId){
        return pointReportRepository.findById(pointReportId).orElseThrow(() -> new RuntimeException("포인트 레포트 없습니당?"));
    }
}
