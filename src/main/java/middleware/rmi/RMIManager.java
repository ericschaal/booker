package middleware.rmi;

import common.io.Logger;
import common.resource.EndPointResourceManager;
import common.resource.RMI;
import common.resource.Resource;
import common.resource.TransactionalResourceManager;
import middleware.MiddlewareResourceManager;
import middleware.config.MiddlewareConfig;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

public class RMIManager {

    private static RMIManager instance;

    private MiddlewareResourceManager obj;

    private EndPointResourceManager flightRm;
    private EndPointResourceManager carRm;
    private EndPointResourceManager customerRm;
    private EndPointResourceManager roomRm;

    private Registry carRegistry;
    private Registry customerRegistry;
    private Registry flightRegistry;
    private Registry roomRegistry;

    private Registry middlewareRegistry;

    private Timer ttl = new Timer();

    private boolean verify(Resource r) {
        try {
            switch (r) {
                case CAR:
                    if (carRm == null) return false;
                    carRm.verify();
                    break;
                case ROOM:
                    if (roomRm == null) return false;
                    roomRm.verify();
                    break;
                case FLIGHT:
                    if (flightRm == null) return false;
                    flightRm.verify();
                    break;
                case CUSTOMER:
                    if (customerRm == null) return false;
                    customerRm.verify();
                    break;
            }
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    private void tryConnectTo(Resource r) {
        try {
            switch (r) {
                case CAR:
                    carRm = (EndPointResourceManager) carRegistry.lookup(RMI.CAR);
                    break;
                case ROOM:
                    roomRm = (EndPointResourceManager) roomRegistry.lookup(RMI.ROOM);
                    break;
                case FLIGHT:
                    flightRm = (EndPointResourceManager) flightRegistry.lookup(RMI.FLIGHT);
                    break;
                case CUSTOMER:
                    customerRm = (EndPointResourceManager) customerRegistry.lookup(RMI.CUSTOMER);
                    break;
            }

            if (verify(r)) {
                Logger.print().info("Connected to RM " + r, "RMIManager");
            }

        } catch (Exception e) {
            Logger.print().error("Failed to reconnect to RM " + r.name(), "RMIManager");
        }
    }

    private RMIManager(MiddlewareConfig config) throws RemoteException {

        Logger.print().info("RM Registry Lookup", "RMIManager");

        carRegistry = LocateRegistry.getRegistry(config.getCarRegistryAddress().getIp(), config.getCarRegistryAddress().getPort());
        customerRegistry = LocateRegistry.getRegistry(config.getCustomerRegistryAddress().getIp(), config.getCustomerRegistryAddress().getPort());
        flightRegistry = LocateRegistry.getRegistry(config.getFlightRegistryAddress().getIp(), config.getFlightRegistryAddress().getPort());
        roomRegistry = LocateRegistry.getRegistry(config.getRoomRegistryAddress().getIp(), config.getRoomRegistryAddress().getPort());
        middlewareRegistry = LocateRegistry.getRegistry(config.getBindAddress().getIp(), config.getBindAddress().getPort());


        Logger.print().info("Connecting to RMs", "RMIManager");

        tryConnectTo(Resource.CAR);
        tryConnectTo(Resource.FLIGHT);
        tryConnectTo(Resource.CUSTOMER);
        tryConnectTo(Resource.ROOM);


        if (this.carRm == null || this.flightRm == null || this.roomRm == null || this.customerRm == null) {
            throw new RemoteException("Failed to start");
        }


        Logger.print().info("Binding to Registry", "RMIManager");
        obj = new MiddlewareResourceManager();
        TransactionalResourceManager rm = (TransactionalResourceManager) UnicastRemoteObject.exportObject(obj, 0);

        middlewareRegistry.rebind(RMI.MIDDLEWARE, rm);


        Logger.print().statement("Bind successful.", "RMIManager");


        ttl.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (Resource r : Resource.values()) {
                    if (!verify(r)) {
                        Logger.print().error("RM " + r.name() + " is down.", "RMIManager");
                        Logger.print().info("Trying to reconnect to RM " + r);
                        tryConnectTo(r);
                    }
                }
            }
        }, 0, 3 * 1000);

    }

    public static void init(MiddlewareConfig config) throws RemoteException {
        if (instance != null) {
            throw new RuntimeException("RMI Manager already initialized");
        } else {
            instance = new RMIManager(config);
        }
    }

    public void healthCheck() {
        obj.healthCheck();
    }


    public static void txHealthCheck() {
        instance.healthCheck();
    }

    public static EndPointResourceManager getRmForResource(Resource r) {
        switch (r) {
            case CUSTOMER:
                return customerRm();
            case FLIGHT:
                return flightRm();
            case ROOM:
                return roomRm();
            case CAR:
                return carRm();
            default:
                throw new IllegalArgumentException();
        }
    }

    public static EndPointResourceManager flightRm() {
        return instance.flightRm;
    }

    public static EndPointResourceManager carRm() {
        return instance.carRm;
    }

    public static EndPointResourceManager customerRm() {
        return instance.customerRm;
    }

    public static EndPointResourceManager roomRm() {
        return instance.roomRm;
    }
}
