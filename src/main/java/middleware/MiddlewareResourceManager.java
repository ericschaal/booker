package middleware;

import common.*;
import common.hashtable.RMHashtable;
import common.resource.*;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import middleware.lockManager.DeadlockException;
import middleware.lockManager.LockManager;
import middleware.transaction.*;

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

        this.globalTxManager = new TxManager(this, 3 * 60 * 1000 /* 3 minutes */);
    }


    private void lockAndEnlist(int transactionId, int lockLevel, Resource resource) throws DeadlockException, InvalidTransactionException {
        if (transactionId >= 0) {
            if (!globalTxManager.transactionExists(transactionId))
                throw new InvalidTransactionException("Transaction doesn't exist. Use id:-1 to run out of a transaction.");
            LockManager.get().Lock(transactionId, resource.name(), lockLevel);
            globalTxManager.enlist(transactionId, resource);
        }
    }

    @Override
    public void commitTransaction(int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException {
        if (! globalTxManager.commitTransaction(txId)) throw new InvalidTransactionException();
    }

    @Override
    public void abortTransaction(int txId) throws RemoteException, InvalidTransactionException {
        if (! globalTxManager.abortTransaction(txId)) throw new InvalidTransactionException();
    }

    @Override
    public int newTransaction() throws RemoteException {
        return globalTxManager.startNewTransaction();
    }

    @Override
    public TransactionResult runInTransaction(TransactionBody<MiddlewareResourceManager, Integer, TransactionResult, Function0<Unit>, TransactionResult> body) throws RemoteException {
        return globalTxManager.runInTransaction(body);
    }

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.FLIGHT);
        return flightRM.addFlight(id, flightNum, flightSeats, flightPrice);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.CAR);
        return carRM.addCars(id, location, numCars, price);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.ROOM);
        return roomRM.addRooms(id, location, numRooms, price);
    }

    @Override
    public int newCustomer(int id) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
        return customerRM.newCustomer(id);
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.FLIGHT);
        return flightRM.deleteFlight(id, flightNum);
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.CAR);
        return carRM.deleteCars(id, location);
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.ROOM);
        return roomRM.deleteRooms(id, location);
    }


    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException, DeadlockException, InvalidTransactionException {

        lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
        lockAndEnlist(id, LockManager.WRITE, Resource.FLIGHT); // Preventive locking.
        lockAndEnlist(id, LockManager.WRITE, Resource.ROOM); // Preventive locking.
        lockAndEnlist(id, LockManager.WRITE, Resource.CAR); // Preventive locking.

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
    public int queryFlight(int id, int flightNumber) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.READ, Resource.FLIGHT);
        return flightRM.queryFlight(id, flightNumber);
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.READ, Resource.CAR);
        return carRM.queryCars(id, location);
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.READ, Resource.ROOM);
        return roomRM.queryRooms(id, location);
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.READ, Resource.CUSTOMER);
        return customerRM.queryCustomerInfo(id, customer);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.READ, Resource.FLIGHT);
        return flightRM.queryFlightPrice(id, flightNumber);
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.READ, Resource.CAR);
        return carRM.queryCarsPrice(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.READ, Resource.ROOM);
        return roomRM.queryRoomsPrice(id, location);
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
        lockAndEnlist(id, LockManager.WRITE, Resource.FLIGHT);
        if (customerRM.isCustomer(id, customer)) {
            if (flightRM.reserveFlight(id, customer, flightNumber)) {
                int price = flightRM.queryFlightPrice(id, flightNumber);
                return customerRM.addReservationToCustomer(id, customer, Flight.getKey(flightNumber), String.valueOf(flightNumber), price, Resource.FLIGHT);
            }
        }
        return false;
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
        lockAndEnlist(id, LockManager.WRITE, Resource.CAR);
        if (customerRM.isCustomer(id, customer)) {
            if (carRM.reserveCar(id, customer, location)) {
                int price = carRM.queryCarsPrice(id, location);
                return customerRM.addReservationToCustomer(id, customer, Car.getKey(location), location, price, Resource.CAR);
            }
        }
        return false;
    }

    @Override
    public boolean reserveRoom(int id, int customer, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
        lockAndEnlist(id, LockManager.WRITE, Resource.ROOM);
        if (customerRM.isCustomer(id, customer)) {
            if (roomRM.reserveRoom(id, customer, location)) {
                int price = roomRM.queryRoomsPrice(id, location);
                return customerRM.addReservationToCustomer(id, customer, Hotel.getKey(location), location, price, Resource.ROOM);
            }
        }
        return false;
    }


    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean wantsCar, boolean wantsRoom) throws RemoteException, DeadlockException, InvalidTransactionException {
        lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
        lockAndEnlist(id, LockManager.WRITE, Resource.FLIGHT);
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
                lockAndEnlist(id, LockManager.WRITE, Resource.CAR);
                if (carRM.reserveCar(id, customer, location)) {
                    int price = carRM.queryCarsPrice(id, location);
                    if (!customerRM.addReservationToCustomer(id, customer, Car.getKey(location), location, price, Resource.CAR)) {
                        return false;
                    }
                } else return false;
            }

            if (wantsRoom) {
                lockAndEnlist(id, LockManager.WRITE, Resource.ROOM);
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

    private void printRunTimeStats() {
        System.out.println("---- Statistics ----");
        System.out.println("");
        System.out.println("Transactions Executed " +  MiddlewareStatistics.instance.getTransactionExecuted());
        System.out.println("Transactions Committed " +  MiddlewareStatistics.instance.getTransactionCommitted());
        System.out.println("Transactions Aborted " +  MiddlewareStatistics.instance.getTransactionAborted());
        System.out.println("-----------");
        System.out.println("Deadlock Count " +  MiddlewareStatistics.instance.getDeadlockCount());
        System.out.println("-----------");
        System.out.println("Average Transaction Commit Time " +  MiddlewareStatistics.instance.getAverageCommitTime().getMean() + "ms");
        System.out.println("Average Transaction Abort Time " +  MiddlewareStatistics.instance.getAverageAbortTime().getMean()+ "ms");
        System.out.println("-----------");
        System.out.println("Average WRITE Lock Grant Time " +  MiddlewareStatistics.instance.getAverageWriteLockGrant().getMean()+ "ms");
        System.out.println("Average READ Lock Grant Time " +   MiddlewareStatistics.instance.getAverageReadLockGrant().getMean()+ "ms");
    }

    @Override
    public void shutdown() throws RemoteException {
        printRunTimeStats();
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
