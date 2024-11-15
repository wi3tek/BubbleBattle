package pl.pw.bubblebattle.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionData {

    private String value;
    private String category;
    private List<AnswerData> answers;

    public void shuffleAnswers() {
        if( answers == null) {
            this.answers = new ArrayList<>();
        }
        Collections.shuffle(answers);
    }
}
