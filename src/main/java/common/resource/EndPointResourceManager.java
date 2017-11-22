package common.resource;

import common.hashtable.RMHashtable;

import java.rmi.RemoteException;

public interface EndPointResourceManager extends RemoteResourceManager {

    void newTransaction(int txId) throws RemoteException;
    boolean abortTransaction(int txId) throws RemoteException;
    boolean commitTransaction(int txId) throws RemoteException;

    boolean voteRequest(int txId) throws RemoteException;

    boolean isCustomer(int id, int cid) throws  RemoteException;
    boolean addReservationToCustomer(int id, int cid, String key, String location, int price, Resource resource) throws RemoteException;
    RMHashtable getCustomerReservations(int id, int customerID) throws RemoteException;

    boolean freeFlight(int id, int flightNumber, int count) throws RemoteException;
    boolean freeCar(int id, String location, int count) throws RemoteException;
    boolean freeRoom(int id, String location, int count) throws RemoteException;

}
