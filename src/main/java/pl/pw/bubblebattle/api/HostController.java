package pl.pw.bubblebattle.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.pw.bubblebattle.api.model.ChangeStatusRequest;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.PerformActionRequest;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.GameService;
import pl.pw.bubblebattle.service.HostGameService;
import pl.pw.bubblebattle.service.PerformActionManaager;

@RestController
@RequestMapping("/bubble-battle/api/host")
@RequiredArgsConstructor
@CrossOrigin
public class HostController {

    private final HostGameService hostGameService;
    private final GameService gameService;
    private final PerformActionManaager performActionManaager;

    @PostMapping("/changeStatus")
    @CrossOrigin
    public void changeStatusTest(
            @RequestBody ChangeStatusRequest request
    ) {
        hostGameService.changeStatusTest( request );
    }

    @GetMapping("/init/{gameId}")
    public GameResponse initHostGame(
            @PathVariable String gameId
    ) throws BubbleBattleException {
        return gameService.initGame( gameId, true );
    }

    @PostMapping("/performAction")
    @CrossOrigin
    public GameResponse startGame(
            @RequestBody PerformActionRequest request
            ) {
        return performActionManaager.performAction( request );
    }
}
