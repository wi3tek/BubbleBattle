package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.RaiseStakesRequest;
import pl.pw.bubblebattle.api.model.enums.RoundStage;
import pl.pw.bubblebattle.infrastructure.SseEmitterManager;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.mapper.BubbleBattleMapper;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.documents.Team;
import pl.pw.bubblebattle.storage.service.GameDatabaseService;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final GameDatabaseService databaseService;
    private final BubbleBattleMapper mapper = Mappers.getMapper( BubbleBattleMapper.class );
    private final RoundStageService roundStageService;

    @Async
    public synchronized void raiseStakes(RaiseStakesRequest request) {
        String gameId = request.getGameId();
        Game game = databaseService.read( gameId );
        if (!RoundStage.AUCTION.name().equals( game.getRoundStage() )) {
            throw new BubbleBattleException( "Raising stakes is possible only in AUCTION stage" );
        }

        Team team = game.getTeams().stream()
                .filter( teamColor -> teamColor.getColor().equals( request.getTeamColor().name() ) )
                .filter( Team::isActive )
                .findFirst()
                .orElseThrow( () -> new BubbleBattleException( "WRONG TEAM COLOR " +
                        request.getTeamColor().name() +
                        " OR TEAM IS NOT ACTIVE" )
                );

        if (request.isFinalBid()) {
            request.setBubblesAmount( team.getBubbleAmount() + team.getBubbleStakesAmount() );
        }

        if (request.getBubblesAmount() <= game.getHighestBidAmount()) {
            throw new BubbleBattleException( String.format(
                    "%s's team bid amount (%d) is less or equal than highest game bid amount (%d)",
                    request.getTeamColor().name(),
                    request.getBubblesAmount(),
                    game.getHighestBidAmount()
            ) );
        }

        int difference = getDifference( request, team );

        team.setBubbleStakesAmount( team.getBubbleStakesAmount() + difference );
        team.setBubbleAmount( team.getBubbleAmount() - difference );
        game.updateTeam( team );
        game.updateStakes( difference );
        game.setHighestBidAmount( team.getBubbleStakesAmount() );

        databaseService.save( game );
        roundStageService.updateAuctionStatus( game ,request.isFinalBid(), request.getTeamColor());

        GameResponse gameResponse = mapper.map( game );
        gameResponse.markHighestStakes( gameResponse.getTeams() );

        SseEmitterManager.sendSseEventToClients( gameId, gameResponse );
    }

    private int getDifference(RaiseStakesRequest request, Team team) {
        int difference = request.getBubblesAmount() - team.getBubbleStakesAmount();

        if (difference < 0) {
            throw new BubbleBattleException( String.format(
                    "Bid amount (%d) is less or equal than %s's bid amount (%d)",
                    request.getBubblesAmount(),
                    team.getColor(),
                    team.getBubbleStakesAmount()
            ) );
        }

        if (team.getBubbleAmount() < difference) {
            throw new BubbleBattleException( String.format(
                    "Team '%s' has not enough bubbles (%d) for bid (value: %d)",
                    request.getTeamColor().name(),
                    team.getBubbleAmount(),
                    request.getBubblesAmount()
            ) );
        }
        return difference;
    }
}
