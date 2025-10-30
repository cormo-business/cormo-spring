package hello.squadfit.security.reissue;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reissue")
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping
    public ResponseEntity<ReissueResponse> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ){

        ReissueResponse result = reissueService.reissue(request, response);

        return ResponseEntity
                .status(result.getStatus())
                .body(result);
    }

}