package middleware.transaction;

public class TransactionAbortedException extends Exception {
    public TransactionAbortedException(String message) {
        super(message);
    }
}
