package pl.pw.bubblebattle.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.pw.bubblebattle.api.model.CreateGameRequest;
import pl.pw.bubblebattle.api.model.GameItem;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.TeamData;
import pl.pw.bubblebattle.api.model.enums.GameStage;
import pl.pw.bubblebattle.api.model.enums.RoundStage;
import pl.pw.bubblebattle.api.model.enums.TeamColor;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.documents.Stakes;
import pl.pw.bubblebattle.storage.documents.Team;

import java.util.Arrays;

@Mapper(componentModel = "spring")
public interface BubbleBattleMapper {


    int START_BUBBLE_AMOUNT = 0;
    int START_BUBBLE_STAKES = 0;
    String DEFAULT_USER = "SYSTEM";

    @Mapping( source = "creationDate", target = "date")
    @Mapping(source ="id", target = "gameId")
    @Mapping(source = "stakes.bubbleAmount", target = "bubbleStakes")
    GameResponse map(Game source);

    Team  map(TeamData source);

    @Mapping( source = "color", target = "teamColor")
    TeamData map(Team source);

    default Game map(CreateGameRequest request) {
        return Game.builder()
                .name( request.getName() )
                .roundStage( RoundStage.ROUND_SUMMARY.name() )
                .roundNumber( 0 )
                .gameStage( GameStage.REGULAR.name() )
                .teams( Arrays.stream( TeamColor.values() )
                        .map( teamColor -> Team.builder()
                                .activeQuestion( null )
                                .bubbleAmount( START_BUBBLE_AMOUNT )
                                .bubbleStakesAmount( START_BUBBLE_STAKES )
                                .isActive( !teamColor.isMaster() )
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
    GameItem mapToGameItem(Game source);

}
