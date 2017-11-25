package common.resource;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import middleware.MiddlewareResourceManager;
import middleware.tx.error.InvalidTransactionException;
import middleware.tx.error.TransactionAbortedException;
import middleware.tx.model.TransactionBody;
import middleware.tx.model.TransactionResult;
import java.rmi.RemoteException;

public interface TransactionalResourceManager extends RemoteConcurrentResourceManager {

    boolean commitTransaction(int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException;

    boolean abortTransaction(int txId) throws RemoteException, InvalidTransactionException;

    int newTransaction() throws RemoteException;

    TransactionResult runInTransaction(TransactionBody<MiddlewareResourceManager, Integer, TransactionResult, Function0<Unit>, TransactionResult> body) throws RemoteException;

    void verify() throws RemoteException;
}
