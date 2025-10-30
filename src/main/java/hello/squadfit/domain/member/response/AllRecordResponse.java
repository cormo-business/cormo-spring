package hello.squadfit.domain.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AllRecordResponse {

    List<SingleRecordResponse> recordResponseList;

}
