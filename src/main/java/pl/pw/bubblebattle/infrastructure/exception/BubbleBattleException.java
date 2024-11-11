package pl.pw.bubblebattle.infrastructure.exception;

public class BubbleBattleException extends RuntimeException {

    public BubbleBattleException(String message) {
        super(message);
    }

    public BubbleBattleException(String message, Throwable e) {
        super(message,e);
    }
}

