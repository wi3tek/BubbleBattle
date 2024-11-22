package pl.pw.bubblebattle.api.model.actions;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.pw.bubblebattle.api.model.enums.Action;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "action", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChooseCategoryRequest.class, name = "CHOOSE_CATEGORY"),
        @JsonSubTypes.Type(value = StartGameRequest.class, name = "START_GAME"),
        @JsonSubTypes.Type(value = StartAuctionRequest.class, name = "START_AUCTION"),
        @JsonSubTypes.Type(value = FinishAuctionRequest.class, name = "FINISH_AUCTION"),
        @JsonSubTypes.Type(value = ShowQuestionRequest.class, name = "SHOW_QUESTION"),
        @JsonSubTypes.Type(value = RandomQuestionRequest.class, name = "RANDOM_QUESTION"),
        @JsonSubTypes.Type(value = SellAnswersRequest.class, name = "SELL_ANSWERS"),
        @JsonSubTypes.Type(value = AnswerTheQuestionRequest.class, name = "ANSWER_THE_QUESTION"),
        @JsonSubTypes.Type(value = FinishRoundRequest.class, name = "FINISH_ROUND"),
        @JsonSubTypes.Type(value = GoToTheFinalRequest.class, name = "GO_TO_THE_FINAL"),
        @JsonSubTypes.Type(value = EndGameRequest.class, name = "FINISH_GAME"),
        @JsonSubTypes.Type(value = InitBubblesRequest.class, name = "INIT_BUBBLES"),
        @JsonSubTypes.Type(value = StartStopQuestionTimerRequest.class, name = "START_STOP_QUESTION_TIMER")
})
public class PerformActionRequest {

    private String gameId;
    private Action action;
}
