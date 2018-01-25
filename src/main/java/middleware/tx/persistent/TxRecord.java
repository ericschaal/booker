package middleware.tx.persistent;
import common.tx.error.UndecidableStateException;
import common.tx.model.TxRecoveryAction;
import common.tx.persistent.TransactionRecord;

import java.util.Hashtable;

public class TxRecord extends TransactionRecord<TxRecordEntry> {


    @Override
    public Hashtable<Integer, TxRecoveryAction> checkPending() throws UndecidableStateException  {
        Hashtable<Integer, TxRecoveryAction> pending = new Hashtable<>();
        for (TxRecordEntry txEntry : this.values()) {
            switch (txEntry.recoveryAction()) {
                case NIL:
                    break;
                case SEND_ABORT:
                    pending.put(txEntry.getId(), TxRecoveryAction.SEND_ABORT);
                    break;
                case SEND_COMMIT:
                    pending.put(txEntry.getId(), TxRecoveryAction.SEND_COMMIT);
                    break;
                case VOTE_REQ:
                    pending.put(txEntry.getId(), TxRecoveryAction.VOTE_REQ);
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
