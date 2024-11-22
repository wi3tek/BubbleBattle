package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.*;
import pl.pw.bubblebattle.api.model.enums.RoundStage;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.mapper.BubbleBattleMapper;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.service.GameDatabaseService;

import java.util.Comparator;
import java.util.Optional;

import static pl.pw.bubblebattle.api.model.enums.RoundStage.CATEGORY_SELECTION;

@Service
@RequiredArgsConstructor
public class GameService {

    public static final String NEW_GAME_DESCRIPTION = "Nowa gra";
    private final BubbleBattleMapper mapper = Mappers.getMapper( BubbleBattleMapper.class );
    private final GameDatabaseService gameDatabaseService;
    private final HostActionService hostActionService;
    private final QuestionService questionService;

    @Value("${game.settings.start-bubble-amount}")
    public Integer bubbleAmount;

    public GameResponse createGame(CreateGameRequest request) throws BubbleBattleException {
        Game savedGame = gameDatabaseService.save( mapper.map( request ) );
        return mapper.map( savedGame );
    }

    public GetGamesResponse getGames() {
        return GetGamesResponse.builder()
                .games( gameDatabaseService.getAllGames().stream()
                        .map( fullGame ->  {
                            GameItem gameItem = mapper.mapToGameItem( fullGame );
                            if(fullGame.getRoundNumber() == 0 && RoundStage.ROUND_SUMMARY.name().equals( fullGame.getRoundStage() )) {
                                gameItem.setRoundDescription( NEW_GAME_DESCRIPTION );
                            }
                            return gameItem;
                        }
                        )
                        .sorted( Comparator.comparing( GameItem::getDate ,Comparator.reverseOrder()))
                        .toList() )
                .build();
    }

    public void resetGame(String gameId) {

        Game game = this.gameDatabaseService.read( gameId );
        game.reset();
        gameDatabaseService.save( game );
    }


    public GameResponse initGame(String gameId, boolean isHost) {
        Game game = this.gameDatabaseService.read( gameId );

        GameResponse gameResponse = mapper.map( game );
        Optional.ofNullable(gameResponse.getAuctionWinner()).ifPresent( TeamData::shuffleAnswers );
        gameResponse.markHighestStakes( gameResponse.getTeams() );
        gameResponse.setMoneyUp( setMoneyUp(gameResponse) );
        if (!isHost) {
            return gameResponse;
        }
        gameResponse.setHostActions( hostActionService.prepareActions( gameResponse ) );
        questionService.prepareQuestionsAndCategories(gameResponse);

        return gameResponse;
    }

    private boolean setMoneyUp(GameResponse gameResponse) {
        return (gameResponse.getRoundStage().equals( CATEGORY_SELECTION ) && gameResponse.getRoundNumber() == 1);
    }
}
