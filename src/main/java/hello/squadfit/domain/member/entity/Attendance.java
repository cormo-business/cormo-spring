package hello.squadfit.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 출석 정보
public class Attendance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    LocalDateTime attendanceTime;

    // == 연관관계 == //
    @ManyToOne(fetch = LAZY)
    Member member;

    // == 연관관계 편의 메서드 == //
    public void addAttendance(Member member){
        this.member = member;
        member.getAttendances().add(this);
    }

    // == 생성 메서드 == //
    public static Attendance create(Member member){
        Attendance attendance = new Attendance();
        attendance.addAttendance(member);
        attendance.attendanceTime = LocalDateTime.now();

        return attendance;
    }




}
