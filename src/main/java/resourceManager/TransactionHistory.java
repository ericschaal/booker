package resourceManager;

import common.io.Logger;
import common.hashtable.RMHashtable;

import java.io.Serializable;
import java.util.Hashtable;

public class TransactionHistory implements Serializable {

    private Hashtable<Integer, RMHashtable> liveTransactions = new Hashtable<>();


    public boolean abortTransaction(int txId){
        if (liveTransactions.containsKey(txId)) {
            Database.getActiveDb().revertDb(liveTransactions.get(txId));
            liveTransactions.remove(txId);
            Logger.print().info("Transaction " + txId + " aborted.", "TransactionHistory");
            return true;
        } else {
            Logger.print().warning("Trying to abort unknown transaction. Already committed/aborted?", "TransactionHistory");
            return false;
        }
    }

    public boolean commitTransaction(int txId) {
        if (liveTransactions.containsKey(txId)) {
            liveTransactions.remove(txId);
            Logger.print().info("Transaction " + txId + " committed. Removing from history.", "TransactionHistory");
            return true;
        } else {
            Logger.print().warning("Trying to commit unknown transaction. Already committed/aborted?", "TransactionHistory");
            return false;
        }
    }

    public void addToHistory(int txId, RMHashtable oldDb) {
        if (!liveTransactions.containsKey(txId)) {
            liveTransactions.put(txId, oldDb);
            Logger.print().info("New transaction added to history", "TransactionHistory");
        } else {
            Logger.print().error("Error! Double history for transaction!", "TransactionHistory");
        }
    }

}
