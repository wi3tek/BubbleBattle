package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.HostAction;
import pl.pw.bubblebattle.api.model.TeamData;
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
            int roundNumber,
            int activeTeams
    ) {
        if (activeTeams < 2 && RoundStage.ROUND_SUMMARY.equals( roundStage )) {
            return switch (gameStage) {
                case REGULAR -> List.of( prepareHostAction( Action.GO_TO_THE_FINAL ) );
                case FINAL -> List.of( prepareHostAction( Action.FINISH_GAME ) );
            };
        }

        if (GameStage.REGULAR.equals( gameStage ) && roundNumber > GameStage.REGULAR.getMaxRoundNumber()) {
            return List.of( prepareHostAction( Action.GO_TO_THE_FINAL ) );
        }

        if (RoundStage.GAME_FINISHED.equals( roundStage ) ) {
            return List.of();
        }

        if (GameStage.FINAL.equals( gameStage ) && roundNumber > GameStage.FINAL.getMaxRoundNumber()) {
            return List.of( prepareHostAction( Action.FINISH_GAME ) );
        }

        if(RoundStage.ROUND_SUMMARY.equals( roundStage ) && GameStage.FINAL.equals(  gameStage)) {
            return List.of(prepareHostAction(  Action.CHOOSE_CATEGORY));
        }


        return roundStage.getActions().stream()
                .map( this::prepareHostAction )
                .toList();
    }

    public List<HostAction> prepareActions(GameResponse gameResponse) {
        long activeTeams = gameResponse.getTeams().stream()
                .filter( TeamData::isActive )
                .count();


        return prepareActions(
                gameResponse.getRoundStage(),
                gameResponse.getGameStage(),
                gameResponse.getRoundNumber(),
                (int) activeTeams
        );
    }

    private HostAction prepareHostAction(Action action) {
        return HostAction.builder()
                .action( action )
                .description( action.getActionDescription() )
                .build();
    }
}
