package client;

import common.Logger;
import common.NetworkAddress;
import common.RMI;
import common.TransactionalResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Vector;

public class Client {

    private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    private ClientConsole obj;
    private ResourceManager rm;

    private String command = "";
    private Vector arguments  = new Vector();
    private int Id, Cid;
    private int flightNum;
    private int flightPrice;
    private int flightSeats;
    private boolean Room;
    private boolean Car;
    private int price;
    private int numRooms;
    private int numCars;
    String location;

    public Client(NetworkAddress registryAddress) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(registryAddress.getIp(), registryAddress.getPort());
        TransactionalResourceManager resourceManager = (TransactionalResourceManager) registry.lookup(RMI.MIDDLEWARE);

        if (resourceManager != null) {
            this.rm = new ResourceManager(resourceManager);
            this.obj = new ClientConsole();

            Logger.print().statement("Client ready");
        } else {
            Logger.print().error("Client failed to connect to Middleware.");
        }

    }

    public void startConsole() {
        System.out.println("\n\n\tClient Interface");
        System.out.println("Type \"help\" for list of supported commands");
        while(true){
            System.out.print("\n>");
            try{
                //read the next command
                command =stdin.readLine();
            }
            catch (IOException io){
                System.out.println("Unable to read from standard in");
                System.exit(1);
            }
            //remove heading and trailing white space
            command=command.trim();
            arguments=obj.parse(command);

            //decide which of the commands this was
            switch(obj.findChoice((String)arguments.elementAt(0))){
                case 1: //help section
                    if(arguments.size()==1)   //command was "help"
                        obj.listCommands();
                    else if (arguments.size()==2)  //command was "help <commandname>"
                        obj.listSpecific((String)arguments.elementAt(1));
                    else  //wrong use of help command
                        System.out.println("Improper use of help command. Type help or help, <commandname>");
                    break;

                case 2:  //new flight
                    if(arguments.size()!=5){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Adding a new Flight using id: "+arguments.elementAt(1));
                    System.out.println("Flight number: "+arguments.elementAt(2));
                    System.out.println("Add Flight Seats: "+arguments.elementAt(3));
                    System.out.println("Set Flight Price: "+arguments.elementAt(4));

                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        flightNum = obj.getInt(arguments.elementAt(2));
                        flightSeats = obj.getInt(arguments.elementAt(3));
                        flightPrice = obj.getInt(arguments.elementAt(4));
                        if(rm.addFlight(Id,flightNum,flightSeats,flightPrice))
                            System.out.println("Flight added");
                        else
                            System.out.println("Flight could not be added");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 3:  //new Car
                    if(arguments.size()!=5){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Adding a new Car using id: "+arguments.elementAt(1));
                    System.out.println("Car Location: "+arguments.elementAt(2));
                    System.out.println("Add Number of Cars: "+arguments.elementAt(3));
                    System.out.println("Set Price: "+arguments.elementAt(4));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        location = obj.getString(arguments.elementAt(2));
                        numCars = obj.getInt(arguments.elementAt(3));
                        price = obj.getInt(arguments.elementAt(4));
                        if(rm.addCars(Id,location,numCars,price))
                            System.out.println("Cars added");
                        else
                            System.out.println("Cars could not be added");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 4:  //new Room
                    if(arguments.size()!=5){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Adding a new Room using id: "+arguments.elementAt(1));
                    System.out.println("Room Location: "+arguments.elementAt(2));
                    System.out.println("Add Number of Rooms: "+arguments.elementAt(3));
                    System.out.println("Set Price: "+arguments.elementAt(4));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        location = obj.getString(arguments.elementAt(2));
                        numRooms = obj.getInt(arguments.elementAt(3));
                        price = obj.getInt(arguments.elementAt(4));
                        if(rm.addRooms(Id,location,numRooms,price))
                            System.out.println("Rooms added");
                        else
                            System.out.println("Rooms could not be added");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 5:  //new Customer
                    if(arguments.size()!=2){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Adding a new Customer using id:"+arguments.elementAt(1));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        int customer=rm.newCustomer(Id);
                        System.out.println("new customer id:"+customer);
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 6: //delete Flight
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Deleting a flight using id: "+arguments.elementAt(1));
                    System.out.println("Flight Number: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        flightNum = obj.getInt(arguments.elementAt(2));
                        if(rm.deleteFlight(Id,flightNum))
                            System.out.println("Flight Deleted");
                        else
                            System.out.println("Flight could not be deleted");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 7: //delete Car
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Deleting the cars from a particular location  using id: "+arguments.elementAt(1));
                    System.out.println("Car Location: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        location = obj.getString(arguments.elementAt(2));

                        if(rm.deleteCars(Id,location))
                            System.out.println("Cars Deleted");
                        else
                            System.out.println("Cars could not be deleted");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 8: //delete Room
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Deleting all rooms from a particular location  using id: "+arguments.elementAt(1));
                    System.out.println("Room Location: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        location = obj.getString(arguments.elementAt(2));
                        if(rm.deleteRooms(Id,location))
                            System.out.println("Rooms Deleted");
                        else
                            System.out.println("Rooms could not be deleted");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 9: //delete Customer
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Deleting a customer from the database using id: "+arguments.elementAt(1));
                    System.out.println("Customer id: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        int customer = obj.getInt(arguments.elementAt(2));
                        if(rm.deleteCustomer(Id,customer))
                            System.out.println("Customer Deleted");
                        else
                            System.out.println("Customer could not be deleted");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 10: //querying a flight
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Querying a flight using id: "+arguments.elementAt(1));
                    System.out.println("Flight number: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        flightNum = obj.getInt(arguments.elementAt(2));
                        int seats=rm.queryFlight(Id,flightNum);
                        System.out.println("Number of seats available:"+seats);
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 11: //querying a Car Location
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Querying a car location using id: "+arguments.elementAt(1));
                    System.out.println("Car location: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        location = obj.getString(arguments.elementAt(2));
                        numCars=rm.queryCars(Id,location);
                        System.out.println("number of Cars at this location:"+numCars);
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 12: //querying a Room location
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Querying a room location using id: "+arguments.elementAt(1));
                    System.out.println("Room location: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        location = obj.getString(arguments.elementAt(2));
                        numRooms=rm.queryRooms(Id,location);
                        System.out.println("number of Rooms at this location:"+numRooms);
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 13: //querying Customer Information
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Querying Customer information using id: "+arguments.elementAt(1));
                    System.out.println("Customer id: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        int customer = obj.getInt(arguments.elementAt(2));
                        String bill=rm.queryCustomerInfo(Id,customer);
                        System.out.println("Customer info:"+bill);
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 14: //querying a flight Price
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Querying a flight Price using id: "+arguments.elementAt(1));
                    System.out.println("Flight number: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        flightNum = obj.getInt(arguments.elementAt(2));
                        price=rm.queryFlightPrice(Id,flightNum);
                        System.out.println("Price of a seat:"+price);
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 15: //querying a Car Price
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Querying a car price using id: "+arguments.elementAt(1));
                    System.out.println("Car location: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        location = obj.getString(arguments.elementAt(2));
                        price=rm.queryCarsPrice(Id,location);
                        System.out.println("Price of a car at this location:"+price);
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 16: //querying a Room price
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Querying a room price using id: "+arguments.elementAt(1));
                    System.out.println("Room Location: "+arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        location = obj.getString(arguments.elementAt(2));
                        price=rm.queryRoomsPrice(Id,location);
                        System.out.println("Price of Rooms at this location:"+price);
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 17:  //reserve a flight
                    if(arguments.size()!=4){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Reserving a seat on a flight using id: "+arguments.elementAt(1));
                    System.out.println("Customer id: "+arguments.elementAt(2));
                    System.out.println("Flight number: "+arguments.elementAt(3));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        int customer = obj.getInt(arguments.elementAt(2));
                        flightNum = obj.getInt(arguments.elementAt(3));
                        if(rm.reserveFlight(Id,customer,flightNum))
                            System.out.println("Flight Reserved");
                        else
                            System.out.println("Flight could not be reserved.");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 18:  //reserve a car
                    if(arguments.size()!=4){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Reserving a car at a location using id: "+arguments.elementAt(1));
                    System.out.println("Customer id: "+arguments.elementAt(2));
                    System.out.println("Location: "+arguments.elementAt(3));

                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        int customer = obj.getInt(arguments.elementAt(2));
                        location = obj.getString(arguments.elementAt(3));

                        if(rm.reserveCar(Id,customer,location))
                            System.out.println("Car Reserved");
                        else
                            System.out.println("Car could not be reserved.");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 19:  //reserve a room
                    if(arguments.size()!=4){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Reserving a room at a location using id: "+arguments.elementAt(1));
                    System.out.println("Customer id: "+arguments.elementAt(2));
                    System.out.println("Location: "+arguments.elementAt(3));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        int customer = obj.getInt(arguments.elementAt(2));
                        location = obj.getString(arguments.elementAt(3));

                        if(rm.reserveRoom(Id,customer,location))
                            System.out.println("Room Reserved");
                        else
                            System.out.println("Room could not be reserved.");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 20:  //reserve an Itinerary
                    if(arguments.size()<7){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Reserving an Itinerary using id:"+arguments.elementAt(1));
                    System.out.println("Customer id:"+arguments.elementAt(2));
                    for(int i=0;i<arguments.size()-6;i++)
                        System.out.println("Flight number"+arguments.elementAt(3+i));
                    System.out.println("Location for Car/Room booking:"+arguments.elementAt(arguments.size()-3));
                    System.out.println("Car to book?:"+arguments.elementAt(arguments.size()-2));
                    System.out.println("Room to book?:"+arguments.elementAt(arguments.size()-1));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        int customer = obj.getInt(arguments.elementAt(2));
                        Vector flightNumbers = new Vector();
                        for(int i=0;i<arguments.size()-6;i++)
                            flightNumbers.addElement(arguments.elementAt(3+i));
                        location = obj.getString(arguments.elementAt(arguments.size()-3));
                        Car = obj.getBoolean(arguments.elementAt(arguments.size()-2));
                        Room = obj.getBoolean(arguments.elementAt(arguments.size()-1));

                        if(rm.itinerary(Id,customer,flightNumbers,location,Car,Room))
                            System.out.println("Itinerary Reserved");
                        else
                            System.out.println("Itinerary could not be reserved.");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 21:  //quit the Client
                    if(arguments.size()!=1){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Quitting Client.");
                    System.exit(1);


                case 22:  //new Customer given id
                    if(arguments.size()!=3){
                        obj.wrongNumber();
                        break;
                    }
                    System.out.println("Adding a new Customer using id:"+arguments.elementAt(1) + " and cid " +arguments.elementAt(2));
                    try{
                        Id = obj.getInt(arguments.elementAt(1));
                        Cid = obj.getInt(arguments.elementAt(2));
                        boolean customer=rm.newCustomer(Id,Cid);
                        System.out.println("new customer id:"+Cid);
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION:");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                default:
                    System.out.println("The interface does not support this command.");
                    break;
            }//end of switch
        }//end of while(true)
    }

}
