package pl.pw.bubblebattle.storage.documents;

import lombok.*;

@Data
@Builder
public class Answer  {

    private String value;
    private Boolean isCorrect;

}
