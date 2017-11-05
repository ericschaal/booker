package client;


import common.RemoteConcurrentResourceManager;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import middleware.transaction.TransactionBody;
import middleware.transaction.TransactionResult;

import java.util.ArrayList;
import java.util.function.Function;

public class ManualTransaction {

    private int txId;

    public ManualTransaction(int txId) {
        this.txId = txId;
    }

    ArrayList<TransactionBody<RemoteConcurrentResourceManager, Integer, TransactionResult, Function0<Unit>, TransactionResult>> operations = new ArrayList<>();

    public void addOperation(TransactionBody<RemoteConcurrentResourceManager, Integer, TransactionResult, Function0<Unit>, TransactionResult> body) {
        operations.add(body);
    }

    public TransactionBody<RemoteConcurrentResourceManager, Integer, TransactionResult, Function0<Unit>, TransactionResult> getTx() {
        return (rm, txId, result, abort) -> {
            for (TransactionBody<RemoteConcurrentResourceManager, Integer, TransactionResult, Function0<Unit>, TransactionResult> body : operations) {
                body.apply(rm, txId, result, abort);
            }
            return  result;
        };
    }

    public int getTxId() {
        return txId;
    }
}
