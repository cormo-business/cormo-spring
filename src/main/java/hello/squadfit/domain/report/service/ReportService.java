package hello.squadfit.domain.report.service;

import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.entity.Trainer;
import hello.squadfit.domain.member.service.MemberService;
import hello.squadfit.domain.member.service.TrainerService;
import hello.squadfit.domain.report.entity.Report;
import hello.squadfit.domain.report.entity.VideoReport;
import hello.squadfit.domain.report.repository.ReportRepository;
import hello.squadfit.domain.report.request.PublishReportRequest;
import hello.squadfit.domain.report.response.ReportResponse;
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
public class ReportService {


    private final MemberService memberService;
    private final VideoService videoService;
    private final TrainerService trainerService;
    private final ReportRepository reportRepository;

    /**
     * member 가 레포트 신청하기
     * Report 안채워진 상태로 껍질만 만들기
     * */
    @Transactional
    public Long createReport(Long trainerId, Long memberId, List<Long> videoIds){
        Member member = memberService.findOne(memberId);


        // 멤버 사용한 쿠폰 갯수 줄이기 + 쿠폰 검증하기
        member.requestReport();

        Trainer trainer = trainerService.findOne(trainerId);
        List<VideoReport> videoReports = new ArrayList<>();

        for (Long videoId : videoIds) {
            Video video = videoService.findOne(videoId);
            if(!video.getMember().getId().equals(member.getId())){
                throw new RuntimeException("이 비디오 주인 맞아?");
            }
            videoReports.add(VideoReport.create(video)) ;
        }

        Report report = Report.create(trainer, member, videoReports);
        reportRepository.save(report);



        return report.getId();
        // video 여러개 불러오기
        // video 이용해서 videoReport 만들기
        // videoReport 여러개 이용해서 report 만들기
        // report 저장하기
    }

    // 레포트 작성하기
    // todo: 레포트작성 방식 바꾸기 PDF로 할것인가?
    @Transactional
    public Long publishReport(Long reportId, PublishReportRequest request){
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("레포트 없는데?"));
        return report.publishReport(request.getContent(), request.getTitle());
    }

    // 레포트 조회하기
    public ReportResponse findById(Long reportId){
        // todo:멤버로 자신의 리포트인지 확인하는 작업

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("리포트가 없는데요?"));
        return ReportResponse.entityToRequest(report);
    }

    // 레포트 전제 조회하기(제목 정도만?)
    public Page<ReportResponse> findAllById(Long memberId, int page, int size){
        Member findMember = memberService.findOne(memberId);
        Page<Report> reports = reportRepository.findAllByMember(findMember, PageRequest.of(page, size));

        return reports.map((report -> ReportResponse.entityToRequest(report)));
    }

    // 레포트 삭제하기
    @Transactional
    public Long deleteReport(Long reportId){
        // todo:멤버로 자신의 리포트인지 확인하는 작업
        reportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("레포트 없음"));

        reportRepository.deleteById(reportId);

        return reportId;

    }

}
