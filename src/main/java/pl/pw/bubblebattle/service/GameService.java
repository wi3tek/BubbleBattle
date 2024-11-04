package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.api.model.enums.RoundStage;
import pl.pw.bubblebattle.infrastructure.SseEmitterManager;
import pl.pw.bubblebattle.service.mapper.BubbleBattleMapper;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.service.GameDatabaseService;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameDatabaseService databaseService;
    private final BubbleBattleMapper mapper = Mappers.getMapper(BubbleBattleMapper.class);


    @Async
    public void changeStatusTest(String gameId) {
        Game game = databaseService.read( gameId );
        game.setRoundStage(RoundStage.QUESTION.name());
        databaseService.save( game );

        SseEmitterManager.sendSseEventToClients( gameId,mapper.map( game ) );
    }
}
