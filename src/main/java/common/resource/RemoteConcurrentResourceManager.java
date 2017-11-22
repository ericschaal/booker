package common.resource;

import middleware.lockManager.DeadlockException;
import middleware.transaction.InvalidTransactionException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface RemoteConcurrentResourceManager extends Remote {
    
    boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    boolean addCars(int id, String location, int numCars, int price) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    boolean addRooms(int id, String location, int numRooms, int price) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    int newCustomer(int id) throws DeadlockException, RemoteException, InvalidTransactionException;


    boolean newCustomer(int id, int cid) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    boolean deleteFlight(int id, int flightNum) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    boolean deleteCars(int id, String location) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    boolean deleteRooms(int id, String location) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    boolean deleteCustomer(int id, int customer) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    int queryFlight(int id, int flightNumber) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    int queryCars(int id, String location) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    int queryRooms(int id, String location) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    String queryCustomerInfo(int id, int customer) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    int queryFlightPrice(int id, int flightNumber) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    int queryCarsPrice(int id, String location) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    int queryRoomsPrice(int id, String location) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    boolean reserveFlight(int id, int customer, int flightNumber) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    boolean reserveCar(int id, int customer, String location) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    boolean reserveRoom(int id, int customer, String locationd) throws DeadlockException, RemoteException, InvalidTransactionException;

     
    boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws DeadlockException, RemoteException, InvalidTransactionException;

    void shutdown() throws RemoteException;
}
