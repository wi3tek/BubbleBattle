package pl.pw.bubblebattle.storage.documents;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("Game")
@Data
@Builder(toBuilder = true)
public class Game {

    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String gameStage;
    private int stageQuestionNumber;
    private String roundStage;
    private List<Team> teams;
    private Stakes stakes;

    @CreatedDate
    private LocalDateTime creationDate;

    @LastModifiedDate
    private LocalDateTime modificationDate;

    @CreatedBy
    private String createdBy;

}
