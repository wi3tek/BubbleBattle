package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.HostAction;
import pl.pw.bubblebattle.api.model.enums.Action;
import pl.pw.bubblebattle.api.model.enums.GameStage;
import pl.pw.bubblebattle.api.model.enums.RoundStage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HostActionService {

    public List<HostAction> prepareActions(
            RoundStage roundStage,
            GameStage gameStage,
            int roundNumber
    ) {

        if (RoundStage.ROUND_SUMMARY.equals( roundStage ) && roundNumber == 0) {
            return List.of( prepareHostAction( Action.START_GAME ) );
        }

        if (GameStage.REGULAR.equals( gameStage ) && roundNumber == GameStage.REGULAR.getMaxRoundNumber()) {
            return List.of( prepareHostAction( Action.GO_TO_THE_FINAL ) );
        }

        if (GameStage.FINAL.equals( gameStage ) && roundNumber == GameStage.FINAL.getMaxRoundNumber()) {
            return List.of( prepareHostAction( Action.FINISH_ROUND ) );
        }

        return roundStage.getActions().stream()
                .map( this::prepareHostAction )
                .toList();
    }

    public List<HostAction> prepareActions(GameResponse gameResponse) {
        return prepareActions(
                gameResponse.getRoundStage(),
                gameResponse.getGameStage(),
                gameResponse.getRoundNumber()
        );
    }

    private HostAction prepareHostAction(Action action) {
        return HostAction.builder()
                .action( action )
                .description( action.getActionDescription() )
                .build();
    }
}
