package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.CreateGameRequest;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.GetGamesResponse;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.mapper.BubbleBattleMapper;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.service.GameDatabaseService;

@Service
@RequiredArgsConstructor
public class GameService {

    private final BubbleBattleMapper mapper = Mappers.getMapper( BubbleBattleMapper.class );
    private final GameDatabaseService gameDatabaseService;

    @Value("${game.settings.start-bubble-amount}")
    public Integer bubbleAmount;

    public GameResponse createGame(CreateGameRequest request) throws BubbleBattleException {
        Game savedGame = gameDatabaseService.save( mapper.map( request ) );
        return mapper.map( savedGame );
    }

    public GetGamesResponse getGames() {
        return GetGamesResponse.builder()
                .games( gameDatabaseService.getAllGames().stream()
                        .map( mapper::mapToGameItem )
                        .toList() )
                .build();
    }

    public void resetGame(String gameId) {

        Game game = this.gameDatabaseService.read( gameId );
        game.reset();
        gameDatabaseService.save( game );
    }
}
