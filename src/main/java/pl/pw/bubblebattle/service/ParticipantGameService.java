package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.infrastructure.SseEmitterManager;
import pl.pw.bubblebattle.service.mapper.BubbleBattleMapper;
import pl.pw.bubblebattle.storage.service.GameDatabaseService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantGameService {

    private final GameDatabaseService gameDatabaseService;
    private final BubbleBattleMapper mapper = Mappers.getMapper( BubbleBattleMapper.class );

    public SseEmitter subscribeGame(String gameId) {
        SseEmitterManager.addEmitter( gameId );
        SseEmitter emitter = SseEmitterManager.getEmitter( gameId );

        // Set a timeout for the SSE connection (optional)
        emitter.onTimeout(() -> {
            log.info("Emitter timed out");
            emitter.complete();
            SseEmitterManager.removeEmitter(gameId);
        });

        // Set a handler for client disconnect (optional)
        emitter.onCompletion(() -> {
            log.info("Emitter completed");
            SseEmitterManager.removeEmitter(gameId);
        });

        return emitter;

    }

    public GameResponse initGame(String gameId) {
        GameResponse gameResponse = mapper.map( this.gameDatabaseService.read( gameId ) );

        gameResponse.markHighestStakes( gameResponse.getTeams() );

        return gameResponse;
    }


}
