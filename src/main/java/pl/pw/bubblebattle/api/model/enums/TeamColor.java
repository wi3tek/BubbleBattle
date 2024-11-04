package pl.pw.bubblebattle.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamColor {

    BLUE( false ),
    GREEN( false ),
    YELLOW( false ),
    BLACK( true );

    private final boolean master;
}
