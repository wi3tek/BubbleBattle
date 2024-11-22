package pl.pw.bubblebattle.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Action {

    START_GAME("Rozpocznij grę",0),
    INIT_BUBBLES("Rozdaj bombelki",0),
    START_STOP_QUESTION_TIMER("Zatrzymaj zegar",0),
    CHOOSE_CATEGORY("Wybierz kategorię",1),
    START_AUCTION("Rozpocznij licytację",2),
    FINISH_AUCTION("Zakończ licytację",3),
    RANDOM_QUESTION("Wylosuj pytanie",4),
    SHOW_QUESTION("Pokaż pytanie",5),
    SELL_ANSWERS("Sprzedaj podpowiedź",6),
    ANSWER_THE_QUESTION("Odpowiedz na pytanie",7),
    FINISH_ROUND("Zakończ rundę",8),
    GO_TO_THE_FINAL("Przejdź do finału",9),
    FINISH_GAME("Zakończ grę",10),
    REVERSE_RESTORE_AUCTION("Cofnij / przywróć stan licytacji", 99);

    private final String actionDescription;
    private final int order;
}
