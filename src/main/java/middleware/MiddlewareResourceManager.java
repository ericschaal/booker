package middleware;

import common.*;
import common.hashtable.RMHashtable;
import common.resource.*;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import middleware.transaction.TransactionBody;
import middleware.transaction.TransactionResult;
import middleware.transaction.TxManager;

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

        this.globalTxManager = new TxManager(this, 12 * 1000 /* 12 seconds */);
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


    //TODO is this the right place?
    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        RMHashtable reservationHT = customerRM.getCustomerReservations(id, customer);
        if (reservationHT != null) {
            for (Enumeration e = reservationHT.elements(); e.hasMoreElements(); ) {
                ReservedItem reservedItem = (ReservedItem) (e.nextElement());
                Trace.info("RM::deleteCustomer(" + id + ", " + customer + ") has reserved " + reservedItem.getResourceType() + " " + reservedItem.getCount() + " times");
                switch (reservedItem.getResourceType()) {
                    case CAR:
                        if (!carRM.freeCar(id, reservedItem.getLocation(), reservedItem.getCount())) {
                            return false;
                        }
                        break;
                    case ROOM:
                        if (!roomRM.freeRoom(id, reservedItem.getLocation(), reservedItem.getCount())) {
                            return false;
                        }
                        break;
                    case FLIGHT:
                        if (!flightRM.freeFlight(id, Integer.valueOf(reservedItem.getLocation()), reservedItem.getCount())) {
                            return false;
                        }
                        break;
                    case CUSTOMER:
                        Trace.info("RM::deleteCustomer error customer is not reservable.");
                        return false;
                }
            }
            return customerRM.deleteCustomer(id, customer);
        }
        return false;
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
        if (customerRM.isCustomer(id, customer)) {
            if (flightRM.reserveFlight(id, customer, flightNumber)) {
                int price = flightRM.queryFlightPrice(id, flightNumber);
                return customerRM.addReservationToCustomer(id, customer, Flight.getKey(flightNumber), String.valueOf(flightNumber), price, Resource.FLIGHT);
            }
        }
        return false;
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        if (customerRM.isCustomer(id, customer)) {
            if (carRM.reserveCar(id, customer, location)) {
                int price = carRM.queryCarsPrice(id, location);
                return customerRM.addReservationToCustomer(id, customer, Car.getKey(location), location, price, Resource.CAR);
            }
        }
        return false;
    }

    @Override
    public boolean reserveRoom(int id, int customer, String location) throws RemoteException {
        if (customerRM.isCustomer(id, customer)) {
            if (roomRM.reserveRoom(id, customer, location)) {
                int price = roomRM.queryRoomsPrice(id, location);
                return customerRM.addReservationToCustomer(id, customer, Hotel.getKey(location), location, price, Resource.ROOM);
            }
        }
        return false;
    }


    //TODO is this the right place?
    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean wantsCar, boolean wantsRoom) throws RemoteException {
        if (customerRM.isCustomer(id, customer)) {
            for (Object obj : flightNumbers) {
                int flightNumber = Integer.parseInt(obj.toString());
                if (flightRM.reserveFlight(id, customer, flightNumber)) {
                    int price = flightRM.queryFlightPrice(id, flightNumber);
                    if (!customerRM.addReservationToCustomer(id, customer, Flight.getKey(flightNumber), String.valueOf(flightNumber), price, Resource.FLIGHT)) {
                        return false;
                    }
                } else return false;
            }

            if (wantsCar) {
                if (carRM.reserveCar(id, customer, location)) {
                    int price = carRM.queryCarsPrice(id, location);
                    if (!customerRM.addReservationToCustomer(id, customer, Car.getKey(location), location, price, Resource.CAR)) {
                        return false;
                    }
                } else return false;
            }

            if (wantsRoom) {
                if (roomRM.reserveRoom(id, customer, location)) {
                    int price = roomRM.queryRoomsPrice(id, location);
                    if (!customerRM.addReservationToCustomer(id, customer, Hotel.getKey(location), location, price, Resource.ROOM)) {
                        return false;
                    }
                } else return false;
            }
            return true;
        }

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
