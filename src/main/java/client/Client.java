package client;

import common.Logger;
import common.NetworkAddress;
import common.RMI;
import common.TransactionalResourceManager;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {


    public Client(NetworkAddress registryAddress) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(registryAddress.getIp(), registryAddress.getPort());
        TransactionalResourceManager resourceManager = (TransactionalResourceManager) registry.lookup(RMI.MIDDLEWARE);

        ResourceManager rm = new ResourceManager(resourceManager);

        Logger.print().statement("Client ready");
    }

    public static void main(String[] args) {

    }

}
