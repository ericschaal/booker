package middleware.tx;

public class InvalidTransactionException extends Exception {
    public InvalidTransactionException() {
    }

    public InvalidTransactionException(String message) {
        super(message);
    }
}
