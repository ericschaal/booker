package middleware.tx.error;

public class InvalidTransactionException extends Exception {
    public InvalidTransactionException() {
    }

    public InvalidTransactionException(String message) {
        super(message);
    }
}
