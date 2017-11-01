package middleware.transaction;

import common.Logger;
import common.RemoteConcurrentResourceManager;
import common.RemoteResourceManager;
import common.Resource;
import middleware.MiddlewareResourceManager;
import middleware.lockManager.DeadlockException;
import middleware.lockManager.LockManager;
import middleware.transaction.TxManager;
import resourceManager.RevertibleResourceManager;

import java.rmi.RemoteException;
import java.util.Vector;

class MiddlewareConcurrentResourceManager implements RemoteConcurrentResourceManager {


    private final MiddlewareResourceManager rm;
    private TxManager txManager;


    public MiddlewareConcurrentResourceManager(MiddlewareResourceManager rm, TxManager txManager) {
        this.rm = rm;
        this.txManager = txManager;
    }


    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.FLIGHT.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.FLIGHT);
        return rm.addFlight(id, flightNum, flightSeats, flightPrice);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.CAR.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.CAR);
        return rm.addCars(id, location, numCars, price);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.ROOM.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.ROOM);
        return rm.addRooms(id, location, numRooms, price);
    }

    @Override
    public int newCustomer(int id) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.CUSTOMER.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.CUSTOMER);
        return rm.newCustomer(id);
    }

    @Override
    public boolean newCustomer(int id, int cid) throws DeadlockException, RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.FLIGHT.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.FLIGHT);
        return rm.deleteFlight(id, flightNum);
    }

    @Override
    public boolean deleteCars(int id, String location) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.CAR.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.CAR);
        return rm.deleteCars(id, location);
    }

    @Override
    public boolean deleteRooms(int id, String location) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.ROOM.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.ROOM);
        return rm.deleteRooms(id, location);
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.CUSTOMER.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.CUSTOMER);
        return rm.deleteCustomer(id, customer);
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.FLIGHT.name(), LockManager.READ);
        txManager.enlist(id, Resource.FLIGHT);
        return rm.queryFlight(id, flightNumber);
    }

    @Override
    public int queryCars(int id, String location) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.CAR.name(), LockManager.READ);
        txManager.enlist(id, Resource.CAR);
        return rm.queryCars(id, location);
    }

    @Override
    public int queryRooms(int id, String location) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.ROOM.name(), LockManager.READ);
        txManager.enlist(id, Resource.ROOM);
        return rm.queryRooms(id, location);
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.CUSTOMER.name(), LockManager.READ);
        txManager.enlist(id, Resource.CUSTOMER);
        return rm.queryCustomerInfo(id, customer);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.FLIGHT.name(), LockManager.READ);
        txManager.enlist(id, Resource.FLIGHT);
        return rm.queryFlightPrice(id, flightNumber);
    }

    @Override
    public int queryCarsPrice(int id, String location) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.CAR.name(), LockManager.READ);
        txManager.enlist(id, Resource.CAR);
        return rm.queryCarsPrice(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.ROOM.name(), LockManager.READ);
        txManager.enlist(id, Resource.ROOM);
        return rm.queryRoomsPrice(id, location);
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.FLIGHT.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.FLIGHT);
        return rm.reserveFlight(id, customer, flightNumber);
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.CAR.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.CAR);
        return rm.reserveCar(id, customer, location);
    }

    @Override
    public boolean reserveRoom(int id, int customer, String location) throws DeadlockException, RemoteException {
        LockManager.get().Lock(id, Resource.ROOM.name(), LockManager.WRITE);
        txManager.enlist(id, Resource.ROOM);
        return rm.reserveRoom(id, customer, location);
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber, int count) throws DeadlockException, RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    @Override
    public boolean reserveCar(int id, int customer, String location, int count) throws DeadlockException, RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd, int count) throws DeadlockException, RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws DeadlockException, RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

}
