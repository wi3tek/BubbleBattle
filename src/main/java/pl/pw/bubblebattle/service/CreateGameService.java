package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.CreateGameRequest;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.InitGameRequest;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.service.GameDatabaseService;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.mapper.BubbleBattleMapper;

@Service
@RequiredArgsConstructor
public class CreateGameService {

    private final BubbleBattleMapper mapper = Mappers.getMapper(BubbleBattleMapper.class);
    private final GameDatabaseService gameDatabaseService;

    @Value("${game.settings.start-bubble-amount}")
    public Integer bubbleAmount;

    public GameResponse createGame(CreateGameRequest request) throws BubbleBattleException {
        Game savedGame = gameDatabaseService.save( mapper.map( request ) );
        return mapper.map( savedGame );
    }
}
