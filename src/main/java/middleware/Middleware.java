package middleware;


import common.io.Logger;
import common.resource.RMI;
import common.resource.EndPointResourceManager;
import common.resource.TransactionalResourceManager;
import middleware.config.MiddlewareConfig;
import middleware.io.MiddlewareIOManager;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class Middleware {


    public Middleware(MiddlewareConfig config) throws RemoteException, AlreadyBoundException, NotBoundException {


        MiddlewareIOManager ioManager = new MiddlewareIOManager("./data/");


        Registry carRegistry = LocateRegistry.getRegistry(config.getCarRegistryAddress().getIp(), config.getCarRegistryAddress().getPort());
        Registry customerRegistry = LocateRegistry.getRegistry(config.getCustomerRegistryAddress().getIp(), config.getCustomerRegistryAddress().getPort());
        Registry flightRegistry = LocateRegistry.getRegistry(config.getFlightRegistryAddress().getIp(), config.getFlightRegistryAddress().getPort());
        Registry roomRegistry = LocateRegistry.getRegistry(config.getRoomRegistryAddress().getIp(), config.getRoomRegistryAddress().getPort());

        Registry registry = LocateRegistry.getRegistry(config.getBindAddress().getIp(), config.getBindAddress().getPort());

        EndPointResourceManager flightRm = (EndPointResourceManager) flightRegistry.lookup(RMI.FLIGHT);
        EndPointResourceManager carRm = (EndPointResourceManager) carRegistry.lookup(RMI.CAR);
        EndPointResourceManager customerRm = (EndPointResourceManager) customerRegistry.lookup(RMI.CUSTOMER);
        EndPointResourceManager roomRm = (EndPointResourceManager) roomRegistry.lookup(RMI.ROOM);


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
