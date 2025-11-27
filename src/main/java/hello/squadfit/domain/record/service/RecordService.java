package hello.squadfit.domain.record.service;

import hello.squadfit.domain.member.response.AllRecordResponse;
import hello.squadfit.domain.member.response.SingleRecordResponse;
import hello.squadfit.domain.member.response.mapper.SingleRecordResponseMapper;
import hello.squadfit.domain.member.service.MemberService;
import hello.squadfit.domain.record.controller.RecordController;
import hello.squadfit.domain.record.dto.CreateRecordDto;
import hello.squadfit.domain.record.entity.ExerciseType;
import hello.squadfit.domain.record.entity.ExerciseRecord;
import hello.squadfit.domain.record.repository.ExerciseTypeRepository;
import hello.squadfit.domain.record.repository.RecordRepository;
import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.repository.MemberRepository;
import hello.squadfit.domain.record.request.SaveRecordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;
    private final MemberService memberService;

    // 기록 저장하기
    @Transactional
    public Long save(RecordController.SaveRequest request, Long userId) {

        // 유효성 검사
        Member member = memberService.findOneByUserId(userId);

        ExerciseType exerciseType = exerciseTypeRepository.findByName(request.exerciseName())
                .orElseThrow(() -> new IllegalStateException("종목 없는데?"));

        // DTO 변경
        CreateRecordDto createRecordDto = CreateRecordDto.builder()
                .member(member)
                .repeat(10)
                .weight(0)
                .successNumber(10)
                .failNumber(0)
                .exerciseType(exerciseType)
                .build();

        ExerciseRecord exerciseRecord = ExerciseRecord.createRecord(createRecordDto);

        ExerciseRecord record = recordRepository.save(exerciseRecord);

        // 포인트 증가
        member.increaseExercisePoint();

        return record.getId();
    }

    // 기록 저장하기
    @Transactional
    public Long save(SaveRecordRequest request){

        // 유효성 검사
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalStateException("유저 맞아?"));

        ExerciseType exerciseType = exerciseTypeRepository.findById(request.getExerciseTypeId())
                .orElseThrow(() -> new IllegalStateException("유저 종목 없는데?"));

        // DTO 변경
        CreateRecordDto createRecordDto = CreateRecordDto.builder()
                .member(member)
                .repeat(request.getRepeatNumber())
                .weight(request.getWeight())
                .successNumber(request.getSuccessNumber())
                .failNumber(request.getFailNumber())
                .exerciseType(exerciseType)
                .build();

        ExerciseRecord exerciseRecord = ExerciseRecord.createRecord(createRecordDto);

        ExerciseRecord record = recordRepository.save(exerciseRecord);

        // 포인트 증가
        member.increaseExercisePoint();

        return record.getId();
    }

    // 전체 기록 조회
    // TODO: 페이징 처리하기
    public AllRecordResponse findAll(Long memberId) {
        List<ExerciseRecord> all = recordRepository.findAllByMemberId(memberId);

        List<SingleRecordResponse> list = all.stream()
                .map(record -> SingleRecordResponseMapper.entityToDto(record)).toList();

        return new AllRecordResponse(list);
    }

    // 단일 기록 조회
    public SingleRecordResponse findByMemberIdAndRecordId(Long memberId, Long recordId) {
        ExerciseRecord findRecord = recordRepository.findByMemberIdAndId(memberId, recordId).orElseThrow(() -> new RuntimeException("기록이 없는데요?"));

        SingleRecordResponse result = SingleRecordResponseMapper.entityToDto(findRecord);

        return result;
    }

    public Optional<ExerciseRecord> getRecord(Long recordId){
        return recordRepository.findById(recordId);
    }

    // 기록 삭제
    @Transactional
    public Long remove(Long exerciseId) {
        recordRepository.deleteById(exerciseId);
        return exerciseId;
    }

    public ExerciseRecord findOne(Long memberId, Long recordId) {
        return recordRepository.findByMemberIdAndId(memberId, recordId).orElseThrow(() -> new RuntimeException("기록이 없는데요?"));
    }

    public int getCountTodayRecord(Member member) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        long result = recordRepository.countTodayRecord(member, start, end);
        
        return (int) result;
    }
}
