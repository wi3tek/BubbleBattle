package pl.pw.bubblebattle.service;

import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.enums.Action;
import pl.pw.bubblebattle.api.model.enums.GameStage;
import pl.pw.bubblebattle.api.model.enums.RoundStage;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.storage.documents.Game;

import java.util.List;

@Service
public class ValidatorService {

    private static final List<String> QUESTION_STAGES = List.of(
            RoundStage.QUESTION.name(),
            RoundStage.QUESTION_WITH_PROMPTS.name(),
            RoundStage.BEFORE_QUESTION.name()
    );

    public void validateGameBeforeAction(Action action, Game game) {
        GameStage gameStage = GameStage.valueOf( game.getGameStage() );
        String roundStage = game.getRoundStage();
        boolean isCorrect = switch (action) {
            case INIT_BUBBLES -> RoundStage.ROUND_SUMMARY.name().equals( roundStage ) && game.getRoundNumber() == 0;
            case START_GAME, CHOOSE_CATEGORY, START_AUCTION, FINISH_AUCTION, RANDOM_QUESTION, SHOW_QUESTION,
                 SELL_ANSWERS, ANSWER_THE_QUESTION, FINISH_ROUND ->
                    RoundStage.actionMatchToRoundStage( action, roundStage );
            case FINISH_GAME ->
                    gameStage.equals( GameStage.FINAL ) && roundStage.equals( RoundStage.ROUND_SUMMARY.name() );
            case REVERSE_RESTORE_AUCTION -> RoundStage.AUCTION.name().equals( roundStage );
            case GO_TO_THE_FINAL ->
                    RoundStage.actionMatchToRoundStage( action, roundStage ) && GameStage.REGULAR.equals( gameStage );
            case START_STOP_QUESTION_TIMER -> QUESTION_STAGES.contains( roundStage );
        };

        if (!isCorrect) {
            throw new BubbleBattleException( "Game is not in proper stage for perform action " + action.name() );
        }
    }


}
