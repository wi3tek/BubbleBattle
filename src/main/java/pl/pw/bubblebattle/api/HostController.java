package pl.pw.bubblebattle.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.pw.bubblebattle.api.model.ChangeStatusRequest;
import pl.pw.bubblebattle.service.HostGameService;

@RestController
@RequestMapping("/bubble-battle/api/host")
@RequiredArgsConstructor
public class HostController {

    private final HostGameService gameService;

    @PostMapping("/changeStatus")
    @CrossOrigin
    public void changeStatusTest(
            @RequestBody ChangeStatusRequest request
    ) {
        gameService.changeStatusTest( request );
    }
}
