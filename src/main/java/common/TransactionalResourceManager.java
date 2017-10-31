package common;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import middleware.transaction.TransactionBody;
import middleware.transaction.TransactionResult;

import java.rmi.RemoteException;

public interface TransactionalResourceManager extends RemoteResourceManager {

    TransactionResult runInTransaction(TransactionBody<RemoteConcurrentResourceManager, Integer, TransactionResult, Function0<Unit>, TransactionResult> body) throws RemoteException;
}
