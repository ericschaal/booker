package resourceManager;

import common.RemoteResourceManager;

import java.rmi.RemoteException;
import java.util.Vector;

public class ResourceManagerImp implements RemoteResourceManager {
    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        return false;
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        return false;
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        return false;
    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        return 0;
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        return false;
    }

    public boolean deleteFlight(int id, int flightNum, int flightCount) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        return false;
    }

    public boolean deleteCars(int id, String location, int carCount) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        return false;
    }

    public boolean deleteRooms(int id, String location, int roomCount) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        return false;
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        return 0;
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        return 0;
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        return 0;
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        return null;
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        return 0;
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        return 0;
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        return 0;
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException {
        return false;
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        return false;
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd) throws RemoteException {
        return false;
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber, int count) throws RemoteException {
        return false;
    }

    @Override
    public boolean reserveCar(int id, int customer, String location, int count) throws RemoteException {
        return false;
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd, int count) throws RemoteException {
        return false;
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws RemoteException {
        return false;
    }
}
