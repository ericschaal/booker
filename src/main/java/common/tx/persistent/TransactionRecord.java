package common.tx.persistent;

import common.tx.error.UndecidableStateException;
import common.tx.model.TxRecoveryAction;

import java.util.Hashtable;

public abstract class TransactionRecord<T> extends Hashtable<Integer, T> {

    public abstract Hashtable<Integer, TxRecoveryAction> checkPending() throws UndecidableStateException;

}
