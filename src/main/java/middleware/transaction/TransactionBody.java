package middleware.transaction;

import middleware.lockManager.DeadlockException;
import java.rmi.RemoteException;


@FunctionalInterface
public interface TransactionBody<A, B, C, D, E>  {
    E apply(A rm, B txId, C result, D abort) throws DeadlockException, RemoteException, TransactionAborted;
}
