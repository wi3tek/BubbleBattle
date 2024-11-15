package pl.pw.bubblebattle.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pw.bubblebattle.api.model.enums.TeamColor;

import java.util.Optional;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TeamData {

    private TeamColor teamColor;
    private int bubbleAmount;
    private int bubbleStakesAmount;
    private boolean highestStakes;
    private boolean active;
    private QuestionData activeQuestion;

    public void shuffleAnswers() {
        Optional.ofNullable(activeQuestion).ifPresent( x -> x.shuffleAnswers() );
    }
}
