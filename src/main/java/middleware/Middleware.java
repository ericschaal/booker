package middleware;


import common.io.Logger;
import middleware.config.MiddlewareConfig;
import middleware.io.MiddlewareIOManager;
import middleware.rmi.RMIManager;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class Middleware {


    public Middleware(MiddlewareConfig config) throws RemoteException, AlreadyBoundException, NotBoundException {

        RMIManager.init(config);

        MiddlewareIOManager ioManager = new MiddlewareIOManager("./data/");


        if (RMIManager.carRm() == null || RMIManager.flightRm() == null || RMIManager.customerRm() == null || RMIManager.roomRm() == null) {
            Logger.print().error("Failed to connect to RMs");
            System.exit(1);
        }

        Logger.print().statement("Middleware Ready.");

    }



}
