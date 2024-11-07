package pl.pw.bubblebattle.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.pw.bubblebattle.api.model.CreateGameRequest;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.GetGamesResponse;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.GameService;

@RestController
@RequestMapping("/bubble-battle/api")
@RequiredArgsConstructor
@CrossOrigin
public class ApplicationController {

    private final GameService gameService;

    @PostMapping("/createGame")
    public GameResponse createGame(
            @RequestBody CreateGameRequest request
    ) throws BubbleBattleException {
        return gameService.createGame( request );
    }

    @GetMapping("/getGames")
    public GetGamesResponse getGames() {
        return gameService.getGames();
    }

    @PostMapping("/reset/{gameId}")
    public void resetGame(
            @PathVariable String gameId
    ) {
        gameService.resetGame( gameId );
    }

}
