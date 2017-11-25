package client.rmi;

import common.io.Logger;
import common.net.NetworkAddress;
import common.resource.RMI;
import common.resource.Resource;
import common.resource.TransactionalResourceManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public class RMIManager {

    private static RMIManager instance;

    private Registry registry;
    private TransactionalResourceManager resourceManager;
    private Timer ttl = new Timer();


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

        ttl.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    if (!verify()) {
                        Logger.print().error("Middleware is down.", "RMIManager");
                        Logger.print().info("Trying to reconnect to Middleware ");
                        tryConnect();
                }
            }
        }, 0, 3 * 1000);
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
