package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.PerformActionRequest;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;

@Service
@RequiredArgsConstructor
public class PerformActionManaager {


    private final HostGameService hostGameService;

    public GameResponse performAction(PerformActionRequest request) {
        return switch (request.getAction()) {
            case START_GAME -> hostGameService.startGame( request.getGameId() );
            default -> throw new BubbleBattleException( "Action not implemented: "+ request.getAction() );
        };
    }
}
