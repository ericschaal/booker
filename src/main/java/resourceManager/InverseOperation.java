package resourceManager;

import java.rmi.RemoteException;

@FunctionalInterface
public interface InverseOperation {
    boolean apply() throws RemoteException;
}
