package common;

import java.rmi.RemoteException;
import java.util.Vector;

public interface ResourceManager extends RemoteResourceManager {
    @Override
    boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice);

    @Override
    boolean addCars(int id, String location, int numCars, int price);

    @Override
    boolean addRooms(int id, String location, int numRooms, int price);

    @Override
    int newCustomer(int id);

    @Override
    boolean newCustomer(int id, int cid);

    @Override
    boolean deleteFlight(int id, int flightNum);

    @Override
    boolean deleteCars(int id, String location);

    @Override
    boolean deleteRooms(int id, String location);

    @Override
    boolean deleteCustomer(int id, int customer);

    @Override
    int queryFlight(int id, int flightNumber);

    @Override
    int queryCars(int id, String location);

    @Override
    int queryRooms(int id, String location);

    @Override
    String queryCustomerInfo(int id, int customer);

    @Override
    int queryFlightPrice(int id, int flightNumber);

    @Override
    int queryCarsPrice(int id, String location);

    @Override
    int queryRoomsPrice(int id, String location);

    @Override
    boolean reserveFlight(int id, int customer, int flightNumber);

    @Override
    boolean reserveCar(int id, int customer, String location);

    @Override
    boolean reserveRoom(int id, int customer, String locationd);

    @Override
    boolean reserveFlight(int id, int customer, int flightNumber, int count);

    @Override
    boolean reserveCar(int id, int customer, String location, int count);

    @Override
    boolean reserveRoom(int id, int customer, String locationd, int count);

    @Override
    boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room);
}
