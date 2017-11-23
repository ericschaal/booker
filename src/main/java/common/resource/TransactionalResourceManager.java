package common.resource;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import middleware.MiddlewareResourceManager;
import middleware.tx.InvalidTransactionException;
import middleware.tx.TransactionAbortedException;
import middleware.tx.TransactionBody;
import middleware.tx.TransactionResult;
import java.rmi.RemoteException;

public interface TransactionalResourceManager extends RemoteConcurrentResourceManager {

    void commitTransaction(int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException;

    void abortTransaction(int txId) throws RemoteException, InvalidTransactionException;

    int newTransaction() throws RemoteException;

    TransactionResult runInTransaction(TransactionBody<MiddlewareResourceManager, Integer, TransactionResult, Function0<Unit>, TransactionResult> body) throws RemoteException;

    void verify() throws RemoteException;
}
