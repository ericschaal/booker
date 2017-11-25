package resourceManager;

import common.io.Logger;
import common.net.NetworkAddress;
import common.resource.RMI;
import common.resource.EndPointResourceManager;
import common.resource.Resource;
import resourceManager.io.RMIOManager;
import resourceManager.storage.Database;
import resourceManager.tx.TxManager;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RM {

    public RM(NetworkAddress registryAddress, Resource resource) throws IOException, AlreadyBoundException {


        // Persistence Setup
        RMIOManager.init("./data/", resource);
        Database.init();
        TxManager.init();

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // RMI Setup
        Registry registry = LocateRegistry.getRegistry("localhost", registryAddress.getPort());

        ResourceManagerImpl obj = new ResourceManagerImpl();

        EndPointResourceManager rm = (EndPointResourceManager) UnicastRemoteObject.exportObject(obj, 0);

        registry.rebind(RMI.toRMIName(resource), rm);





        Logger.print().statement("Resource Manager ready.");


    }
}
