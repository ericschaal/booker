package common.tx.error;

public class UndecidableStateException extends Exception {
    public UndecidableStateException() {
    }

    public UndecidableStateException(String message) {
        super(message);
    }
}
