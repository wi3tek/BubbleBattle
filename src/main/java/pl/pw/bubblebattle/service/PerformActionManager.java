package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.actions.PerformActionRequest;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;

@Service
@RequiredArgsConstructor
public class PerformActionManager {


    private final HostGameService hostGameService;

    public GameResponse performAction(PerformActionRequest request) {
        return switch (request.getAction()) {
            case START_GAME -> hostGameService.startGame( request.getGameId() );
            case CHOOSE_CATEGORY -> hostGameService.chooseCategory(request);
            case START_AUCTION -> hostGameService.startAuction(request);
            case FINISH_AUCTION -> hostGameService.finishAuction(request);
            case RANDOM_QUESTION -> hostGameService.randomQuestion(request);
            case SHOW_QUESTION -> hostGameService.showQuestion(request);
            case SELL_ANSWERS -> hostGameService.sellAnswers(request);
            case ANSWER_THE_QUESTION -> hostGameService.answerTheQuestion(request);
            case FINISH_ROUND -> hostGameService.finishRound(request);
            case GO_TO_THE_FINAL -> hostGameService.goToTheFinal(request);
            default -> throw new BubbleBattleException( "Action not implemented: "+ request.getAction() );
        };
    }
}
