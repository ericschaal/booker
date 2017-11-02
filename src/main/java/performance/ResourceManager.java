package performance;

import common.TransactionalResourceManager;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class ResourceManager implements Serializable {

    private TransactionalResourceManager rm;

    private Queue<Integer> aliveCustomers = new PriorityQueue<>();

    private int idCounter = 0;

    public ResourceManager(TransactionalResourceManager middlewareRM) {
        this.rm = middlewareRM;
    }

    public int runRandom() throws RemoteException {
        int choice = ThreadLocalRandom.current().nextInt(0, 6 + 1);
        switch (choice) {
            case 0:
                tx0();
                break;
            case 1:
                if (!aliveCustomers.isEmpty()) {
                    tx1();
                    break;
                }
            case 2:
                tx2();
                break;
            case 3:
                tx3();
                break;
            case 4:
                tx4();
                break;
            case 5:
                tx5();
                break;
            case 6:
                tx6();
                break;
            default:
                throw new RuntimeException("Random number not in range!");
        }
        return choice;
    }

    /**
     * Creates a customer and a flight
     * Reserves flight
     *
     * @throws RemoteException
     */
    private void tx0() throws RemoteException {
        rm.runInTransaction(((rm1, txId, result, abort) -> {
            int cid = rm1.newCustomer(txId);

            if (cid == 0)
                abort.invoke();

            aliveCustomers.add(cid);

            int flightId = idCounter++;
            String location1 = String.valueOf(idCounter++);
            String location2 = String.valueOf(idCounter++);

            if (!rm1.addFlight(txId, flightId, 20, 500))
                abort.invoke();
            if (!rm1.reserveFlight(txId, cid, flightId))
                abort.invoke();

            if (!rm1.addCars(txId, location1, 12, 50))
                abort.invoke();
            if (!rm1.reserveCar(txId, cid, location2))
                abort.invoke();

            if (!rm1.addRooms(txId, location1, 12, 50))
                abort.invoke();
            if (!rm1.reserveRoom(txId, cid, location2))
                abort.invoke();


            return result.setResult(true);
        }));
    }

    /**
     * Deletes a customer
     */
    private void tx1() throws RemoteException {
        rm.runInTransaction((rm1, txId, result, abort) -> {
                    int cid = aliveCustomers.peek();

                    if (!rm1.deleteCustomer(txId, cid))
                        abort.invoke();

                    aliveCustomers.remove();
                    return result.setResult(true);
                }
        );
    }

    /**
     * Creates an itinerary with two flights, car and room
     * @throws RemoteException
     */
    private void tx2() throws RemoteException {
        rm.runInTransaction(((rm1, txId, result, abort) -> {

            int cid = rm1.newCustomer(txId);
            aliveCustomers.add(cid);

            int flightId1 = idCounter++;
            int flightId2 = idCounter++;
            Vector flights = new Vector();
            flights.add(flightId1);
            flights.add(flightId2);

            String location = String.valueOf(idCounter++);

            if (!rm1.addFlight(txId, flightId1, 20, 500))
                abort.invoke();
            if (!rm1.addFlight(txId, flightId2, 352, 3287))
                abort.invoke();

            if (!rm1.addCars(txId, location, 20, 100))
                abort.invoke();

            if(!rm1.addRooms(txId, location, 20, 98))
                abort.invoke();

            if(!rm1.itinerary(txId,cid, flights, location, true, true))
                abort.invoke();

            return result.setResult(true);
        }));
    }

    /**
     * Creates 4 flights and query info about them
     * ONE RM INVOLVED
     * @throws RemoteException
     */
    private void tx3() throws RemoteException {
        rm.runInTransaction(((rm1, txId, result, abort) -> {
            int flightId1 = idCounter++;
            int flightId2 = idCounter++;
            int flightId3 = idCounter++;
            int flightId4 = idCounter++;

            if (!rm1.addFlight(txId, flightId1, 20, 500))
                abort.invoke();
            if (!rm1.addFlight(txId, flightId2, 352, 3287))
                abort.invoke();
            if (!rm1.addFlight(txId, flightId3, 20, 500))
                abort.invoke();
            if (!rm1.addFlight(txId, flightId4, 352, 3287))
                abort.invoke();

            rm1.queryFlight(txId, flightId1);
            rm1.queryFlight(txId, flightId2);
            rm1.queryFlight(txId, flightId3);
            rm1.queryFlight(txId, flightId4);

            return result.setResult(true);
        }));
    }

    /**
     * Creates a new customer and a new flight
     * Reserves flight and fails to reserve a car
     * Failing transaction
     * @throws RemoteException
     */
    private void tx4() throws RemoteException {
        rm.runInTransaction(((rm1, txId, result, abort) -> {
            int cid = rm1.newCustomer(txId);

            if (cid == 0)
                abort.invoke();

            aliveCustomers.add(cid);

            int flightId = idCounter++;

            if (!rm1.addFlight(txId, flightId, 20, 500))
                abort.invoke();
            if (!rm1.reserveFlight(txId, cid, flightId))
                abort.invoke();
            if (!rm1.reserveCar(txId, cid, "notvalid"))
                abort.invoke();

            return result.setResult(true);
        }));
    }

    /**
     * Creates a customer
     * Reserves two cars at two different locations
     * @throws RemoteException
     */
    private void tx5() throws RemoteException {
        rm.runInTransaction(((rm1, txId, result, abort) -> {
            int cid = rm1.newCustomer(txId);

            if (cid == 0)
                abort.invoke();

            aliveCustomers.add(cid);

            String location1 = String.valueOf(idCounter++);
            String location2 = String.valueOf(idCounter++);

            if (!rm1.addCars(txId, location1, 12, 50))
                abort.invoke();
            if (!rm1.reserveCar(txId, cid, location2))
                abort.invoke();

            if (!rm1.addCars(txId, location1, 12, 50))
                abort.invoke();
            if (!rm1.reserveCar(txId, cid, location2))
                abort.invoke();

            return result.setResult(true);
        }));
    }

    /**
     * Creates 10 rooms;
     * ONE RM INVOLVED
     * @throws RemoteException
     */
    private void tx6() throws RemoteException {
        rm.runInTransaction(((rm1, txId, result, abort) -> {

            String location1 = String.valueOf(idCounter++);
            String location2 = String.valueOf(idCounter++);
            String location3 = String.valueOf(idCounter++);
            String location4 = String.valueOf(idCounter++);
            String location5 = String.valueOf(idCounter++);
            String location6 = String.valueOf(idCounter++);
            String location7 = String.valueOf(idCounter++);
            String location8 = String.valueOf(idCounter++);
            String location9 = String.valueOf(idCounter++);
            String location10 = String.valueOf(idCounter++);


            if (!rm1.addCars(txId, location1, 12, 50))
                abort.invoke();
            if (!rm1.addCars(txId, location2, 12, 50))
                abort.invoke();
            if (!rm1.addCars(txId, location3, 12, 50))
                abort.invoke();
            if (!rm1.addCars(txId, location4, 12, 50))
                abort.invoke();
            if (!rm1.addCars(txId, location5, 12, 50))
                abort.invoke();
            if (!rm1.addCars(txId, location6, 12, 50))
                abort.invoke();
            if (!rm1.addCars(txId, location7, 12, 50))
                abort.invoke();
            if (!rm1.addCars(txId, location8, 12, 50))
                abort.invoke();
            if (!rm1.addCars(txId, location9, 12, 50))
                abort.invoke();
            if (!rm1.addCars(txId, location10, 12, 50))
                abort.invoke();


            return result.setResult(true);
        }));
    }



}