package pl.pw.bubblebattle.infrastructure.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(BubbleBattleException.class)
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public GameResponse bookNotFoundHandler(BubbleBattleException ex) {
        log.info( ex.getMessage() );

        return GameResponse.builder()
                .message( ex.getMessage() )
                .status( 418 )
                .build();
    }
}
