package middleware;

import common.Logger;
import common.NetworkAddress;
import common.RMI;
import resourceManager.RevertibleResourceManager;

import java.net.InetSocketAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Middleware {


    public Middleware(NetworkAddress registryAddress) throws RemoteException, AlreadyBoundException, NotBoundException {

        Registry registry = LocateRegistry.getRegistry(registryAddress.getIp(), registryAddress.getPort());
        RevertibleResourceManager flightRm = (RevertibleResourceManager) registry.lookup(RMI.FLIGHT);
        RevertibleResourceManager carRm = (RevertibleResourceManager) registry.lookup(RMI.CAR);
        RevertibleResourceManager customerRm = (RevertibleResourceManager) registry.lookup(RMI.CUSTOMER);
        RevertibleResourceManager roomRm = (RevertibleResourceManager) registry.lookup(RMI.ROOM);


        if (flightRm == null || carRm == null || customerRm == null || roomRm == null) {
            Logger.print().error("Failed to connect to RMs");
            System.exit(1);
        }

        Logger.print().statement("Connected to RMs.");

        MiddlewareResourceManager middlewareResourceManager = new MiddlewareResourceManager(carRm, flightRm, customerRm, roomRm);


        registry.bind(RMI.MIDDLEWARE, middlewareResourceManager);

        Logger.print().statement("Middleware Ready.");

    }


    public static void main(String[] args) {

    }

}
