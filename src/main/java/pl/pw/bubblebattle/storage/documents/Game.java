package pl.pw.bubblebattle.storage.documents;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.pw.bubblebattle.api.model.enums.TeamColor;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Document("Game")
@Data
@Builder(toBuilder = true)
public class Game {

    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String gameStage;
    private int roundNumber;
    private String roundStage;
    private List<Team> teams;
    private Stakes stakes;
    private String currentCategory;
    private int highestBidAmount;
    private AuctionHistory currentAuctionHistory;

    @CreatedDate
    private LocalDateTime creationDate;

    @LastModifiedDate
    private LocalDateTime modificationDate;

    @CreatedBy
    private String createdBy;


    public void updateStakes(int bubblesAmount) {
        this.stakes.setBubbleAmount( this.stakes.getBubbleAmount() + bubblesAmount );
    }

    public void updateTeam(Team team) {
        List<Team> objectTeams = new ArrayList<>( this.teams );

        Team teamT = this.teams.stream().filter( t -> t.getColor().equals( team.getColor() ) )
                .findFirst()
                .orElseThrow( () -> new BubbleBattleException( "Wrong team color " + team.getColor() ) );
        objectTeams.remove( teamT );
        objectTeams.add( team );
        this.teams = objectTeams;
    }

    public void reset() {
        this.stakes.setBubbleAmount( 0 );
        this.teams.forEach( team -> {
            team.setBubbleAmount( 10000 );
            team.setBubbleStakesAmount( 0 );
        } );
    }

    public void incrementRoundNumber() {
        this.roundNumber++;
    }

    public void startAuction() {
        this.setHighestBidAmount( 500 );
        this.teams.stream()
                .filter( Team::isActive )
                .forEach( team -> {
                    int bubbles = team.getBubbleAmount();
                    int teamStakes = team.getBubbleStakesAmount();
                    team.setBubbleAmount( bubbles - 500 );
                    team.setBubbleStakesAmount( teamStakes + 500 );
                    this.stakes.setBubbleAmount( this.stakes.getBubbleAmount() + 500 );
                } );
    }

    public void subtractBubbles(TeamColor teamColor, int price) {
        getTeamByColor( teamColor )
                .filter( team -> team.getBubbleAmount() >= price )
                .ifPresent( team -> team.setBubbleAmount( team.getBubbleAmount() - price ) );
    }

    private Optional<Team> getTeamByColor(TeamColor teamColor) {
        return this.getTeams().stream()
                .filter( Team::isActive )
                .filter( team -> team.getColor().equals( teamColor.name() ) )
                .findFirst();
    }

    public void updateTeamBubbles(TeamColor teamColor, int bubbleAmount) {
        this.teams.stream().filter( Team::isActive )
                .filter( team -> teamColor.name().equals( team.getColor() ) )
                .findFirst()
                .ifPresent( team -> team.setBubbleAmount( team.getBubbleAmount() + bubbleAmount ) );
    }

    public void resetStakes() {
        this.teams.stream()
                .filter( Team::isActive )
                .forEach( team -> {
                    team.setBubbleStakesAmount( 0 );
                    team.setActiveQuestion( null );
                } );
    }

    public void checkTeamsAfterRound() {
        this.teams.stream()
                .filter( Team::isActive )
                .filter( team -> team.getBubbleAmount() < 600 )
                .forEach( team -> team.setActive( false ) );
    }

    public void prepareTeamsToFinal() {
        int maxBubblesAmount = this.teams.stream()
                .filter( Team::isActive )
                .mapToInt( Team::getBubbleAmount )
                .max()
                .orElseThrow( () -> new BubbleBattleException( "Cannot estimate max bubbles amount" ) );

        if (
                this.teams.stream()
                        .filter( Team::isActive )
                        .filter( team -> team.getBubbleAmount() == maxBubblesAmount )
                        .count() > 2
        ) {
            throw new BubbleBattleException( "More than one team has max bubbles amount " + maxBubblesAmount );
        }

        int currentStakesAmount = this.getCurrentAuctionHistory().getAuctionHistoryItemList().stream()
                .filter( x -> TeamColor.STAKES.name().equals( x.getTeamColor() ) )
                .findFirst()
                .map( AuctionHistoryItem::getBubblesAmount )
                .orElseThrow( () -> new BubbleBattleException( "There is no auction history item for Stakes!" ) );
        int finalBubblesAmount = maxBubblesAmount;

        for (Team team : teams) {
            if (team.getBubbleAmount() != maxBubblesAmount) {
                team.setActive( false );
            }

            if (team.getBubbleAmount() == maxBubblesAmount) {
                if (!team.getColor().equals( this.stakes.getAuctionWinner().getColor() )) {
                    team.addBubbles( currentStakesAmount );
                }
                finalBubblesAmount = team.getBubbleAmount();
            }
        }

        int masterBubblesAmount = finalBubblesAmount;
        this.teams.stream()
                .filter( team -> TeamColor.BLACK.name().equals( team.getColor() ) )
                .findFirst()
                .ifPresent( team -> {
                    team.setActive( true );
                    team.setBubbleAmount( masterBubblesAmount );
                } );

        this.stakes.setAuctionWinner( null );
    }

    public void updateTeamsByCurrentHistory() {
        AuctionHistory auctionHistory = Optional.ofNullable( getCurrentAuctionHistory() )
                .orElseThrow( () -> new BubbleBattleException( "There is no actual auction history" ) );
        this.setHighestBidAmount( auctionHistory.getHighestBidAmount() );
        this.teams.forEach( team -> updateTeam(team,auctionHistory.getAuctionHistoryItemList() ) );
    }

    private void updateTeam(Team team, List<AuctionHistoryItem> historyItems) {
        for (AuctionHistoryItem historyItem: historyItems) {
            if(TeamColor.STAKES.name().equals( historyItem.getTeamColor() )) {
                this.stakes.setBubbleAmount( historyItem.getBubblesAmount() );
                continue;
            }
            if(team.getColor().equals( historyItem.getTeamColor() )) {
                team.setBubbleAmount( historyItem.getBubblesAmount() );
                team.setBubbleStakesAmount( historyItem.getBidAmount() );
            }
        }
    }
}
