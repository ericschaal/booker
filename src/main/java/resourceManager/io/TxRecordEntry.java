package resourceManager.io;

import common.tx.TxDecision;
import common.tx.TxState;

import java.io.Serializable;

public class TxRecordEntry implements Serializable {

    private final int id;

    private TxDecision decision;
    private TxState state;

    public TxRecordEntry(int id) {
        this.id = id;
    }

    public TxRecordEntry setYes() {
        decision = TxDecision.YES;
        return this;
    }

    public TxRecordEntry setNo() {
        decision = TxDecision.NO;
        return this;
    }

    public TxRecordEntry setAbort() {
        state = TxState.ABORT;
        return this;
    }

    public TxRecordEntry setCommit() {
        state = TxState.COMMIT;
        return this;
    }

    public int getId() {
        return id;
    }

    public TxDecision getDecision() {
        return decision;
    }

    public TxState getState() {
        return state;
    }
}
