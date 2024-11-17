package pl.pw.bubblebattle.storage.documents;

import lombok.*;

import java.util.Optional;

@Data
@Builder
public class Team  {

    private String color;
    private int bubbleAmount;
    private int bubbleStakesAmount;
    private boolean active;
    private Question activeQuestion;

    public void subtractBubbles(int amount) {
        if(this.bubbleAmount >= amount) {
            setBubbleAmount( this.bubbleAmount - amount );
        }
    }

    public void addBubbles(int amount) {
        setBubbleAmount( this.bubbleAmount + amount );

    }

    public void answer(boolean answerValue) {
        Optional.ofNullable( activeQuestion ).ifPresent( x -> x.setAnsweredCorrect( answerValue ) );
    }
}
