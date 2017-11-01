package common;

import java.rmi.RemoteException;

public interface RemoteRevertibleResourceManager extends RemoteResourceManager {

    void startTransaction(int txId) throws RemoteException;
    boolean abortTransaction(int txId) throws RemoteException;
    boolean commitTransaction(int txId) throws RemoteException;
}
