package pl.pw.bubblebattle.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamColor {

    BLUE( 1,false ),
    GREEN( 2,false ),
    YELLOW( 3,false ),
    BLACK( 4,true );

    private final int order;
    private final boolean master;
}
