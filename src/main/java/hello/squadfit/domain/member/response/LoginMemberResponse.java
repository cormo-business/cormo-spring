package hello.squadfit.domain.member.response;

import lombok.*;

@Getter
//@NoArgsConstructor
@Builder @AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginMemberResponse {

    private String nickName;
    private Integer level;
    private Integer requiredExperience; // 잔여 경험치
    private Integer point;
    private Integer availableReportCount; // 레포트 신청 가능한 숫자

}
