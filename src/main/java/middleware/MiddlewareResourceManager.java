package middleware;

import common.*;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

import java.rmi.RemoteException;
import java.util.Vector;

import middleware.transaction.TransactionBody;
import middleware.transaction.TransactionResult;
import middleware.transaction.TxManager;
import resourceManager.RevertibleResourceManager;

public class MiddlewareResourceManager implements TransactionalResourceManager {

    private final TxManager globalTxManager;

    private final RemoteRevertibleResourceManager carRM;
    private final RemoteRevertibleResourceManager flightRM;
    private final RemoteRevertibleResourceManager customerRM;
    private final RemoteRevertibleResourceManager roomRM;


    public MiddlewareResourceManager(RemoteRevertibleResourceManager carRM, RemoteRevertibleResourceManager flightRM, RemoteRevertibleResourceManager customerRM, RemoteRevertibleResourceManager roomRM) {
        this.carRM = carRM;
        this.flightRM = flightRM;
        this.customerRM = customerRM;
        this.roomRM = roomRM;

        this.globalTxManager = new TxManager(this, 4000);
    }

    @Override
    public TransactionResult runInTransaction(TransactionBody<RemoteConcurrentResourceManager, Integer, TransactionResult, Function0<Unit>, TransactionResult> body) throws RemoteException {
        return globalTxManager.runInTransaction(body);
    }

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        return flightRM.addFlight(id, flightNum, flightSeats, flightPrice);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        return carRM.addCars(id, location, numCars, price);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        return roomRM.addRooms(id, location, numRooms, price);
    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        return customerRM.newCustomer(id);
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        return flightRM.deleteFlight(id, flightNum);
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        return carRM.deleteCars(id, location);
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        return roomRM.deleteRooms(id, location);
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        return carRM.deleteCustomer(id, customer);
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        return flightRM.queryFlight(id, flightNumber);
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        return carRM.queryCars(id, location);
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        return roomRM.queryRooms(id, location);
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        return customerRM.queryCustomerInfo(id, customer);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        return flightRM.queryFlightPrice(id, flightNumber);
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        return carRM.queryCarsPrice(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        return roomRM.queryRoomsPrice(id, location);
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException {
        return flightRM.reserveFlight(id, customer, flightNumber);
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        return carRM.reserveCar(id, customer, location);
    }

    @Override
    public boolean reserveRoom(int id, int customer, String location) throws RemoteException {
        return roomRM.reserveRoom(id, customer, location);
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber, int count) throws RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    @Override
    public boolean reserveCar(int id, int customer, String location, int count) throws RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd, int count) throws RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    public RemoteRevertibleResourceManager getCarRM() {
        return carRM;
    }

    public RemoteRevertibleResourceManager getFlightRM() {
        return flightRM;
    }

    public RemoteRevertibleResourceManager getCustomerRM() {
        return customerRM;
    }

    public RemoteRevertibleResourceManager getRoomRM() {
        return roomRM;
    }
}
