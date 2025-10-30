package hello.squadfit.security.reissue;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class ReissueResponse {

    private HttpStatus status;
    private String message;

}