package hello.squadfit.domain.record.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveRecordRequest {

    @NotNull
    private Long memberId;
    @NotNull
    private Integer weight;
    @NotNull
    private Integer repeatNumber;
    @NotNull
    private Integer successNumber;
    @NotNull
    private Integer failNumber;
    @NotNull
    private Long exerciseTypeId;
}