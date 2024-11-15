package pl.pw.bubblebattle.api.model.actions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.pw.bubblebattle.api.model.enums.AnswerType;
import pl.pw.bubblebattle.api.model.enums.TeamColor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class AnswerTheQuestionRequest extends PerformActionRequest {

    private AnswerType answer;
    private TeamColor teamColor;
}
