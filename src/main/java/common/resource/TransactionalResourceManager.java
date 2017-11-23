package common.resource;

import common.resource.RemoteConcurrentResourceManager;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import middleware.MiddlewareResourceManager;
import middleware.transaction.InvalidTransactionException;
import middleware.transaction.TransactionAbortedException;
import middleware.transaction.TransactionBody;
import middleware.transaction.TransactionResult;
import java.rmi.RemoteException;

public interface TransactionalResourceManager extends RemoteConcurrentResourceManager {

    void commitTransaction(int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException;

    void abortTransaction(int txId) throws RemoteException, InvalidTransactionException;

    int newTransaction() throws RemoteException;

    TransactionResult runInTransaction(TransactionBody<MiddlewareResourceManager, Integer, TransactionResult, Function0<Unit>, TransactionResult> body) throws RemoteException;

    void verify() throws RemoteException;
}
