package hello.squadfit.domain.member.controller;

import hello.squadfit.domain.PageResponse;
import hello.squadfit.domain.member.response.AttendanceResponse;
import hello.squadfit.domain.member.service.AttendanceService;
import hello.squadfit.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 출석하기
    @PostMapping
    public ResponseEntity<Long> save(@AuthenticationPrincipal CustomUserDetails userDetails){

        Long attendance = attendanceService.attendance(userDetails.getUserId());

        return ResponseEntity.ok(attendance);
    }

    // 출석 전체 조회하기
    @GetMapping
    public ResponseEntity<PageResponse<AttendanceResponse>> findByMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size
    ){

        Page<AttendanceResponse> responsePage = attendanceService.findAttendanceByMember(userDetails.getUserId(), page, size);
        PageResponse<AttendanceResponse> result = PageResponse.from(responsePage);

        return ResponseEntity.ok(result);
    }
}
