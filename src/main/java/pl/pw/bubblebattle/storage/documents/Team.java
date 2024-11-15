package pl.pw.bubblebattle.storage.documents;

import lombok.*;

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
}
