package hello.squadfit.domain.report.controller;

import hello.squadfit.domain.report.request.CreateReportRequest;
import hello.squadfit.domain.report.request.PublishReportRequest;
import hello.squadfit.domain.report.response.ReportResponse;
import hello.squadfit.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    // 레포트 신청하기
    @PostMapping("/{trainerId}/{memberId}")
    public ResponseEntity<Long> applyReport(
            @PathVariable("trainerId") Long trainerId,
            @PathVariable("memberId") Long memberId,
            @RequestBody CreateReportRequest createReportRequest){

        Long report = reportService.createReport(trainerId, memberId, createReportRequest.getVideoIds());

        return ResponseEntity.ok(report);
    }

    // 레포트 작성하기
    @PutMapping("/{reportId}")
    public ResponseEntity<Long> publishReport(
            @PathVariable("reportId") Long reportId,
            @RequestBody PublishReportRequest request
    ){
        Long report = reportService.publishReport(reportId, request);
        return ResponseEntity.ok(report);
    }

    // 레포트 조회하기
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponse> findOne(@PathVariable("reportId") Long reportId){
        ReportResponse result = reportService.findById(reportId);

        return ResponseEntity.ok(result);
    }

    // 레포트 전체 조회
    @GetMapping("/all/{memberId}")
    public ResponseEntity<Page<ReportResponse>> findAll(
            @PathVariable("memberId") Long memberId,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "0") int page

    ){
        Page<ReportResponse> result = reportService.findAllById(memberId, page, size);
        return ResponseEntity.ok(result);
    }

    // 레포트 삭제
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Long> delete(@PathVariable("reportId") Long reportId){

        Long report = reportService.deleteReport(reportId);

        return ResponseEntity.ok(report);

    }
}
