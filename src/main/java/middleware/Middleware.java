package middleware;

import common.*;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class Middleware {


    public Middleware(MiddlewareConfig config) throws RemoteException, AlreadyBoundException, NotBoundException {



        Registry carRegistry = LocateRegistry.getRegistry(config.getCarRegistryAddress().getIp(), config.getCarRegistryAddress().getPort());
        Registry customerRegistry = LocateRegistry.getRegistry(config.getCustomerRegistryAddress().getIp(), config.getCustomerRegistryAddress().getPort());
        Registry flightRegistry = LocateRegistry.getRegistry(config.getFlightRegistryAddress().getIp(), config.getFlightRegistryAddress().getPort());
        Registry roomRegistry = LocateRegistry.getRegistry(config.getRoomRegistryAddress().getIp(), config.getRoomRegistryAddress().getPort());

        Registry registry = LocateRegistry.getRegistry(config.getBindAddress().getIp(), config.getBindAddress().getPort());

        RemoteRevertibleResourceManager flightRm = (RemoteRevertibleResourceManager) flightRegistry.lookup(RMI.FLIGHT);
        RemoteRevertibleResourceManager carRm = (RemoteRevertibleResourceManager) carRegistry.lookup(RMI.CAR);
        RemoteRevertibleResourceManager customerRm = (RemoteRevertibleResourceManager) customerRegistry.lookup(RMI.CUSTOMER);
        RemoteRevertibleResourceManager roomRm = (RemoteRevertibleResourceManager) roomRegistry.lookup(RMI.ROOM);


        if (flightRm == null || carRm == null || customerRm == null || roomRm == null) {
            Logger.print().error("Failed to connect to RMs");
            System.exit(1);
        }

        Logger.print().statement("Connected to RMs.");

        MiddlewareResourceManager obj = new MiddlewareResourceManager(carRm, flightRm, customerRm, roomRm);

        TransactionalResourceManager rm = (TransactionalResourceManager) UnicastRemoteObject.exportObject(obj, 0);

        registry.rebind(RMI.MIDDLEWARE, rm);

        Logger.print().statement("Middleware Ready.");

    }



}
