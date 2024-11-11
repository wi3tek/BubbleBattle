package pl.pw.bubblebattle.storage.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("Question")
public class Question  {

    @Id
    private String id;
    private int excelId;
    private String value;
    private boolean answeredCorrect;
    private List<Answer> answers;
    private boolean used;
    private String category;

    @CreatedDate
    private LocalDateTime creationDate;

    @LastModifiedDate
    private LocalDateTime modificationDate;

    @CreatedBy
    private String createdBy;



    public void addAnswer(Answer answer) {
        if(this.answers == null) {
            this.answers = new ArrayList<>();
        }

        answers.add( answer );
    }

}
