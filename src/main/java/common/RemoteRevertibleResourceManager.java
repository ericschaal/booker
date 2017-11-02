package common;

import common.hashtable.RMHashtable;

import java.rmi.RemoteException;

public interface RemoteRevertibleResourceManager extends RemoteResourceManager {

    void newTransaction(int txId) throws RemoteException;
    boolean abortTransaction(int txId) throws RemoteException;
    boolean commitTransaction(int txId) throws RemoteException;

    boolean isCustomer(int id, int cid) throws  RemoteException;
    boolean addReservationToCustomer(int id, int cid, String key, String location, int price, Resource resource) throws RemoteException;
    RMHashtable getCustomerReservations(int id, int customerID) throws RemoteException;

    boolean freeFlight(int id, int flightNumber, int count);
    boolean freeCar(int id, String location, int count);
    boolean freeRoom(int id, String location, int count);

}
