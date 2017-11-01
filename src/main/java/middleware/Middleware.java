package middleware;

import common.*;
import resourceManager.RevertibleResourceManager;

import java.net.InetSocketAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class Middleware {


    public Middleware(NetworkAddress registryAddress) throws RemoteException, AlreadyBoundException, NotBoundException {

        Registry registry = LocateRegistry.getRegistry(registryAddress.getIp(), registryAddress.getPort());
        RemoteRevertibleResourceManager flightRm = (RemoteRevertibleResourceManager) registry.lookup(RMI.FLIGHT);
        RemoteRevertibleResourceManager carRm = (RemoteRevertibleResourceManager) registry.lookup(RMI.CAR);
        RemoteRevertibleResourceManager customerRm = (RemoteRevertibleResourceManager) registry.lookup(RMI.CUSTOMER);
        RemoteRevertibleResourceManager roomRm = (RemoteRevertibleResourceManager) registry.lookup(RMI.ROOM);


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


    public static void main(String[] args) {

    }

}
