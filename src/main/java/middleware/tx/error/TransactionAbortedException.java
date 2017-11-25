package middleware.tx.error;

import java.io.Serializable;

public class TransactionAbortedException extends Exception implements Serializable {
    public TransactionAbortedException(String message) {
        super(message);
    }
}
