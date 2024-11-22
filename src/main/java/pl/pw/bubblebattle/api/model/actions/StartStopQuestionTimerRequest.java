package pl.pw.bubblebattle.api.model.actions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class StartStopQuestionTimerRequest extends PerformActionRequest {

    private int secondsRemaining;
    private boolean start;

}
