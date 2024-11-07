package pl.pw.bubblebattle.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public enum RoundStage {

    CATEGORY_SELECTION( List.of(Action.CHOOSE_CATEGORY)),
    BEFORE_AUCTION(List.of(Action.START_AUCTION)),
    AUCTION(List.of(Action.FINISH_AUCTION)),
    AUCTION_COMPLETE(List.of(Action.RANDOM_QUESTION)),
    QUESTION(List.of(Action.SHOW_QUESTION,Action.SELL_ANSWERS,Action.ANSWER_THE_QUESTION)),
    QUESTION_WITH_PROMPTS(List.of(Action.ANSWER_THE_QUESTION)),
    QUESTION_WITH_ANSWER(List.of(Action.FINISH_ROUND)),
    ROUND_SUMMARY(List.of(Action.GO_TO_THE_FINAL, Action.CHOOSE_CATEGORY));

    private final List<Action> actions;
}