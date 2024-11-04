package pl.pw.bubblebattle.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pw.bubblebattle.api.model.CreateGameRequest;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.CreateGameService;
import pl.pw.bubblebattle.service.GameService;
import pl.pw.bubblebattle.service.InitGameService;

@RestController
@RequestMapping("/bubble-battle/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ApplicationController {

    private final CreateGameService createGameService;
    private final InitGameService initGameService;
    private final GameService gameService;

    @GetMapping("/createGame")
    public GameResponse createGame(
            @RequestBody CreateGameRequest request
    ) throws BubbleBattleException {
        return createGameService.createGame( request );
    }

    @GetMapping("/init/host/{gameId}")
    public SseEmitter initHostGame(
            @PathVariable String gameId
    ) throws BubbleBattleException {
        return initGameService.initGame( gameId, true );
    }

    @GetMapping("/init/participant/{gameId}")
    public SseEmitter initParticipantGame(
            @PathVariable String gameId
    ) throws BubbleBattleException {
        return initGameService.initGame( gameId, false );
    }

    @PostMapping("/changeStatus/{gameId}")
    public void changeStatusTest(
            @PathVariable String gameId
    ) {
        gameService.changeStatusTest(gameId);
    }


}
