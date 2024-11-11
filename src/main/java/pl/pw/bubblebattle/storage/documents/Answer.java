package pl.pw.bubblebattle.storage.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
public class Answer  {

    private String value;
    @JsonProperty("isCorrect")
    private boolean correct;

}
