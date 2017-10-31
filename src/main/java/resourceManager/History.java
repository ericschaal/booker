package resourceManager;

import common.Logger;

import java.util.Hashtable;
import java.util.Stack;

public class History {

    private Hashtable<Integer, Stack<InverseOperation>> liveTransactions = new Hashtable<>();


    public boolean abortTransaction(int txId) {
        if (liveTransactions.containsKey(txId)) {
            Stack<InverseOperation> stack = liveTransactions.get(txId);
            while (!stack.empty()) {
                if (stack.pop().apply())
                    Logger.print().error("Revert failed for transaction " + txId);
            }
            liveTransactions.remove(txId);
            return true;
        } else return false;
    }

    public boolean commitTransaction(int txId) {
        if (liveTransactions.containsKey(txId)) {
            liveTransactions.remove(txId);
            return true;
        } else {
            return false;
        }
    }

    public void addtoHistory(int txId, InverseOperation operation) {
        if (liveTransactions.containsKey(txId)) {
            liveTransactions.get(txId).push(operation);
        } else {
            liveTransactions.put(txId, new Stack<>()).push(operation);
        }
    }

}
