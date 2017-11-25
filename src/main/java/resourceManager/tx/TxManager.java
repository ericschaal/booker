package resourceManager.tx;

import common.io.Logger;
import common.tx.error.ImproperShutdownException;
import common.tx.error.UndecidableStateException;
import resourceManager.io.RMIOManagerException;
import resourceManager.perf.RMStatistics;
import resourceManager.storage.Database;
import resourceManager.storage.DatabaseException;
import resourceManager.tx.persistent.TxRecordException;
import resourceManager.tx.persistent.TxRecordManager;


public class TxManager {

    private static TxManager instance;

    private TxRecordManager recordManager;


    public TxManager() {
        int retryCount = 0;
        while (true) {
            try {
                this.recordManager = new TxRecordManager();
                break;
            } catch (RMIOManagerException e) {
                Logger.print().error("File System Error! Please delete all files and try again.");
                e.printStackTrace();
            } catch (ImproperShutdownException e) {
                if (retryCount > 3) {
                    throw new RuntimeException("Failed to recover transactions.");
                }
                e.getPendingTransactions().forEach((txId, action) -> {
                    switch (action) {
                        case ABORT:
                            Logger.print().warning("Transaction " + txId + " needs to be aborted ", "TXManager");
                            abortTransaction(txId);
                            break;
                        case COMMIT:
                            Logger.print().warning("Transaction " + txId + " needs to be committed ", "TXManager");
                            commitTransaction(txId);
                            break;
                    }
                });
                retryCount++;
            } catch (UndecidableStateException e) {
                Logger.print().error("Undecidable transaction state. Aborting start.");
                e.printStackTrace();
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
        Database.get().newLocalCopy(txId);
    }

    public boolean prepare(int txId) {
        try {
            if (!recordManager.hasRecord(txId)) return false;
            Logger.print().info("Adding new tx record " + txId, "TxManager");
            recordManager.newRecord(txId);

            //TODO vote request

            if (Database.get().isLocalCopyAvailable(txId)) {
                Logger.print().info("Setting tx record to YES", "Tx: " + txId);
                recordManager.setDecisionYes(txId);
                return true;
            } else {
                Logger.print().info("Setting tx record to NO", "Tx: " + txId);
                recordManager.setDecisionNo(txId);
                return false;
            }
        } catch (TxRecordException e) {
            //ignore request
            return false;
        }
    }

    public boolean abortTransaction(int txId) {
        try {
            if (!recordManager.hasRecord(txId)) return false;
            Logger.print().info("ABORT received from middleware", "Tx: " + txId);
            long start = System.currentTimeMillis();
            try {
                Database.get().removeTxLocalCopy(txId);
                return true;
            } catch (DatabaseException e) {
                Logger.print().warning(e.getMessage(), "Tx: " + txId);
                return true;
            } finally {
                Logger.print().info("Setting tx record to ABORT", "Tx: " + txId);
                recordManager.setCommit(txId);
                RMStatistics.instance.getAverageAbortTime().addValue(System.currentTimeMillis() - start);
            }
        } catch (TxRecordException e) {
            //ignore request
            return false;
        }
    }

    public boolean commitTransaction(int txId) {
        try {
            if (!recordManager.hasRecord(txId)) return false;
            Logger.print().info("COMMIT received from middleware", "Tx: " + txId);
            long start = System.currentTimeMillis();
            try {
                Database.get().writeBackLocalCopyToDiskAndRemove(txId);
                Database.get().swapMaster();
                return true;
            } catch (DatabaseException e) {
                Logger.print().warning(e.getMessage(), "Tx: " + txId);
                return true;
            } finally {
                Logger.print().info("Setting tx record to COMMITTED", "Tx: " + txId);
                recordManager.setCommit(txId);
                RMStatistics.instance.getAverageCommitTime().addValue(System.currentTimeMillis() - start);
            }
        } catch (TxRecordException e) {
            //ignore request
            return false;
        }
    }

}
