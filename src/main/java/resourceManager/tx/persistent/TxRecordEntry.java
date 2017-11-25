package resourceManager.tx.persistent;

import common.tx.model.TxRecoveryAction;
import common.tx.persistent.TransactionRecordEntry;
import common.tx.model.TxVote;
import common.tx.model.TxDecision;

public class TxRecordEntry implements TransactionRecordEntry {

    private final int id;

    private TxVote vote;
    private TxDecision decision;

    public TxRecordEntry(int id) {
        this.id = id;
    }

    public TxRecordEntry setYes() {
        vote = TxVote.YES;
        return this;
    }

    public TxRecordEntry setNo() {
        vote = TxVote.NO;
        return this;
    }

    public TxRecordEntry setAbort() {
        decision = TxDecision.ABORT;
        return this;
    }

    public TxRecordEntry setCommit() {
        decision = TxDecision.COMMIT;
        return this;
    }

    public int getId() {
        return id;
    }

    public TxVote getVote() {
        return vote;
    }

    public TxDecision getDecision() {
        return decision;
    }


    public TxRecoveryAction recoveryAction() {
        if (decision != null) return TxRecoveryAction.NIL;
        else {
            if (vote != TxVote.YES) return TxRecoveryAction.ABORT;
            if (decision == null) return TxRecoveryAction.ABORT;
            if (vote == TxVote.YES) {
                return TxRecoveryAction.UNDECIDABLE;
            }
            else {
                throw new RuntimeException("Should never never never never ever happen!");
            }
        }
    }

}
