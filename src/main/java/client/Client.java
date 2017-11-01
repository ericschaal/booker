package client;

import common.NetworkAddress;
import common.RMI;
import middleware.MiddlewareResourceManager;

import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {


    public Client(NetworkAddress registryAddress) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(registryAddress.getIp(), registryAddress.getPort());
        MiddlewareResourceManager resourceManager = (MiddlewareResourceManager) registry.lookup(RMI.MIDDLEWARE);

    }

    public static void main(String[] args) {

    }

}
