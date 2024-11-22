package pl.pw.bubblebattle.storage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.enums.AuctionHistoryOption;
import pl.pw.bubblebattle.api.model.enums.TeamColor;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.storage.documents.*;
import pl.pw.bubblebattle.storage.repository.AuctionRepo;
import pl.pw.bubblebattle.storage.repository.GameRepo;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuctionDatabaseService {

    private final AuctionRepo auctionRepo;
    private final GameRepo gameRepo;

    public void saveHistory(Game game, AuctionHistoryOption auctionHistoryOption) {
        switch (auctionHistoryOption) {
            case INSERT -> insert( game, true );
            case FORWARD -> getNextAuctionHistory( game );
            case BACKWARD -> getPreviousAuctionHistory( game );
            case RAISE_STAKES -> insert( game, false );
        }
    }

    private void getNextAuctionHistory(Game game) {
        Optional.ofNullable( game.getCurrentAuctionHistory().getNextHistoryId() )
                .ifPresent( auctionHistoryId -> {
                    AuctionHistory auctionHistory = auctionRepo
                            .findById( auctionHistoryId )
                            .orElseThrow( () -> new BubbleBattleException( "There is no next auction history for " +
                                    "game: " + game.getId() ) );
                    game.setCurrentAuctionHistory( auctionHistory );
                } );
    }

    private void getPreviousAuctionHistory(Game game) {
        AuctionHistory currentAuctionHistory =
                Optional.ofNullable( game.getCurrentAuctionHistory() ).orElseThrow( () -> new BubbleBattleException(
                        "There is no current auction History" ) );
        Optional.ofNullable( currentAuctionHistory.getPreviousHistoryId() ).ifPresent( auctionHistoryId -> {
            AuctionHistory previousAuctionHistory = auctionRepo
                    .findById( auctionHistoryId )
                    .orElseThrow( () -> new BubbleBattleException( "There is no previous auction history for " +
                            "game: " + game.getId() ) );
            previousAuctionHistory.setNextHistoryId( currentAuctionHistory.getHistoryId() );
            game.setCurrentAuctionHistory( auctionRepo.save( previousAuctionHistory ) );
        } );
    }


    private void insert(Game game, boolean newAuction) {
        String auctionId = newAuction
                ? UUID.randomUUID().toString()
                : game.getCurrentAuctionHistory().getAuctionId();

        AuctionHistory newAuctionHistory = AuctionHistory.builder()
                .auctionId( auctionId )
                .gameId( game.getId() )
                .highestBidAmount( game.getHighestBidAmount() )
                .previousHistoryId( Optional.ofNullable( game.getCurrentAuctionHistory() ).map( AuctionHistory::getHistoryId ).orElse( null ) )
                .nextHistoryId( null )
                .auctionHistoryItemList( prepareItemList( game.getTeams(), game.getStakes(), game.getCurrentCategory() ) )
                .build();

        String previousAuctionHistoryId = auctionRepo.findByAuctionId( auctionId ).stream()
                .max( Comparator.comparing( AuctionHistory::getCreationDate ) )
                .map( AuctionHistory::getHistoryId )
                .orElse( null );

        newAuctionHistory.setPreviousHistoryId( previousAuctionHistoryId );
        game.setCurrentAuctionHistory( auctionRepo.insert( newAuctionHistory ) );
    }

    private List<AuctionHistoryItem> prepareItemList(List<Team> teams, Stakes stakes, String currentCategory) {
        List<AuctionHistoryItem> itemList = new ArrayList<>();
        itemList.add(
                AuctionHistoryItem.builder()
                        .bubblesAmount( stakes.getBubbleAmount() )
                        .teamColor( TeamColor.STAKES.name() )
                        .category( currentCategory )
                        .build()
        );

        teams.stream()
                .filter( Team::isActive )
                .forEach( team -> itemList.add(
                        AuctionHistoryItem.builder()
                                .teamColor( team.getColor() )
                                .bidAmount( team.getBubbleStakesAmount() )
                                .bubblesAmount( team.getBubbleAmount() )
                                .category( currentCategory )
                                .build()
                ) );
        return itemList;
    }
}
