package middleware.tx.persistent;

import common.io.Logger;
import common.tx.error.UndecidableStateException;
import common.tx.model.TxRecoveryAction;
import common.tx.model.TxStart;
import common.tx.model.TxDecision;
import common.tx.model.TxTermination;
import common.tx.persistent.TransactionRecordEntry;

public class TxRecordEntry implements TransactionRecordEntry {

    private final int id;

    private TxStart start;
    private TxDecision decision;

    public TxRecordEntry(int id) {
        this.id = id;
    }

    // TODO find a way to tell txmanager commit/abort
    @Override
    public TxRecoveryAction recoveryAction() {
        if (start == null) {
            Logger.print().info("No START Record");
            return TxRecoveryAction.SEND_ABORT;
        }
        if (decision == null) {
            Logger.print().info("NO COMMIT/ABORT Record");
            return TxRecoveryAction.VOTE_REQ;
        }
        else {
            Logger.print().info("FOUND " + decision);
            if (decision == TxDecision.ABORT) return TxRecoveryAction.SEND_ABORT;
            else return TxRecoveryAction.SEND_COMMIT;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    public TxRecordEntry setStart() {
        this.start = TxStart.START_2PC;
        return this;
    }

    public TxRecordEntry setCommit() {
        this.decision = TxDecision.COMMIT;
        return this;
    }

    public TxRecordEntry setAbort() {
        this.decision = TxDecision.ABORT;
        return this;
    }


}
