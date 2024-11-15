package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.actions.PerformActionRequest;
import pl.pw.bubblebattle.api.model.enums.TeamColor;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.documents.Team;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoundStageService {

    private final HostGameService hostGameService;

    public void updateAuctionStatus(Game game, boolean finalBid, TeamColor teamColor) {
        int highestBid = game.getHighestBidAmount();
        if(finalBid ||  !isPossibleToContinueBidding(game.getTeams(), highestBid, teamColor)) {
            hostGameService.finishAuction( prepareRequest( game ) );
        }
    }

    private PerformActionRequest prepareRequest(Game game) {
        return PerformActionRequest.builder().gameId( game.getId() ).build();
    }

    private boolean isPossibleToContinueBidding(List<Team> teams, int highestBid, TeamColor teamColor) {
        return teams.stream()
                .filter( Team::isActive )
                .filter(team -> !teamColor.name().equals( team.getColor()))
                .anyMatch( team -> team.getBubbleAmount() + team.getBubbleStakesAmount() > highestBid );
    }
}
