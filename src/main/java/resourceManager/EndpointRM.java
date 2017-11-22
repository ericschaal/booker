package resourceManager;

import common.io.Logger;
import common.net.NetworkAddress;
import common.resource.RMI;
import common.resource.RemoteRevertibleResourceManager;
import common.resource.Resource;
import resourceManager.io.RMIOManager;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EndpointRM {

    public EndpointRM(NetworkAddress registryAddress, Resource resource) throws IOException, AlreadyBoundException {


        // Persistence Setup
        RMIOManager.init("./data/", resource);

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // RMI Setup
        Registry registry = LocateRegistry.getRegistry("localhost", registryAddress.getPort());

        ResourceManagerImpl obj = new ResourceManagerImpl();

        RemoteRevertibleResourceManager rm = (RemoteRevertibleResourceManager) UnicastRemoteObject.exportObject(obj, 0);

        registry.rebind(RMI.toRMIName(resource), rm);





        Logger.print().statement("Resource Manager ready.");


    }
}
