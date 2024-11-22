package pl.pw.bubblebattle.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.pw.bubblebattle.api.model.CorrectBubblesRequest;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.RaiseStakesRequest;
import pl.pw.bubblebattle.api.model.ReverseRestoreAuctionRequest;
import pl.pw.bubblebattle.api.model.actions.PerformActionRequest;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.*;

@RestController
@RequestMapping("/bubble-battle/api/host")
@RequiredArgsConstructor
@CrossOrigin
public class HostController {

    private final HostGameService hostGameService;
    private final GameService gameService;
    private final PerformActionManager performActionManager;
    private final AuctionService auctionService;

    @PostMapping("/raiseStakes")
    @CrossOrigin
    public void raiseStakes(
            @RequestBody RaiseStakesRequest request
    ) {
        auctionService.raiseStakes( request );
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
        return performActionManager.performAction( request );
    }

    @PostMapping("/correctBubbles")
    @CrossOrigin
    public void raiseStakes(
            @RequestBody CorrectBubblesRequest request
    ) {
        hostGameService.correctBubbles( request );
    }


    @PostMapping("/reverseRestoreAuction")
    @CrossOrigin
    public void reverseRestoreAuction(
            @RequestBody ReverseRestoreAuctionRequest request
    ) {
        hostGameService.reverseRestoreAuction( request );
    }
}
