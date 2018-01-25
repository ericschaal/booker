package resourceManager.tx.persistent;

import common.tx.model.TxRecoveryAction;
import common.tx.persistent.TransactionRecord;
import common.tx.error.UndecidableStateException;

import java.util.Hashtable;

public class TxRecord extends TransactionRecord<TxRecordEntry> {


    public TxRecord() {
    }


    public Hashtable<Integer, TxRecoveryAction> checkPending() throws UndecidableStateException {
        Hashtable<Integer, TxRecoveryAction> pending = new Hashtable<>();
        for (TxRecordEntry txEntry : this.values()) {

            switch (txEntry.recoveryAction()) {
                case NIL:
                    break;
                case ABORT:
                    pending.put(txEntry.getId(), TxRecoveryAction.ABORT);
                    break;
                case UNDECIDABLE:
                    throw new UndecidableStateException("Cannot recover transaction "
                            + txEntry.getId() + ", is in an undecidable state.");
                default:
                    throw new RuntimeException("Illegal transaction recovery state");
            }

        }
        return pending;
    }
}
