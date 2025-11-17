package hello.squadfit.domain.member.response;

import hello.squadfit.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HomeInitResponse {

    private String nickname;
    private Integer level;
    private Integer point;
    private Integer requiredExperience;
    private int size;
    private Boolean checkAttendance;


    public static HomeInitResponse from(Member member, Boolean checkAttendance) {
        HomeInitResponse homeInitResponse = new HomeInitResponse();
        homeInitResponse.nickname = member.getNickName();
        homeInitResponse.level = member.getLevel();
        homeInitResponse.point = member.getPoint();
        homeInitResponse.requiredExperience = member.getRequiredExperience();
        homeInitResponse.size = member.getAttendances().size();
        homeInitResponse.checkAttendance = checkAttendance;
        return homeInitResponse;
    }
}
