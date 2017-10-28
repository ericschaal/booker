package middleware.transaction;

public class TransactionAborted extends Exception {
    public TransactionAborted(String message) {
        super(message);
    }
}
