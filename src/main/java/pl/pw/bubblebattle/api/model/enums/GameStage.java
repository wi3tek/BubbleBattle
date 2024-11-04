package pl.pw.bubblebattle.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GameStage {

    REGULAR( 7 ),
    FINAL( 5 ),;

    @Getter
    private final int maxRoundNumber;
}
