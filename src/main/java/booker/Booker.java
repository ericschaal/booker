package booker;

import client.Client;
import common.Logger;
import common.NetworkAddress;
import common.Resource;
import middleware.Middleware;
import performance.ConstantLoad;
import performance.Increasing;
import performance.LoadEvolution;
import performance.PerformanceRunner;
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

    public static void printAvailablePerformanceAnalysisModes() {
        System.out.println("Select performance analysis mode:");
        System.out.println("1. Fixed load.");
        System.out.println("2. Increasing load.");
        System.out.print("> ");
    }

    public static void startClient(NetworkAddress registry) throws RemoteException, NotBoundException {
        new Client(registry).startConsole();
    }

    public static void startMiddleware(NetworkAddress registry) throws RemoteException, NotBoundException, AlreadyBoundException {
        new Middleware(registry);
    }

    public static void startRM(NetworkAddress registry, Resource resource) throws AlreadyBoundException, RemoteException {
        new EndpointRM(registry, resource);
    }

    public static void startPerformanceAnalysis(NetworkAddress registry, LoadEvolution loadEvolution) throws RemoteException, NotBoundException {
        new PerformanceRunner(registry, loadEvolution, 180, 0).start();
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

    public static LoadEvolution configurePerformanceAnalysis() {
        Scanner scanner = new Scanner(System.in);
        printAvailablePerformanceAnalysisModes();
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                System.out.print("Target Load: ");
                return new ConstantLoad(scanner.nextInt());
            case 2:
                System.out.print("Starting Load: ");
                int startingLoad = scanner.nextInt();
                System.out.print("Coefficient: ");
                return new Increasing(startingLoad, scanner.nextDouble());
            default:
                return new ConstantLoad(5);
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
                            startMiddleware(configure());
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
                        startMiddleware(registryAddress);
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
