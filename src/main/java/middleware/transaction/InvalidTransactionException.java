package middleware.transaction;

public class InvalidTransactionException extends Exception {
    public InvalidTransactionException() {
    }

    public InvalidTransactionException(String message) {
        super(message);
    }
}
