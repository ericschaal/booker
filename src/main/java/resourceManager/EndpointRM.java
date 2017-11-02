package resourceManager;

import common.*;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EndpointRM {

    public EndpointRM(NetworkAddress registryAddress, Resource resource)throws RemoteException, AlreadyBoundException {


        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Registry registry = LocateRegistry.getRegistry(registryAddress.getIp(), registryAddress.getPort());

        ResourceManagerImpl obj = new ResourceManagerImpl();

        RemoteRevertibleResourceManager rm = (RemoteRevertibleResourceManager) UnicastRemoteObject.exportObject(obj, 0);

        registry.rebind(RMI.toRMIName(resource), rm);

        Logger.print().statement("Resource Manager ready.");


    }
}
