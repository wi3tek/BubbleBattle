package pl.pw.bubblebattle.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.pw.bubblebattle.api.model.*;
import pl.pw.bubblebattle.api.model.enums.GameStage;
import pl.pw.bubblebattle.api.model.enums.RoundStage;
import pl.pw.bubblebattle.api.model.enums.TeamColor;
import pl.pw.bubblebattle.storage.documents.*;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface BubbleBattleMapper {


    int START_BUBBLE_AMOUNT = 0;
    int START_BUBBLE_STAKES = 0;
    String DEFAULT_USER = "SYSTEM";

    @Mapping( source = "creationDate", target = "date")
    @Mapping(source ="id", target = "gameId")
    @Mapping(source = "stakes.bubbleAmount", target = "bubbleStakes")
    @Mapping(source = "currentCategory", target = "currentCategory")
    @Mapping( source = "stakes.auctionWinner", target = "auctionWinner")
    @Mapping(target = "possibleForward", source = "currentAuctionHistory", qualifiedByName = "mapPossibleForward")
    @Mapping(target = "possibleBackward", source = "currentAuctionHistory", qualifiedByName = "mapPossibleBackward")
    GameResponse map(Game source);

    @Named("mapPossibleForward")
    default boolean mapPossibleForward (AuctionHistory currentAuctionHistory) {
        return Optional.ofNullable(currentAuctionHistory)
                .map( ah -> ah.getNextHistoryId() != null )
                .orElse( false );
    }

    @Named("mapPossibleBackward")
    default boolean mapPossibleBackward (AuctionHistory currentAuctionHistory) {
        return Optional.ofNullable(currentAuctionHistory)
                .map( ah -> ah.getPreviousHistoryId() != null )
                .orElse( false );
    }

    @Mapping( source = "color", target = "teamColor")
    TeamData map(Team source);

    default Game map(CreateGameRequest request) {
        return Game.builder()
                .name( request.getName() )
                .roundStage( RoundStage.NEW_GAME.name() )
                .roundNumber( 0 )
                .gameStage( GameStage.REGULAR.name() )
                .teams(TeamColor.getValuesWithoutStakes().stream()
                        .map( teamColor -> Team.builder()
                                .activeQuestion( null )
                                .bubbleAmount( START_BUBBLE_AMOUNT )
                                .bubbleStakesAmount( START_BUBBLE_STAKES )
                                .active( !teamColor.isMaster() )
                                .color( teamColor.name() )
                                .build() )
                        .toList() )
                .stakes( Stakes.builder()
                        .bubbleAmount( 0 )
                        .build() )
                .createdBy( DEFAULT_USER )
                .build();
    }

    @Mapping( source = "id", target = "gameId")
    @Mapping( source = "creationDate", target = "date")
    @Mapping(target = "roundDescription", source = "roundStage", qualifiedByName = "prepareRoundDescription")
    GameItem mapToGameItem(Game source);

    @Named("prepareRoundDescription")
    default String prepareRoundDescription (String roundStage) {
        return RoundStage.valueOf( roundStage ).getDescription();
    }

    QuestionData map(Question source);

    AnswerData map (Answer source);


}
