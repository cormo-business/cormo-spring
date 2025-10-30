package hello.squadfit.domain.report.controller;

import hello.squadfit.domain.report.request.ApplyPointRequest;
import hello.squadfit.domain.report.response.PointReportListResponse;
import hello.squadfit.domain.report.response.PointReportResponse;
import hello.squadfit.domain.report.service.PointReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point-report")
public class PointReportController {

    private final PointReportService pointReportService;

    // 포인트 레포트 신청하기
    @PostMapping("/{memberId}")
    public ResponseEntity<Long> applyPointReport(
            @PathVariable("memberId") Long memberId,
            @RequestBody ApplyPointRequest applyPointRequest
    ){
        Long pointReportId = pointReportService.requestPointReport(memberId, applyPointRequest);
        
        return ResponseEntity.ok(pointReportId);
    }
    
    // 포인트 레포트 작성하기
    @PutMapping("/{pointReportId}")
    public ResponseEntity<Long> writePointReport(@PathVariable(name = "pointReportId") Long pointReportId){
        Long result = pointReportService.writePointReport(pointReportId);

        return ResponseEntity.ok(result);
    }

    // 포인트 레포트 전체 조회하기
    @GetMapping("/{memberId}")
    public ResponseEntity<Page<PointReportListResponse>> findAllPointReports(
            @PathVariable(name = "memberId") Long memberId,
            @RequestParam(name = "size") int size,
            @RequestParam(name = "page") int page
    ){
        Page<PointReportListResponse> result = pointReportService.findAllPointReport(memberId, size, page);
        return ResponseEntity.ok(result);
    }

    // 포인트 레포트 상세 조회하기
    @GetMapping("/{pointReportId}")
    public ResponseEntity<PointReportResponse> findDetailPointReport(
            @PathVariable(name = "pointReportId") Long pointReportId
    ){
        PointReportResponse result = pointReportService.findDetailPointReport(pointReportId);

        return ResponseEntity.ok(result);
    }

    // 포인트 레포트 삭제하기
    @DeleteMapping("/{pointReportId}")
    public ResponseEntity<String> deletePointReport(@PathVariable(name = "pointReportId") Long pointReportId){

        pointReportService.deletePointReport(pointReportId);

        return ResponseEntity.ok("ok");

    }
    

}
