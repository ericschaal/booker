// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
//
package resourceManager;

import common.RemoteResourceManager;
import common.Trace;
import common.hashtable.RMHashtable;
import common.resource.*;

import java.util.*;
import java.rmi.RemoteException;


public class ResourceManagerImpl implements RemoteResourceManager {

    public ResourceManagerImpl() { }



    private void removeData(int id, String key) {
        Database.getActiveDb().removeData(id, key);
    }
    private RMItem readData(int id, String key) {
        return Database.getActiveDb().readData(id, key);
    }
    private void writeData(int id, String key, RMItem value) {
        Database.getActiveDb().writeData(id, key, value);
    }


    // deletes the entire item
    private boolean deleteItem(int id, String key) {
        Trace.info("RM::deleteItem(" + id + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem) readData(id, key);
        // Check if there is such an item in the storage
        if (curObj == null) {
            Trace.warn("RM::deleteItem(" + id + ", " + key + ") failed--item doesn't exist");
            return false;
        } else {
            if (curObj.getReserved() == 0) {
                removeData(id, curObj.getKey());
                Trace.info("RM::deleteItem(" + id + ", " + key + ") item deleted");
                return true;
            } else {
                Trace.info("RM::deleteItem(" + id + ", " + key + ") item can't be deleted because some customers reserved it");
                return false;
            }
        } // if
    }

    private boolean freeItem(int id, String key, int count) {
        Trace.info("RM::freeItem( " + id + ", " + key + ", " + key + ", " +count+") called" );
        ReservableItem item  = (ReservableItem) readData(id, key);
        if (item.getReserved()-count < 0) return false;
        item.setReserved(item.getReserved()-count);
        item.setCount(item.getCount()+count);
        return true;
    }

    // reserve an item
    private boolean reserveItem(int id, int customerID, String key, String location, int count) {
        Trace.info("RM::reserveItem( " + id + ", customer=" + customerID + ", " + key + ", " + location + " ) called");
        // check if the item is available
        ReservableItem item = (ReservableItem) readData(id, key);
        if (item == null) {
            Trace.warn("RM::reserveItem( " + id + ", " + customerID + ", " + key + ", " + location + ") failed--item doesn't exist");
            return false;
        } else if (item.getCount() < count) {
            Trace.warn("RM::reserveItem( " + id + ", " + customerID + ", " + key + ", " + location + ") failed--No more items");
            return false;
        } else {

            // decrease the number of available items in the storage
            item.setCount(item.getCount() - count);
            item.setReserved(item.getReserved() + count);

            Trace.info("RM::reserveItem( " + id + ", " + customerID + ", " + key + ", " + location + ") succeeded");
            return true;
        }
    }


    // query the number of available seats/rooms/cars
    private int queryNum(int id, String key) {
        Trace.info("RM::queryNum(" + id + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem) readData(id, key);
        int value = 0;
        if (curObj != null) {
            value = curObj.getCount();
        } // else
        Trace.info("RM::queryNum(" + id + ", " + key + ") returns count=" + value);
        return value;
    }

    // query the price of an item
    private int queryPrice(int id, String key) {
        Trace.info("RM::queryCarsPrice(" + id + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem) readData(id, key);
        int value = 0;
        if (curObj != null) {
            value = curObj.getPrice();
        } // else
        Trace.info("RM::queryCarsPrice(" + id + ", " + key + ") returns cost=$" + value);
        return value;
    }


    // Create a new flight, or add seats to existing flight
    //  NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice)
            throws RemoteException {
        Trace.info("RM::addFlight(" + id + ", " + flightNum + ", $" + flightPrice + ", " + flightSeats + ") called");
        Flight curObj = (Flight) readData(id, Flight.getKey(flightNum));
        if (curObj == null) {
            // doesn't exist...add it
            Flight newObj = new Flight(flightNum, flightSeats, flightPrice);
            writeData(id, newObj.getKey(), newObj);
            Trace.info("RM::addFlight(" + id + ") created new flight " + flightNum + ", seats=" +
                    flightSeats + ", price=$" + flightPrice);
        } else {
            // add seats to existing flight and update the price...
            curObj.setCount(curObj.getCount() + flightSeats);
            if (flightPrice > 0) {
                curObj.setPrice(flightPrice);
            } // if
            writeData(id, curObj.getKey(), curObj);
            Trace.info("RM::addFlight(" + id + ") modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice);
        } // else
        return (true);
    }


    public boolean deleteFlight(int id, int flightNum)
            throws RemoteException {
        return deleteItem(id, Flight.getKey(flightNum));
    }

    // Create a new room location or add rooms to an existing location
    //  NOTE: if price <= 0 and the room location already exists, it maintains its current price
    public boolean addRooms(int id, String location, int count, int price)
            throws RemoteException {
        Trace.info("RM::addRooms(" + id + ", " + location + ", " + count + ", $" + price + ") called");
        Hotel curObj = (Hotel) readData(id, Hotel.getKey(location));
        if (curObj == null) {
            // doesn't exist...add it
            Hotel newObj = new Hotel(location, count, price);
            writeData(id, newObj.getKey(), newObj);
            Trace.info("RM::addRooms(" + id + ") created new room location " + location + ", count=" + count + ", price=$" + price);
        } else {
            // add count to existing object and update price...
            curObj.setCount(curObj.getCount() + count);
            if (price > 0) {
                curObj.setPrice(price);
            } // if
            writeData(id, curObj.getKey(), curObj);
            Trace.info("RM::addRooms(" + id + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
        } // else
        return (true);
    }

    // Delete rooms from a location
    public boolean deleteRooms(int id, String location)
            throws RemoteException {
        return deleteItem(id, Hotel.getKey(location));

    }

    // Create a new car location or add cars to an existing location
    //  NOTE: if price <= 0 and the location already exists, it maintains its current price
    public boolean addCars(int id, String location, int count, int price)
            throws RemoteException {
        Trace.info("RM::addCars(" + id + ", " + location + ", " + count + ", $" + price + ") called");
        Car curObj = (Car) readData(id, Car.getKey(location));
        if (curObj == null) {
            // car location doesn't exist...add it
            Car newObj = new Car(location, count, price);
            writeData(id, newObj.getKey(), newObj);
            Trace.info("RM::addCars(" + id + ") created new location " + location + ", count=" + count + ", price=$" + price);
        } else {
            // add count to existing car location and update price...
            curObj.setCount(curObj.getCount() + count);
            if (price > 0) {
                curObj.setPrice(price);
            } // if
            writeData(id, curObj.getKey(), curObj);
            Trace.info("RM::addCars(" + id + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
        } // else
        return (true);
    }


    // Delete cars from a location
    public boolean deleteCars(int id, String location)
            throws RemoteException {
        return deleteItem(id, Car.getKey(location));
    }


    // Returns the number of empty seats on this flight
    public int queryFlight(int id, int flightNum)
            throws RemoteException {
        return queryNum(id, Flight.getKey(flightNum));
    }

    // Returns the number of reservations for this flight. 
//    public int queryFlightReservations(int id, int flightNum)
//        throws RemoteException
//    {
//        Trace.info("RM::queryFlightReservations(" + id + ", #" + flightNum + ") called" );
//        RMInteger numReservations = (RMInteger) readData( id, Flight.getNumReservationsKey(flightNum) );
//        if ( numReservations == null ) {
//            numReservations = new RMInteger(0);
//        } // if
//        Trace.info("RM::queryFlightReservations(" + id + ", #" + flightNum + ") returns " + numReservations );
//        return numReservations.getValue();
//    }


    // Returns price of this flight
    public int queryFlightPrice(int id, int flightNum)
            throws RemoteException {
        return queryPrice(id, Flight.getKey(flightNum));
    }


    // Returns the number of rooms available at a location
    public int queryRooms(int id, String location)
            throws RemoteException {
        return queryNum(id, Hotel.getKey(location));
    }


    // Returns room price at this location
    public int queryRoomsPrice(int id, String location)
            throws RemoteException {
        return queryPrice(id, Hotel.getKey(location));
    }


    // Returns the number of cars available at a location
    public int queryCars(int id, String location)
            throws RemoteException {
        return queryNum(id, Car.getKey(location));
    }


    // Returns price of cars at this location
    public int queryCarsPrice(int id, String location)
            throws RemoteException {
        return queryPrice(id, Car.getKey(location));
    }

    // Returns data structure containing customer reservation info. Returns null if the
    //  customer doesn't exist. Returns empty RMHashtable if customer exists but has no
    //  reservations.
    public RMHashtable getCustomerReservations(int id, int customerID)
            throws RemoteException {
        Trace.info("RM::getCustomerReservations(" + id + ", " + customerID + ") called");
        Customer cust = (Customer) readData(id, Customer.getKey(customerID));
        if (cust == null) {
            Trace.warn("RM::getCustomerReservations failed(" + id + ", " + customerID + ") failed--customer doesn't exist");
            return null;
        } else {
            return cust.getReservations();
        } // if
    }

    // return a bill
    public String queryCustomerInfo(int id, int customerID)
            throws RemoteException {
        return null;
    }

    // customer functions
    // new customer just returns a unique customer identifier

    public int newCustomer(int id)
            throws RemoteException {
//        Trace.info("INFO: RM::newCustomer(" + id + ") called");
//        // Generate a globally unique ID for the new customer
//        int cid = Integer.parseInt(String.valueOf(id) +
//                String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
//                String.valueOf(Math.round(Math.random() * 100 + 1)));
//        Customer cust = new Customer(cid);
//        writeData(id, cust.getKey(), cust);
//        Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid);
//        return cid;
        return 0;
    }

    // I opted to pass in customerID instead. This makes testing easier
    public boolean newCustomer(int id, int customerID)
            throws RemoteException {
//        Trace.info("INFO: RM::newCustomer(" + id + ", " + customerID + ") called");
//        Customer cust = (Customer) readData(id, Customer.getKey(customerID));
//        if (cust == null) {
//            cust = new Customer(customerID);
//            writeData(id, cust.getKey(), cust);
//            Trace.info("INFO: RM::newCustomer(" + id + ", " + customerID + ") created a new customer");
//            return true;
//        } else {
//            Trace.info("INFO: RM::newCustomer(" + id + ", " + customerID + ") failed--customer already exists");
//            return false;
//        } // else
        return false;
    }


    // Deletes customer from the database. 
    public boolean deleteCustomer(int id, int customerID)
            throws RemoteException {
        return false;
    }



    /*
    // Frees flight reservation record. Flight reservation records help us make sure we
    // don't delete a flight if one or more customers are holding reservations
    public boolean freeFlightReservation(int id, int flightNum)
        throws RemoteException
    {
        Trace.info("RM::freeFlightReservations(" + id + ", " + flightNum + ") called" );
        RMInteger numReservations = (RMInteger) readData( id, Flight.getNumReservationsKey(flightNum) );
        if ( numReservations != null ) {
            numReservations = new RMInteger( Math.max( 0, numReservations.getValue()-1) );
        } // if
        writeData(id, Flight.getNumReservationsKey(flightNum), numReservations );
        Trace.info("RM::freeFlightReservations(" + id + ", " + flightNum + ") succeeded, this flight now has "
                + numReservations + " reservations" );
        return true;
    }
    */


    // Adds car reservation to this customer. 
    public boolean reserveCar(int id, int customerID, String location)
            throws RemoteException {
        return reserveItem(id, customerID, Car.getKey(location), location, 1);
    }


    // Adds room reservation to this customer. 
    public boolean reserveRoom(int id, int customerID, String location)
            throws RemoteException {
        return reserveItem(id, customerID, Hotel.getKey(location), location, 1);
    }

    // Adds flight reservation to this customer.
    public boolean reserveFlight(int id, int customerID, int flightNum)
            throws RemoteException {
        return reserveItem(id, customerID, Flight.getKey(flightNum), String.valueOf(flightNum), 1);
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber, int count) throws RemoteException {
         return reserveItem(id, customer, Flight.getKey(flightNumber), String.valueOf(flightNumber), count);
    }

    @Override
    public boolean reserveCar(int id, int customer, String location, int count) throws RemoteException {
        return reserveItem(id, customer, Car.getKey(location), location, count);
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd, int count) throws RemoteException {
        return reserveItem(id, customer, Hotel.getKey(locationd), locationd, count);
    }

    // Reserve an itinerary
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room)
            throws RemoteException {
        return false;
    }

    public boolean freeCar(int id, int customer, String location, int count) throws RemoteException {
        return freeItem(id, Car.getKey(location), count);
    }

    public boolean freeRoom(int id, int customer, String location, int count) throws RemoteException {
        return freeItem(id, Hotel.getKey(location), count);
    }

    public boolean freeFlight(int id, int customer, int flightNumber, int count) throws RemoteException {
        return freeItem(id, Flight.getKey(flightNumber), count);
    }


}