package pl.pw.bubblebattle.api.model.actions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.pw.bubblebattle.api.model.enums.TeamColor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class SellAnswersRequest extends PerformActionRequest {

    private int price;
    private TeamColor teamColor;
    private int questionRemainingSeconds;
}
