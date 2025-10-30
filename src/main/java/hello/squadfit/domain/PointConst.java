package hello.squadfit.domain;

public interface PointConst {
    Integer ATTENDANCE_POINT = 5; // 출석 시 증가하는 포인트
    Integer MISSION_POINT = 10; // 미션 성공 시 증가하는 포인트
    Integer EXERCISE_POINT = 30; // 운동 시 증가하는 포인트
    Integer COMMENT_POINT = 100; // 코멘트 달 때 필요한 포인트
    Integer REPORT_POINT = 5000;
    Integer DETAIL_REPORT_POINT = 10000;
    Integer LEVEL_UP_POINT = 50; // 레벨업 시 증가하는 필요한 경험치 양
    Integer INITIAL_EXPERIENCE = 100; // 1레벨 시 필요한 경험치
}

