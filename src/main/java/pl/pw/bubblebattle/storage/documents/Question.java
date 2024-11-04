package pl.pw.bubblebattle.storage.documents;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Builder
@Document("Question")
public class Question  {

    @Id
    private String id;
    private String value;
    private Boolean answeredCorrect;
    private List<Answer> answers;
    private Boolean used;
    private String category;


}
