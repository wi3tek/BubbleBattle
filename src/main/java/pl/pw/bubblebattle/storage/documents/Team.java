package pl.pw.bubblebattle.storage.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
public class Team  {

    private String color;
    private int bubbleAmount;
    private int bubbleStakesAmount;
    @JsonProperty("isActive")
    private boolean active;
    private Question activeQuestion;

}
