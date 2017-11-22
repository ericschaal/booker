package resourceManager.tx;

import common.io.Logger;
import resourceManager.io.RMIOManager;
import resourceManager.io.RMIOManagerException;
import resourceManager.io.TxRecord;
import resourceManager.io.TxRecordEntry;


public class TxRecordManager {

    private TxRecord txRecord;

    public TxRecordManager() throws RMIOManagerException {
        try {
            txRecord = RMIOManager.getInstance().readTxRecord();
        } catch (Exception e) {
            txRecord = new TxRecord();
            RMIOManager.getInstance().writeTxRecord(txRecord);
            Logger.print().warning("Initializing fresh TX Record.", "TxRecordManager");
        }
    }

    public void newRecord(int txId) {
        txRecord.put(txId, new TxRecordEntry(txId));
    }

    public void setDecisionYes(int txId) {
        if (!txRecord.containsKey(txId)) {
            throw new RuntimeException("Transaction record doesn't exist");
        }
        txRecord.compute(txId, (k,v) -> v.setYes());
    }

    public void setDecisionNo(int txId) {
        if (!txRecord.containsKey(txId)) {
            throw new RuntimeException("Transaction record doesn't exist");
        }
        txRecord.compute(txId, (k,v) -> v.setNo());
    }

    public void setStatusCommit(int txId) {
        if (!txRecord.containsKey(txId)) {
            throw new RuntimeException("Transaction record doesn't exist");
        }
        txRecord.compute(txId, (k,v) -> v.setCommit());
    }

    public void setStatusAbort(int txId) {
        if (!txRecord.containsKey(txId)) {
            throw new RuntimeException("Transaction record doesn't exist");
        }
        txRecord.compute(txId, (k,v) -> v.setAbort());
    }

}
