package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.ChangeStatusRequest;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.infrastructure.SseEmitterManager;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.mapper.BubbleBattleMapper;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.documents.Team;
import pl.pw.bubblebattle.storage.service.GameDatabaseService;

@Service
@RequiredArgsConstructor
public class HostGameService {

    private final GameDatabaseService databaseService;
    private final BubbleBattleMapper mapper = Mappers.getMapper( BubbleBattleMapper.class );


    @Async
    public synchronized void changeStatusTest(ChangeStatusRequest request) {
        String gameId = request.getGameId();
        Game game = databaseService.read( gameId );

        game.updateStakes( request.getBubblesAmount() );

        Team team = game.getTeams().stream()
                .filter( teamColor -> teamColor.getColor().equals( request.getTeam() ) )
                .findFirst()
                .orElseThrow( () -> new BubbleBattleException( "WRONG TEAM COLOR " + request.getTeam() ) );

        team.setBubbleStakesAmount( team.getBubbleStakesAmount() + request.getBubblesAmount() );
        team.setBubbleAmount( team.getBubbleAmount() - request.getBubblesAmount() );
        game.updateTeam( team );

        databaseService.save( game );

        GameResponse gameResponse = mapper.map( game );
        gameResponse.markHighestStakes( gameResponse.getTeams() );
        SseEmitterManager.sendSseEventToClients( gameId, gameResponse );
    }
}
