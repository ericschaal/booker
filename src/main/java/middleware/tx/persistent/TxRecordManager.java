package middleware.tx.persistent;

import common.io.Logger;
import common.tx.error.ImproperShutdownException;
import common.tx.error.UndecidableStateException;
import common.tx.model.TxRecoveryAction;
import middleware.io.MiddlewareIOManager;
import middleware.io.MiddlewareIOManagerException;


import java.io.Serializable;
import java.util.Hashtable;

public class TxRecordManager implements Serializable {

    private TxRecord txRecord;

    public TxRecordManager() throws MiddlewareIOManagerException, UndecidableStateException {
        try {
            txRecord = MiddlewareIOManager.getInstance().readTxRecord();

            Logger.print().info("Checking for pending transactions...");

        } catch (MiddlewareIOManagerException e) {
            txRecord = new TxRecord();
            MiddlewareIOManager.getInstance().writeTxRecord(txRecord);
            Logger.print().warning("Initializing fresh TX Record.", "TxRecordManager");
        }
    }

    public Hashtable<Integer, TxRecoveryAction> healthCheck() throws UndecidableStateException {
        return txRecord.checkPending();
    }

    public void newRecord(int txId) {
        txRecord.put(txId, new TxRecordEntry(txId));
        try {
            MiddlewareIOManager.getInstance().writeTxRecord(txRecord);
        } catch (MiddlewareIOManagerException e) {
            e.printStackTrace();
        }
    }

    public void setStart(int txId) {
        if (!txRecord.containsKey(txId)) {
            throw new RuntimeException("Transaction record doesn't exist");
        }
        txRecord.compute(txId, (k,v) -> v.setStart());
        try {
            MiddlewareIOManager.getInstance().writeTxRecord(txRecord);
        } catch (MiddlewareIOManagerException e) {
            e.printStackTrace();
        }
    }


    public void setCommit(int txId) {
        if (!txRecord.containsKey(txId)) {
            throw new RuntimeException("Transaction record doesn't exist");
        }
        txRecord.compute(txId, (k,v) -> v.setCommit());
        try {
            MiddlewareIOManager.getInstance().writeTxRecord(txRecord);
        } catch (MiddlewareIOManagerException e) {
            e.printStackTrace();
        }
    }

    public void setAbort(int txId) {
        if (!txRecord.containsKey(txId)) {
            throw new RuntimeException("Transaction record doesn't exist");
        }
        txRecord.compute(txId, (k,v) -> v.setAbort());
        try {
            MiddlewareIOManager.getInstance().writeTxRecord(txRecord);
        } catch (MiddlewareIOManagerException e) {
            e.printStackTrace();
        }
    }



}
