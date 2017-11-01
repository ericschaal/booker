package common;

import middleware.lockManager.DeadlockException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface RemoteConcurrentResourceManager extends Remote {
    
    boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws DeadlockException, RemoteException;

     
    boolean addCars(int id, String location, int numCars, int price) throws DeadlockException, RemoteException;

     
    boolean addRooms(int id, String location, int numRooms, int price) throws DeadlockException, RemoteException;

     
    int newCustomer(int id) throws DeadlockException, RemoteException;

     
    boolean newCustomer(int id, int cid) throws DeadlockException, RemoteException;

     
    boolean deleteFlight(int id, int flightNum) throws DeadlockException, RemoteException;

     
    boolean deleteCars(int id, String location) throws DeadlockException, RemoteException;

     
    boolean deleteRooms(int id, String location) throws DeadlockException, RemoteException;

     
    boolean deleteCustomer(int id, int customer) throws DeadlockException, RemoteException;

     
    int queryFlight(int id, int flightNumber) throws DeadlockException, RemoteException;

     
    int queryCars(int id, String location) throws DeadlockException, RemoteException;

     
    int queryRooms(int id, String location) throws DeadlockException, RemoteException;

     
    String queryCustomerInfo(int id, int customer) throws DeadlockException, RemoteException;

     
    int queryFlightPrice(int id, int flightNumber) throws DeadlockException, RemoteException;

     
    int queryCarsPrice(int id, String location) throws DeadlockException, RemoteException;

     
    int queryRoomsPrice(int id, String location) throws DeadlockException, RemoteException;

     
    boolean reserveFlight(int id, int customer, int flightNumber) throws DeadlockException, RemoteException;

     
    boolean reserveCar(int id, int customer, String location) throws DeadlockException, RemoteException;

     
    boolean reserveRoom(int id, int customer, String locationd) throws DeadlockException, RemoteException;

     
    boolean reserveFlight(int id, int customer, int flightNumber, int count) throws DeadlockException, RemoteException;

     
    boolean reserveCar(int id, int customer, String location, int count) throws DeadlockException, RemoteException;

     
    boolean reserveRoom(int id, int customer, String locationd, int count) throws DeadlockException, RemoteException;

     
    boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws DeadlockException, RemoteException;
}
