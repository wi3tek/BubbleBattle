package pl.pw.bubblebattle.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public enum SpecialCategory {

    PROMPT( "PODPOWIEDŹ" ),
    WHITE_BOX( "BIAŁA SKRZYNKA" );

    private final String value;

    public static Optional<SpecialCategory> getByValue(String value) {
        return Arrays.stream( values() )
                .filter( specialCategory -> specialCategory.getValue().equals( value ) )
                .findAny();
    }
}
