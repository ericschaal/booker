package client;

import client.rmi.RMIManager;
import common.io.Logger;
import common.resource.RemoteResourceManager;
import middleware.lockManager.DeadlockException;
import middleware.transaction.InvalidTransactionException;
import middleware.transaction.TransactionAbortedException;


import java.rmi.RemoteException;
import java.util.Vector;

public class ResourceManager implements RemoteResourceManager {
    

    public ResourceManager() {
    }

    public int startTx() throws RemoteException {
        return RMIManager.rm().newTransaction();
    }

    public boolean commitTx(int txId) throws RemoteException, InvalidTransactionException {
        try {
            RMIManager.rm().commitTransaction(txId);
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
            RMIManager.rm().abortTransaction(txId);
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
            return RMIManager.rm().addFlight(id, flightNum, flightSeats, flightPrice);
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
            return RMIManager.rm().addCars(id, location, numCars, price);
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
            return RMIManager.rm().addRooms(id, location, numRooms, price);
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
            return RMIManager.rm().newCustomer(id);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return 0;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return 0;
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
            return RMIManager.rm().deleteFlight(id, flightNum);
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
            return RMIManager.rm().deleteCars(id, location);
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
            return RMIManager.rm().deleteRooms(id, location);
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
            return RMIManager.rm().deleteCustomer(id, customer);
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
            return RMIManager.rm().queryFlight(id, flightNumber);
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
            return RMIManager.rm().queryCars(id, location);
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
            return RMIManager.rm().queryRooms(id, location);
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
            return RMIManager.rm().queryCustomerInfo(id, customer);
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
            return RMIManager.rm().queryFlightPrice(id, flightNumber);
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
            return RMIManager.rm().queryCarsPrice(id, location);
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
            return RMIManager.rm().queryRoomsPrice(id, location);
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
            return RMIManager.rm().reserveFlight(id, customer, flightNumber);
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
            return RMIManager.rm().reserveCar(id, customer, location);
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
            return RMIManager.rm().reserveRoom(id, customer, location);
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
            return RMIManager.rm().itinerary(id, customer, flightNumbers, location, Car, Room);
        } catch (DeadlockException e) {
            Logger.print().warning("Deadlock.");
            return false;
        } catch (InvalidTransactionException e) {
            Logger.print().error(e.getMessage());
            return false;
        }
    }

    @Override
    public void shutdown() throws RemoteException {
        RMIManager.rm().shutdown();
    }
}
