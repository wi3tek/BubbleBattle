package pl.pw.bubblebattle.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum RoundStage {

    NEW_GAME(List.of(Action.START_GAME), "Nowa gra"),
    CATEGORY_SELECTION( List.of(Action.CHOOSE_CATEGORY),"Wybór kategorii"),
    BEFORE_AUCTION(List.of(Action.START_AUCTION), Constants.LICYTACJA ),
    AUCTION(List.of(Action.FINISH_AUCTION), Constants.LICYTACJA ),
    AUCTION_COMPLETE(List.of(Action.RANDOM_QUESTION), Constants.LICYTACJA ),
    BEFORE_QUESTION(List.of(Action.SHOW_QUESTION), Constants.PYTANIE ),
    QUESTION(List.of(Action.SELL_ANSWERS,Action.ANSWER_THE_QUESTION), Constants.PYTANIE ),
    QUESTION_WITH_PROMPTS(List.of(Action.ANSWER_THE_QUESTION), Constants.PYTANIE ),
    AFTER_ANSWER(List.of(Action.FINISH_ROUND), Constants.PYTANIE ),
    ROUND_SUMMARY(List.of(Action.CHOOSE_CATEGORY, Action.GO_TO_THE_FINAL),"Koniec rundy"),
    GAME_FINISHED(List.of(),"Koniec gry");


    private final List<Action> actions;
    private final String description;

    private static class Constants {
        public static final String PYTANIE = "Pytanie";
        public static final String LICYTACJA = "Licytacja";
    }

    public static boolean actionMatchToRoundStage(Action action, String gameRoundStage) {
        RoundStage roundStage = getByName( gameRoundStage );
        return Arrays.stream( values() )
                .filter( stage -> stage.equals( roundStage ) )
                .anyMatch( stage -> stage.getActions().contains( action ) );
    }


    private static RoundStage getByName(String roundStageName) {
        return Arrays.stream( values() )
                .filter( stage -> stage.name().equals( roundStageName ) )
                .findAny()
                .orElseThrow(() -> new BubbleBattleException( "Not recognized round stage: "+ roundStageName ) );
    }
}