package pl.pw.bubblebattle.service;

import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.enums.Action;
import pl.pw.bubblebattle.api.model.enums.GameStage;
import pl.pw.bubblebattle.api.model.enums.RoundStage;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.storage.documents.Game;

@Service
public class ValidatorService {


    public void validateGameBeforeAction(Action action, Game game) {
        GameStage gameStage = GameStage.valueOf( game.getGameStage() );
        String roundStage = game.getRoundStage();
        boolean isCorrect = switch (action) {
            case START_GAME -> gameStage.equals( GameStage.REGULAR ) && game.getRoundNumber() == 0;
            case CHOOSE_CATEGORY, START_AUCTION, FINISH_AUCTION, RANDOM_QUESTION, SHOW_QUESTION, SELL_ANSWERS,
                 ANSWER_THE_QUESTION, FINISH_ROUND, GO_TO_THE_FINAL, FINISH_GAME ->
                    RoundStage.actionMatchToRoundStage( action, roundStage );
        };

        if (!isCorrect) {
            throw new BubbleBattleException( "Game is not in proper stage for perform action " + action.name() );
        }

    }

}
