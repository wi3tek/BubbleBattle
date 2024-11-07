package pl.pw.bubblebattle.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.GameService;
import pl.pw.bubblebattle.service.ParticipantGameService;

@RestController
@RequestMapping("/bubble-battle/api/participant")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantGameService participantGameService;
    private final GameService gameService;


    @CrossOrigin
    @GetMapping("/subscribe/{gameId}")
    public SseEmitter subscribeGame(
            @PathVariable String gameId
    ) throws BubbleBattleException {
        return participantGameService.subscribeGame( gameId );
    }

    @CrossOrigin
    @GetMapping("/init/{gameId}")
    public GameResponse initParticipantGame(
            @PathVariable String gameId
    ) throws BubbleBattleException {
        return gameService.initGame( gameId, false );
    }
}
