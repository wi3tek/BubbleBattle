package pl.pw.bubblebattle.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum TeamColor {

    BLUE( 1,false ),
    GREEN( 2,false ),
    YELLOW( 3,false ),
    BLACK( 4,true ),
    STAKES(5, false);

    private final int order;
    private final boolean master;

    public static List<TeamColor> getValuesWithoutStakes() {
        return Arrays.stream( values() ).filter( x-> x != STAKES )
                .toList();
    }
}
