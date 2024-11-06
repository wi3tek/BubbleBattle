package pl.pw.bubblebattle.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GameStage {

    BEFORE_GAME( 0 ),
    REGULAR( 7 ),
    FINAL( 5 );


    private final int maxRoundNumber;
}
