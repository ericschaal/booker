package resourceManager.tx.persistent;

import common.io.Logger;
import common.tx.error.ImproperShutdownException;
import common.tx.error.UndecidableStateException;
import common.tx.model.TxRecoveryAction;
import resourceManager.io.RMIOManager;
import resourceManager.io.RMIOManagerException;

import java.io.IOException;
import java.util.Hashtable;


public class TxRecordManager {

    private TxRecord txRecord;

    public TxRecordManager() throws RMIOManagerException, ImproperShutdownException, UndecidableStateException {
        try {
            txRecord = RMIOManager.getInstance().readTxRecord();

            Logger.print().info("Checking for pending transactions...");
            Hashtable<Integer, TxRecoveryAction> pending = txRecord.checkPending();

            if (!pending.isEmpty()) {
                throw new ImproperShutdownException(pending);
            }

        } catch (RMIOManagerException e) {
            txRecord = new TxRecord();
            RMIOManager.getInstance().writeTxRecord(txRecord);
            Logger.print().warning("Initializing fresh TX Record.", "TxRecordManager");
        }
    }


    public void newRecord(int txId) {
        txRecord.put(txId, new TxRecordEntry(txId));
    }

    public void setDecisionYes(int txId) throws TxRecordException {
        if (!txRecord.containsKey(txId)) {
            throw new TxRecordException();
        }
        txRecord.compute(txId, (k,v) -> v.setYes());
        try {
            RMIOManager.getInstance().writeTxRecord(txRecord);
        } catch (RMIOManagerException e) {
            e.printStackTrace();
        }
    }

    public void setDecisionNo(int txId) throws TxRecordException {
        if (!txRecord.containsKey(txId)) {
            throw new TxRecordException();
        }
        txRecord.compute(txId, (k,v) -> v.setNo());
        try {
            RMIOManager.getInstance().writeTxRecord(txRecord);
        } catch (RMIOManagerException e) {
            e.printStackTrace();
        }
    }

    public void setCommit(int txId) throws TxRecordException {
        if (!txRecord.containsKey(txId)) {
            throw new TxRecordException();
        }
        txRecord.compute(txId, (k,v) -> v.setCommit());
        try {
            RMIOManager.getInstance().writeTxRecord(txRecord);
        } catch (RMIOManagerException e) {
            e.printStackTrace();
        }
    }

    public void setAbort(int txId) throws TxRecordException {
        if (!txRecord.containsKey(txId)) {
            throw new TxRecordException();
        }
        txRecord.compute(txId, (k,v) -> v.setAbort());
        try {
            RMIOManager.getInstance().writeTxRecord(txRecord);
        } catch (RMIOManagerException e) {
            e.printStackTrace();
        }
    }

    public boolean hasRecord(int txId) {
        return txRecord.containsKey(txId);
    }

}
