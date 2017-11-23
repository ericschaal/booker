package client.rmi;

import common.io.Logger;
import common.net.NetworkAddress;
import common.resource.RMI;
import common.resource.TransactionalResourceManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIManager {

    private static RMIManager instance;

    private Registry registry;
    private TransactionalResourceManager resourceManager;


    private void tryConnect() {
        try {
            resourceManager = (TransactionalResourceManager) registry.lookup(RMI.MIDDLEWARE);

            if (verify()) {
                Logger.print().info("Connected to Middleware", "RMIManager");
            }

        } catch (Exception e) {
            Logger.print().info("Failed to connect to Middleware", "RMIManager");
        }
    }

    private boolean verify() {
        try {
            if (resourceManager == null) return false;
            resourceManager.verify();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public RMIManager(NetworkAddress registryAddress) throws RemoteException {
        Logger.print().info("Middleware Registry Lookup", "RMIManager");
        registry = LocateRegistry.getRegistry(registryAddress.getIp(), registryAddress.getPort());
        Logger.print().info("Connecting to Middleware", "RMIManager");
        tryConnect();

        if (resourceManager == null) throw new RemoteException("Failed to start");

    }

    public static void init(NetworkAddress registryAddress) throws RemoteException {
        if (instance != null) {
            throw new RuntimeException("RMIManager already initialized");
        }
        instance = new RMIManager(registryAddress);
    }

    public static TransactionalResourceManager rm() {
        return instance.resourceManager;
    }

}
