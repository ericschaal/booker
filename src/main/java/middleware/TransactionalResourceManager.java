package middleware;

import common.Logger;
import common.ResourceManager;
import middleware.transaction.TransactionResult;
import middleware.transaction.TransactionStatus;
import middleware.transaction.TxManager;

import java.util.Vector;

public class TransactionalResourceManager implements ResourceManager {

    private TxManager globalTxManager;

    public TransactionalResourceManager(TxManager globalTxManager) {
        this.globalTxManager = globalTxManager;
    }

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.addFlight(txId, flightNum, flightSeats, flightPrice))
        );

        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.addCars(txId, location, numCars, price))
        );

        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.addRooms(txId, location, numRooms, price))
        );

        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

    @Override
    public int newCustomer(int id) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.newCustomer(txId))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getIntResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return 0;
        }
    }

    @Override
    public boolean newCustomer(int id, int cid) {
        Logger.print().error("Unimplemented", "TransactionalResourceManager");
        return false;
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.deleteFlight(txId, flightNum))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

    @Override
    public boolean deleteCars(int id, String location) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.deleteCars(txId, location))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

    @Override
    public boolean deleteRooms(int id, String location) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.deleteRooms(txId, location))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int id, int customer) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> {
                    //TODO fetch reservations and delete them.
                    return result.setResult(resourceManager.deleteCustomer(txId, customer));
                }
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

    @Override
    public int queryFlight(int id, int flightNumber) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.queryFlight(txId, flightNumber))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getIntResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return 0;
        }
    }

    @Override
    public int queryCars(int id, String location) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.queryCars(txId, location))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getIntResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return 0;
        }
    }

    @Override
    public int queryRooms(int id, String location) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.queryRooms(txId, location))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getIntResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return 0;
        }
    }

    @Override
    public String queryCustomerInfo(int id, int customer) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.queryCustomerInfo(txId, customer))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getStringResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return "";
        }
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.queryFlightPrice(txId, flightNumber))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getIntResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return 0;
        }
    }

    @Override
    public int queryCarsPrice(int id, String location) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.queryCarsPrice(txId, location))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getIntResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return 0;
        }
    }

    @Override
    public int queryRoomsPrice(int id, String location) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.queryRoomsPrice(txId, location))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getIntResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return 0;
        }
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.reserveFlight(txId, customer, flightNumber))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.reserveCar(txId, customer, location))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customer, String location) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> result.setResult(resourceManager.reserveRoom(txId, customer, location))
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber, int count) {
        Logger.print().error("Unimplemented", "TransactionalResourceManager");
        return false;
    }

    @Override
    public boolean reserveCar(int id, int customer, String location, int count) {
        Logger.print().error("Unimplemented", "TransactionalResourceManager");
        return false;
    }

    @Override
    public boolean reserveRoom(int id, int customer, String location, int count) {
        Logger.print().error("Unimplemented", "TransactionalResourceManager");
        return false;
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) {
        TransactionResult transactionResult = globalTxManager.runInTransaction(
                (resourceManager, txId, result, abort) -> {
                    if (true /*TODO check customer */) {
                        for (String rawFlightNumber : (Vector<String>) flightNumbers) {
                            int flightNumber = Integer.valueOf(rawFlightNumber);
                            if (!resourceManager.reserveFlight(txId, customer, flightNumber))
                                abort.invoke();
                        }
                        if (Car) {
                            if (!resourceManager.reserveCar(txId, customer, location))
                                abort.invoke();
                        }
                        if (Room) {
                            if (!resourceManager.reserveRoom(txId, customer, location))
                                abort.invoke();
                        }

                        //TODO add reservations to customer

                        return result.setResult(true);
                    } else
                        abort.invoke();

                    return result.setResult(false);
                }
        );
        if (transactionResult.getStatus() == TransactionStatus.OK) {
            return transactionResult.getBooleanResult();
        } else {
            Logger.print().warning("Transaction aborted");
            return false;
        }
    }

}
