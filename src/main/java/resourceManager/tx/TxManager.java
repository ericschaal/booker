package resourceManager.tx;

import common.io.Logger;
import resourceManager.io.RMIOManagerException;
import resourceManager.perf.RMStatistics;
import resourceManager.storage.Database;
import resourceManager.storage.DatabaseException;


public class TxManager {

    private static TxManager instance;

    private TxRecordManager recordManager;


    public TxManager() {
        try {
            this.recordManager = new TxRecordManager();
        } catch (RMIOManagerException e) {
            Logger.print().error("File System Error! Please delete all files and try again.");
            e.printStackTrace();
        } catch (ImproperShutdownException e) {
            Logger.print().warning("Aborting pending transactions", "TXManager");
            for (int txId : e.getPendingTransactions()) {
                abortTransaction(txId);
            }
        }
    }

    public static void init() {
        if (instance == null) {
            instance = new TxManager();
        } else {
            throw new RuntimeException("TxManager already initialized");
        }
    }

    public static TxManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("TxManager not initialized");
        }
        return instance;
    }

    public void newTransaction(int txId) {
        Logger.print().info("START_2PC " + txId, "TxManager");
        Database.get().newLocalCopy(txId);
        recordManager.newRecord(txId);
    }

    public boolean voteRequest(int txId) {
        //TODO vote request
        Logger.print().info("VOTE_REQ", "Tx: " + txId);
        recordManager.setDecisionYes(txId);
        return true;
    }

    public boolean abortTransaction(int txId) {
        Logger.print().info("ABORT" + txId, "TxManager");
        long start = System.currentTimeMillis();
        try {
            Database.get().removeTxLocalCopy(txId);
            return true;
        } catch (DatabaseException e) {
            Logger.print().warning(e.getMessage(), "Tx: " + txId);
            return true;
        } finally {
            RMStatistics.instance.getAverageAbortTime().addValue(System.currentTimeMillis() - start);
            recordManager.setStatusAbort(txId);
        }
    }

    public boolean commitTransaction(int txId) {
        Logger.print().info("COMMIT" + txId, "TxManager");
        long start = System.currentTimeMillis();
        try {
            Database.get().writeBackLocalCopyToDiskAndRemove(txId);
            Database.get().swapMaster();
            return true;
        } catch (DatabaseException e) {
            Logger.print().warning(e.getMessage(), "Tx: " + txId);
            return true;
        }finally {
            RMStatistics.instance.getAverageCommitTime().addValue(System.currentTimeMillis() - start);
            recordManager.setStatusCommit(txId);
        }
    }

}
