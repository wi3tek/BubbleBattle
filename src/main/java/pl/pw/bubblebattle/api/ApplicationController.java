package pl.pw.bubblebattle.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.pw.bubblebattle.api.model.CreateGameRequest;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.GetGamesResponse;
import pl.pw.bubblebattle.infrastructure.ExcelReader;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.GameService;
import pl.pw.bubblebattle.service.QuestionService;

@RestController
@RequestMapping("/bubble-battle/api")
@RequiredArgsConstructor
@CrossOrigin
public class ApplicationController {

    private final GameService gameService;
    private final QuestionService questionService;

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

    @PostMapping("/excel/upload")
    public ResponseEntity<Object> uploadFile(
            @RequestParam("file") MultipartFile file
    ) {
        String message = "";
        if (ExcelReader.hasExcelFormat(file)) {
            try {
                questionService.save(file);
                message = "The Excel file is uploaded: " + file.getOriginalFilename();
                return ResponseEntity.status( HttpStatus.OK).body(message);
            } catch (Exception exp) {
                message = "The Excel file is not upload: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

}
