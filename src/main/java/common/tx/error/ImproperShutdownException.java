package common.tx.error;

import common.tx.model.TxRecoveryAction;

import java.util.Hashtable;

public class ImproperShutdownException extends Exception {

    private final Hashtable<Integer, TxRecoveryAction> pendingTransactions;

    public ImproperShutdownException(String message, Hashtable<Integer, TxRecoveryAction> pendingTransactions) {
        super(message);
        this.pendingTransactions = pendingTransactions;
    }

    public ImproperShutdownException(Hashtable<Integer, TxRecoveryAction> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public Hashtable<Integer, TxRecoveryAction> getPendingTransactions() {
        return pendingTransactions;
    }
}
