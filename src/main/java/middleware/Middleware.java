package middleware;


import common.io.Logger;
import middleware.config.MiddlewareConfig;
import middleware.io.MiddlewareIOManager;
import middleware.rmi.RMIManager;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class Middleware {


    public Middleware(MiddlewareConfig config) throws AlreadyBoundException, NotBoundException, IOException {

        try {
            MiddlewareIOManager.init("./data");
            RMIManager.init(config);

        } catch (RemoteException e) {
            Logger.print().error("Failed to connect to RMs");
            System.exit(1);
        }

        Logger.print().statement("Middleware Ready.");

    }



}
