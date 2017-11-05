package client;

import common.Logger;
import common.RemoteResourceManager;
import common.TransactionalResourceManager;
import middleware.lockManager.DeadlockException;
import middleware.transaction.InvalidTransactionException;
import middleware.transaction.TransactionAbortedException;
import middleware.transaction.TransactionResult;
import middleware.transaction.TransactionStatus;


import java.rmi.RemoteException;
import java.util.Vector;

public class ResourceManager implements RemoteResourceManager {

    private final TransactionalResourceManager rm;

    public ResourceManager(TransactionalResourceManager rm) {
        this.rm = rm;
    }

    public int startTx() throws RemoteException {
        return rm.newTransaction();
    }

    public boolean commitTx(int txId) throws RemoteException, InvalidTransactionException {
        try {
            rm.commitTransaction(txId);
            Logger.print().info("Transaction " + txId + " committed", "ResourceManager");
            return true;
        } catch (InvalidTransactionException e) {
            Logger.print().error("Not a valid transaction. Start a transaction before committing.", "ResourceManager");
            return false;
        } catch (TransactionAbortedException e) {
            Logger.print().error("Transaction " + txId + " aborted", "ResourceManager");
            return false;
        }
    }

    public boolean abortTx(int txId) throws RemoteException, InvalidTransactionException {
        try {
            rm.abortTransaction(txId);
            Logger.print().info("Transaction " + txId + " aborted", "ResourceManager");
            return true;
        } catch (InvalidTransactionException e) {
            Logger.print().error("Not a valid transaction. Start a transaction before aborting.");
            return false;
        }
    }

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        try {
            return rm.addFlight(id, flightNum, flightSeats, flightPrice);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        try {
            return rm.addCars(id, location, numCars, price);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        try {
            return rm.addRooms(id, location, numRooms, price);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        try {
            return rm.newCustomer(id);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return -1;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return -1;
        }
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        Logger.print().error("Unimplemented", "ResourceManager");
        return false;
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        try {
            return rm.deleteFlight(id, flightNum);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        try {
            return rm.deleteCars(id, location);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        try {
            return rm.deleteRooms(id, location);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        try {
            return rm.deleteCustomer(id, customer);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        try {
            return rm.queryFlight(id, flightNumber);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return 0;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        try {
            return rm.queryCars(id, location);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return 0;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        try {
            return rm.queryRooms(id, location);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return 0;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return 0;
        }
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        try {
            return rm.queryCustomerInfo(id, customer);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return "";
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return "";
        }
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        try {
            return rm.queryFlightPrice(id, flightNumber);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return 0;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        try {
            return rm.queryCarsPrice(id, location);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return 0;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        try {
            return rm.queryRoomsPrice(id, location);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return 0;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException {
        try {
            return rm.reserveFlight(id, customer, flightNumber);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        try {
            return rm.reserveCar(id, customer, location);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customer, String location) throws RemoteException {
        try {
            return rm.reserveRoom(id, customer, location);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws RemoteException {
        try {
            return rm.itinerary(id, customer, flightNumbers, location, Car, Room);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

}
