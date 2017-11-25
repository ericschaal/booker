package middleware.tx.model;

import middleware.lockManager.DeadlockException;
import middleware.tx.error.InvalidTransactionException;
import middleware.tx.error.TransactionAbortedException;

import java.io.Serializable;
import java.rmi.RemoteException;


@FunctionalInterface
public interface TransactionBody<A, B, C, D, E> extends Serializable {
    E apply(A rm, B txId, C result, D abort) throws DeadlockException, RemoteException, TransactionAbortedException, InvalidTransactionException;
}
