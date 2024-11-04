package pl.pw.bubblebattle.storage.documents;

import lombok.*;

@Data
@Builder
public class Team  {

    private String color;
    private int bubbleAmount;
    private int bubbleStakesAmount;
    private Boolean isActive;
    private Question activeQuestion;

}
