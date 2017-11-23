package middleware;

import common.hashtable.RMHashtable;
import common.io.Logger;
import common.io.Trace;
import common.resource.*;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import middleware.lockManager.DeadlockException;
import middleware.lockManager.LockManager;
import middleware.perf.MiddlewareStatistics;
import middleware.rmi.RMIManager;
import middleware.transaction.*;

public class MiddlewareResourceManager implements TransactionalResourceManager {

    private final TxManager globalTxManager;


    public MiddlewareResourceManager() {

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
        if (!globalTxManager.commitTransaction(txId)) throw new InvalidTransactionException();
    }

    @Override
    public void abortTransaction(int txId) throws RemoteException, InvalidTransactionException {
        if (!globalTxManager.abortTransaction(txId)) throw new InvalidTransactionException();
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
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.FLIGHT);
            return RMIManager.flightRm().addFlight(id, flightNum, flightSeats, flightPrice);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.CAR);
            return RMIManager.carRm().addCars(id, location, numCars, price);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.ROOM);
            return RMIManager.roomRm().addRooms(id, location, numRooms, price);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public int newCustomer(int id) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
            return RMIManager.customerRm().newCustomer(id);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        Logger.print().error("Unimplemented", "MiddlewareConcurrentResourceManager");
        return false;
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.FLIGHT);
            return RMIManager.flightRm().deleteFlight(id, flightNum);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.CAR);
            return RMIManager.carRm().deleteCars(id, location);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.ROOM);
            return RMIManager.roomRm().deleteRooms(id, location);
        } catch (RemoteException e) {
            return false;
        }
    }


    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException, DeadlockException, InvalidTransactionException {

        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
            lockAndEnlist(id, LockManager.WRITE, Resource.FLIGHT); // Preventive locking.
            lockAndEnlist(id, LockManager.WRITE, Resource.ROOM); // Preventive locking.
            lockAndEnlist(id, LockManager.WRITE, Resource.CAR); // Preventive locking.

            RMHashtable reservationHT = RMIManager.customerRm().getCustomerReservations(id, customer);
            if (reservationHT != null) {
                for (Enumeration e = reservationHT.elements(); e.hasMoreElements(); ) {
                    ReservedItem reservedItem = (ReservedItem) (e.nextElement());
                    Trace.info("RM::deleteCustomer(" + id + ", " + customer + ") has reserved " + reservedItem.getResourceType() + " " + reservedItem.getCount() + " times");
                    switch (reservedItem.getResourceType()) {
                        case CAR:
                            if (!RMIManager.carRm().freeCar(id, reservedItem.getLocation(), reservedItem.getCount())) {
                                return false;
                            }
                            break;
                        case ROOM:
                            if (!RMIManager.roomRm().freeRoom(id, reservedItem.getLocation(), reservedItem.getCount())) {
                                return false;
                            }
                            break;
                        case FLIGHT:
                            if (!RMIManager.flightRm().freeFlight(id, Integer.valueOf(reservedItem.getLocation()), reservedItem.getCount())) {
                                return false;
                            }
                            break;
                        case CUSTOMER:
                            Trace.info("RM::deleteCustomer error customer is not reservable.");
                            return false;
                    }
                }
                return RMIManager.customerRm().deleteCustomer(id, customer);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.READ, Resource.FLIGHT);
            return RMIManager.flightRm().queryFlight(id, flightNumber);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.READ, Resource.CAR);
            return RMIManager.carRm().queryCars(id, location);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.READ, Resource.ROOM);
            return RMIManager.roomRm().queryRooms(id, location);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.READ, Resource.CUSTOMER);
            return RMIManager.customerRm().queryCustomerInfo(id, customer);
        } catch (RemoteException e) {
            return "";
        }
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.READ, Resource.FLIGHT);
            return RMIManager.flightRm().queryFlightPrice(id, flightNumber);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.READ, Resource.CAR);
            return RMIManager.carRm().queryCarsPrice(id, location);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.READ, Resource.ROOM);
            return RMIManager.roomRm().queryRoomsPrice(id, location);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
            lockAndEnlist(id, LockManager.WRITE, Resource.FLIGHT);
            if (RMIManager.customerRm().isCustomer(id, customer)) {
                if (RMIManager.flightRm().reserveFlight(id, customer, flightNumber)) {
                    int price = RMIManager.flightRm().queryFlightPrice(id, flightNumber);
                    return RMIManager.customerRm().addReservationToCustomer(id, customer, Flight.getKey(flightNumber), String.valueOf(flightNumber), price, Resource.FLIGHT);
                }
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
            lockAndEnlist(id, LockManager.WRITE, Resource.CAR);
            if (RMIManager.customerRm().isCustomer(id, customer)) {
                if (RMIManager.carRm().reserveCar(id, customer, location)) {
                    int price = RMIManager.carRm().queryCarsPrice(id, location);
                    return RMIManager.customerRm().addReservationToCustomer(id, customer, Car.getKey(location), location, price, Resource.CAR);
                }
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customer, String location) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
            lockAndEnlist(id, LockManager.WRITE, Resource.ROOM);
            if (RMIManager.customerRm().isCustomer(id, customer)) {
                if (RMIManager.roomRm().reserveRoom(id, customer, location)) {
                    int price = RMIManager.roomRm().queryRoomsPrice(id, location);
                    return RMIManager.customerRm().addReservationToCustomer(id, customer, Hotel.getKey(location), location, price, Resource.ROOM);
                }
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }


    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean wantsCar, boolean wantsRoom) throws RemoteException, DeadlockException, InvalidTransactionException {
        try {
            lockAndEnlist(id, LockManager.WRITE, Resource.CUSTOMER);
            lockAndEnlist(id, LockManager.WRITE, Resource.FLIGHT);
            if (RMIManager.customerRm().isCustomer(id, customer)) {
                for (Object obj : flightNumbers) {
                    int flightNumber = Integer.parseInt(obj.toString());
                    if (RMIManager.flightRm().reserveFlight(id, customer, flightNumber)) {
                        int price = RMIManager.flightRm().queryFlightPrice(id, flightNumber);
                        if (!RMIManager.customerRm().addReservationToCustomer(id, customer, Flight.getKey(flightNumber), String.valueOf(flightNumber), price, Resource.FLIGHT)) {
                            return false;
                        }
                    } else return false;
                }

                if (wantsCar) {
                    lockAndEnlist(id, LockManager.WRITE, Resource.CAR);
                    if (RMIManager.carRm().reserveCar(id, customer, location)) {
                        int price = RMIManager.carRm().queryCarsPrice(id, location);
                        if (!RMIManager.customerRm().addReservationToCustomer(id, customer, Car.getKey(location), location, price, Resource.CAR)) {
                            return false;
                        }
                    } else return false;
                }

                if (wantsRoom) {
                    lockAndEnlist(id, LockManager.WRITE, Resource.ROOM);
                    if (RMIManager.roomRm().reserveRoom(id, customer, location)) {
                        int price = RMIManager.roomRm().queryRoomsPrice(id, location);
                        if (!RMIManager.customerRm().addReservationToCustomer(id, customer, Hotel.getKey(location), location, price, Resource.ROOM)) {
                            return false;
                        }
                    } else return false;
                }
                return true;
            }

            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    private void printRunTimeStats() {
        System.out.println("---- Statistics ----");
        System.out.println("");
        System.out.println("Transactions Executed " + MiddlewareStatistics.instance.getTransactionExecuted());
        System.out.println("Transactions Committed " + MiddlewareStatistics.instance.getTransactionCommitted());
        System.out.println("Transactions Aborted " + MiddlewareStatistics.instance.getTransactionAborted());
        System.out.println("-----------");
        System.out.println("Deadlock Count " + MiddlewareStatistics.instance.getDeadlockCount());
        System.out.println("-----------");
        System.out.println("Average Transaction Commit Time " + MiddlewareStatistics.instance.getAverageCommitTime().getMean() + "ms");
        System.out.println("Average Transaction Abort Time " + MiddlewareStatistics.instance.getAverageAbortTime().getMean() + "ms");
        System.out.println("-----------");
        System.out.println("Average WRITE Lock Grant Time " + MiddlewareStatistics.instance.getAverageWriteLockGrant().getMean() + "ms");
        System.out.println("Average READ Lock Grant Time " + MiddlewareStatistics.instance.getAverageReadLockGrant().getMean() + "ms");
    }

    @Override
    public void shutdown() throws RemoteException {
        try {
            printRunTimeStats();

            RMIManager.flightRm().shutdown();
            RMIManager.carRm().shutdown();
            RMIManager.customerRm().shutdown();
            RMIManager.roomRm().shutdown();

            Timer timedShutdown = new Timer();
            timedShutdown.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 2000);
        } catch (RemoteException e) {
        }
    }

    public EndPointResourceManager getCarRM() {
        return RMIManager.carRm();
    }

    public EndPointResourceManager getFlightRM() {
        return RMIManager.flightRm();
    }

    public EndPointResourceManager getCustomerRM() {
        return RMIManager.customerRm();
    }

    public EndPointResourceManager getRoomRM() {
        return RMIManager.roomRm();
    }
}
