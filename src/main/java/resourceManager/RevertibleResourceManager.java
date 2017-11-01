package resourceManager;

import common.Logger;
import common.RemoteRevertibleResourceManager;
import java.rmi.RemoteException;

import java.util.Vector;

public class RevertibleResourceManager implements RemoteRevertibleResourceManager {

    private final ResourceManagerImpl rm = new ResourceManagerImpl();
    private final TransactionHistory history = new TransactionHistory();

    public void startTransaction(int txId) throws RemoteException {
        history.addToHistory(txId, Database.getActiveDb().cloneDb());
    }

    public boolean abortTransaction(int txId) throws RemoteException {
        return history.abortTransaction(txId);
    }

    public boolean commitTransaction(int txId) throws RemoteException {
        return history.commitTransaction(txId);
    }

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        return rm.addFlight(id, flightNum, flightSeats, flightPrice);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        return rm.addCars(id, location, numCars, price);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        return rm.addCars(id, location, numRooms, price);
    }

    //TODO check this method
    @Override
    public int newCustomer(int id) throws RemoteException {
        return rm.newCustomer(id);
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        Logger.print().error("Unimplemented", "RevertibleResourceManager");
        return false;
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        int count = rm.queryFlight(id, flightNum);
        int price = rm.queryFlightPrice(id, flightNum);
        //TODO check if item exists


        return rm.deleteFlight(1, flightNum);
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        return false;
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        return rm.queryFlight(id, flightNumber);
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        return rm.queryCars(id, location);
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        return rm.queryRooms(id, location);
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        return rm.queryCustomerInfo(id, customer);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        return rm.queryFlightPrice(id, flightNumber);
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        return rm.queryCarsPrice(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        return rm.queryRoomsPrice(id, location);
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
