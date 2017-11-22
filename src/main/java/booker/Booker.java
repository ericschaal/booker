package booker;

import client.Client;
import common.io.Logger;
import common.net.NetworkAddress;
import common.resource.Resource;
import middleware.Middleware;
import middleware.MiddlewareConfig;
import performance.*;
import resourceManager.EndpointRM;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Booker {


    public static boolean noNetworkAddressArg = false;

    public static void printMenu() {
        System.out.println("---- BOOKER ----");
        System.out.println("Select process to start: ");
        System.out.println("1. Client");
        System.out.println("2. Middleware");
        System.out.println("3. RM");
        System.out.println("4. Performance");
        System.out.println("5. Exit");
        System.out.println();
        System.out.print("> ");
    }

    public static void printAvailableResource() {
        System.out.println("Select resource for RM:");
        System.out.println("1. Customer");
        System.out.println("2. Room");
        System.out.println("3. Flight");
        System.out.println("4. Car");
        System.out.println("5. Cancel");
        System.out.println();
        System.out.print("> ");
    }

    public static void printAvailablePerformanceAnalysisLoadModes() {
        System.out.println("Select performance analysis load mode:");
        System.out.println("1. Fixed load.");
        System.out.println("2. Increasing load.");
        System.out.print("> ");
    }

    public static void printAvailablePerformanceAnalysisMode() {
        System.out.println("Select performance analysis mode:");
        System.out.println("1. One RM.");
        System.out.println("2. Multiple RM");
        System.out.println("3. Random");
        System.out.print("> ");
    }

    public static void startClient(NetworkAddress registry) throws RemoteException, NotBoundException {
        new Client(registry).startConsole();
    }

    public static void startMiddleware(MiddlewareConfig config) throws RemoteException, NotBoundException, AlreadyBoundException {
        new Middleware(config);
    }

    public static void startRM(NetworkAddress registry, Resource resource) throws AlreadyBoundException, RemoteException {
        new EndpointRM(registry, resource);
    }

    public static void startPerformanceAnalysis(NetworkAddress registry, PerformanceConfiguration config) throws RemoteException, NotBoundException {
        new PerformanceRunner(registry, config.getLoadEvolution(), 600, 0, config.isSingleRM(), config.isRandom()).start();
    }

    public static NetworkAddress configure() {
        Scanner scanner = new Scanner(System.in);
        String ip = "";
        int port = 1099;
        System.out.print("Registry address[127.0.01]: ");
        try {
            ip = scanner.nextLine();
            if (ip.isEmpty())
                ip = "127.0.0.1";
        } catch (Exception e) {
        }
        System.out.print("Registry port[1099]: ");
        try {
            port = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
        }
        return new NetworkAddress(ip, port);
    }

    public static PerformanceConfiguration configurePerformanceAnalysis() {
        Scanner scanner = new Scanner(System.in);
        printAvailablePerformanceAnalysisLoadModes();
        int choice = scanner.nextInt();
        LoadEvolution le;
        switch (choice) {
            case 1:
                System.out.print("Target Load: ");
                le = new ConstantLoad(scanner.nextInt());
                break;
            case 2:
                System.out.print("Starting Load: ");
                int startingLoad = scanner.nextInt();
                System.out.print("Coefficient: ");
                le =  new Increasing(startingLoad, scanner.nextDouble());
                break;
            default:
                le =  new ConstantLoad(5);
                break;
        }
        printAvailablePerformanceAnalysisMode();
        choice = scanner.nextInt();
        switch (choice) {
            case 1:
                return new PerformanceConfiguration(le, true, false);
            case 2:
                return new PerformanceConfiguration(le, false, false);
            case 3:
                return new PerformanceConfiguration(le, false, true);
            default:
                return new PerformanceConfiguration(le, false, true);
        }

    }


    public static Resource configureResource() {
        Scanner scanner = new Scanner(System.in);
        printAvailableResource();
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                return Resource.CUSTOMER;
            case 2:
                return Resource.ROOM;
            case 3:
                return Resource.FLIGHT;
            case 4:
                return Resource.CAR;
            default:
                return null;

        }

    }

    public static NetworkAddress parseNetworkAddress(String arg) {
        String[] split = arg.split(":");
        if (split.length != 2) {
            noNetworkAddressArg = true;
            return new NetworkAddress("127.0.0.1", 1099);
        }
        String host = split[0];
        try {
            int port = Integer.parseInt(split[1]);
            return new NetworkAddress(host, port);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegal input");
        }
    }

    public static MiddlewareConfig parseMiddlewareConfig(String[] args) {
        if (args.length != 6) throw new IllegalArgumentException("Invalid argument count");
        else {
            NetworkAddress bind = parseNetworkAddress(args[0]);
            NetworkAddress car = parseNetworkAddress(args[2]);
            NetworkAddress customer = parseNetworkAddress(args[3]);
            NetworkAddress flight = parseNetworkAddress(args[4]);
            NetworkAddress room = parseNetworkAddress(args[5]);
            return new MiddlewareConfig(car, customer, flight, room, bind);
        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Logger.init(3);
        boolean ready = false;

        try {

            if (args.length == 0) {
                while (!ready) {

                    printMenu();

                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            startClient(configure());
                            ready = true;
                            break;
                        case 2:
                            //startMiddleware(configureMiddleware());
                            System.out.println("Not implemented yet.");
                            ready = true;
                            break;
                        case 3:
                            Resource res = configureResource();
                            if (res != null)
                                startRM(configure(), res);
                            ready = true;
                            break;
                        case 4:
                            startPerformanceAnalysis(configure(), configurePerformanceAnalysis());
                            ready =true;
                            break;
                        case 5:
                            System.exit(1);
                        default:
                            break;
                    }
                }
            } else {

                NetworkAddress registryAddress = parseNetworkAddress(args[0]);
                switch (args[1]) {
                    case "c":
                        startClient(registryAddress);
                        break;
                    case "m":
                        startMiddleware(parseMiddlewareConfig(args));
                        break;
                    case "r":
                        if (args.length == 1) break;
                        switch (args[2]) {
                            case "customer":
                                startRM(registryAddress, Resource.CUSTOMER);
                                break;
                            case "car":
                                startRM(registryAddress, Resource.CAR);
                                break;
                            case "flight":
                                startRM(registryAddress, Resource.FLIGHT);
                                break;
                            case "room":
                                startRM(registryAddress, Resource.ROOM);
                                break;
                            default:
                                break;
                        }
                }
            }
        } catch (Exception e) {
            Logger.print().error(e.getMessage());
            e.printStackTrace();
            e.getCause();
            Logger.print().error("Failed to start.");
            System.exit(1);
        }
    }
}
