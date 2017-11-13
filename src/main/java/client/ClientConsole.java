package client;




import java.util.*;


public class ClientConsole
{
    public Vector parse(String command)
    {
        Vector arguments = new Vector();
        StringTokenizer tokenizer = new StringTokenizer(command,",");
        String argument ="";
        while (tokenizer.hasMoreTokens())
        {
            argument = tokenizer.nextToken();
            argument = argument.trim();
            arguments.add(argument);
        }
        return arguments;
    }
    public int findChoice(String argument)
    {
        if (argument.compareToIgnoreCase("help")==0)
            return 1;
        else if(argument.compareToIgnoreCase("newflight")==0)
            return 2;
        else if(argument.compareToIgnoreCase("newcar")==0)
            return 3;
        else if(argument.compareToIgnoreCase("newroom")==0)
            return 4;
        else if(argument.compareToIgnoreCase("newcustomer")==0)
            return 5;
        else if(argument.compareToIgnoreCase("deleteflight")==0)
            return 6;
        else if(argument.compareToIgnoreCase("deletecar")==0)
            return 7;
        else if(argument.compareToIgnoreCase("deleteroom")==0)
            return 8;
        else if(argument.compareToIgnoreCase("deletecustomer")==0)
            return 9;
        else if(argument.compareToIgnoreCase("queryflight")==0)
            return 10;
        else if(argument.compareToIgnoreCase("querycar")==0)
            return 11;
        else if(argument.compareToIgnoreCase("queryroom")==0)
            return 12;
        else if(argument.compareToIgnoreCase("querycustomer")==0)
            return 13;
        else if(argument.compareToIgnoreCase("queryflightprice")==0)
            return 14;
        else if(argument.compareToIgnoreCase("querycarprice")==0)
            return 15;
        else if(argument.compareToIgnoreCase("queryroomprice")==0)
            return 16;
        else if(argument.compareToIgnoreCase("reserveflight")==0)
            return 17;
        else if(argument.compareToIgnoreCase("reservecar")==0)
            return 18;
        else if(argument.compareToIgnoreCase("reserveroom")==0)
            return 19;
        else if(argument.compareToIgnoreCase("itinerary")==0)
            return 20;
        else if (argument.compareToIgnoreCase("quit")==0)
            return 21;
        else if (argument.compareToIgnoreCase("newcustomerid")==0)
            return 22;
        else if (argument.compareToIgnoreCase("start") == 0)
            return 23;
        else if (argument.compareToIgnoreCase("commit") == 0)
            return 24;
        else if (argument.compareToIgnoreCase("abort") == 0)
            return 25;
        else if (argument.compareToIgnoreCase("shutdown") == 0)
            return 26;
        else
            return 666;

    }

    public void listCommands()
    {
        System.out.println("\nWelcome to the Client interface provided to test your project.");
        System.out.println("Commands accepted by the interface are:");
        System.out.println("help");
        System.out.println("newflight\nnewcar\nnewroom\nnewcustomer\nnewcusomterid\ndeleteflight\ndeletecar\ndeleteroom");
        System.out.println("deletecustomer\nqueryflight\nquerycar\nqueryroom\nquerycustomer");
        System.out.println("queryflightprice\nquerycarprice\nqueryroomprice");
        System.out.println("reserveflight\nreservecar\nreserveroom\nitinerary");
        System.out.println("start\ncommit\nabort\nshutdown\n");
        System.out.println("nquit");
        System.out.println("\ntype help, <commandname> for detailed info(NOTE the use of comma).");
    }


    public void listSpecific(String command)
    {
        System.out.print("Help on: ");
        switch(findChoice(command))
        {
            case 1:
                System.out.println("Help");
                System.out.println("\nTyping help on the prompt gives a list of all the commands available.");
                System.out.println("Typing help, <commandname> gives details on how to use the particular command.");
                break;

            case 2:  //new flight
                System.out.println("Adding a new Flight.");
                System.out.println("Purpose:");
                System.out.println("\tAdd information about a new flight.");
                System.out.println("\nUsage:");
                System.out.println("\tnewflight,<id>,<flightnumber>,<flightSeats>,<flightprice>");
                break;

            case 3:  //new Car
                System.out.println("Adding a new Car.");
                System.out.println("Purpose:");
                System.out.println("\tAdd information about a new car location.");
                System.out.println("\nUsage:");
                System.out.println("\tnewcar,<id>,<location>,<numberofcars>,<pricepercar>");
                break;

            case 4:  //new Room
                System.out.println("Adding a new Room.");
                System.out.println("Purpose:");
                System.out.println("\tAdd information about a new room location.");
                System.out.println("\nUsage:");
                System.out.println("\tnewroom,<id>,<location>,<numberofrooms>,<priceperroom>");
                break;

            case 5:  //new Customer
                System.out.println("Adding a new Customer.");
                System.out.println("Purpose:");
                System.out.println("\tGet the system to provide a new customer id. (same as adding a new customer)");
                System.out.println("\nUsage:");
                System.out.println("\tnewcustomer,<id>");
                break;


            case 6: //delete Flight
                System.out.println("Deleting a flight");
                System.out.println("Purpose:");
                System.out.println("\tDelete a flight's information.");
                System.out.println("\nUsage:");
                System.out.println("\tdeleteflight,<id>,<flightnumber>");
                break;

            case 7: //delete Car
                System.out.println("Deleting a Car");
                System.out.println("Purpose:");
                System.out.println("\tDelete all cars from a location.");
                System.out.println("\nUsage:");
                System.out.println("\tdeletecar,<id>,<location>,<numCars>");
                break;

            case 8: //delete Room
                System.out.println("Deleting a Room");
                System.out.println("\nPurpose:");
                System.out.println("\tDelete all rooms from a location.");
                System.out.println("Usage:");
                System.out.println("\tdeleteroom,<id>,<location>,<numRooms>");
                break;

            case 9: //delete Customer
                System.out.println("Deleting a Customer");
                System.out.println("Purpose:");
                System.out.println("\tRemove a customer from the database.");
                System.out.println("\nUsage:");
                System.out.println("\tdeletecustomer,<id>,<customerid>");
                break;

            case 10: //querying a flight
                System.out.println("Querying flight.");
                System.out.println("Purpose:");
                System.out.println("\tObtain Seat information about a certain flight.");
                System.out.println("\nUsage:");
                System.out.println("\tqueryflight,<id>,<flightnumber>");
                break;

            case 11: //querying a Car Location
                System.out.println("Querying a Car location.");
                System.out.println("Purpose:");
                System.out.println("\tObtain number of cars at a certain car location.");
                System.out.println("\nUsage:");
                System.out.println("\tquerycar,<id>,<location>");
                break;

            case 12: //querying a Room location
                System.out.println("Querying a Room Location.");
                System.out.println("Purpose:");
                System.out.println("\tObtain number of rooms at a certain room location.");
                System.out.println("\nUsage:");
                System.out.println("\tqueryroom,<id>,<location>");
                break;

            case 13: //querying Customer Information
                System.out.println("Querying Customer Information.");
                System.out.println("Purpose:");
                System.out.println("\tObtain information about a customer.");
                System.out.println("\nUsage:");
                System.out.println("\tquerycustomer,<id>,<customerid>");
                break;

            case 14: //querying a flight for price
                System.out.println("Querying flight.");
                System.out.println("Purpose:");
                System.out.println("\tObtain price information about a certain flight.");
                System.out.println("\nUsage:");
                System.out.println("\tqueryflightprice,<id>,<flightnumber>");
                break;

            case 15: //querying a Car Location for price
                System.out.println("Querying a Car location.");
                System.out.println("Purpose:");
                System.out.println("\tObtain price information about a certain car location.");
                System.out.println("\nUsage:");
                System.out.println("\tquerycarprice,<id>,<location>");
                break;

            case 16: //querying a Room location for price
                System.out.println("Querying a Room Location.");
                System.out.println("Purpose:");
                System.out.println("\tObtain price information about a certain room location.");
                System.out.println("\nUsage:");
                System.out.println("\tqueryroomprice,<id>,<location>");
                break;

            case 17:  //reserve a flight
                System.out.println("Reserving a flight.");
                System.out.println("Purpose:");
                System.out.println("\tReserve a flight for a customer.");
                System.out.println("\nUsage:");
                System.out.println("\treserveflight,<id>,<customerid>,<flightnumber>");
                break;

            case 18:  //reserve a car
                System.out.println("Reserving a Car.");
                System.out.println("Purpose:");
                System.out.println("\tReserve a given number of cars for a customer at a particular location.");
                System.out.println("\nUsage:");
                System.out.println("\treservecar,<id>,<customerid>,<location>,<nummberofCars>");
                break;

            case 19:  //reserve a room
                System.out.println("Reserving a Room.");
                System.out.println("Purpose:");
                System.out.println("\tReserve a given number of rooms for a customer at a particular location.");
                System.out.println("\nUsage:");
                System.out.println("\treserveroom,<id>,<customerid>,<location>,<nummberofRooms>");
                break;

            case 20:  //reserve an Itinerary
                System.out.println("Reserving an Itinerary.");
                System.out.println("Purpose:");
                System.out.println("\tBook one or more flights.Also book zero or more cars/rooms at a location.");
                System.out.println("\nUsage:");
                System.out.println("\titinerary,<id>,<customerid>,<flightnumber1>....<flightnumberN>,<LocationToBookCarsOrRooms>,<NumberOfCars>,<NumberOfRoom>");
                break;


            case 21:  //quit the Client
                System.out.println("Quitting Client.");
                System.out.println("Purpose:");
                System.out.println("\tExit the Client application.");
                System.out.println("\nUsage:");
                System.out.println("\tquit");
                break;

            case 22:  //new customer with id
                System.out.println("Create new customer providing an id");
                System.out.println("Purpose:");
                System.out.println("\tCreates a new customer with the id provided");
                System.out.println("\nUsage:");
                System.out.println("\tnewcustomerid, <id>, <customerid>");
                break;
            case 23:
                System.out.println("Starts a new transaction");
                System.out.println("Purpose:");
                System.out.println("\tStarts a new atomic transaction. Must be committed or aborted");
                System.out.println("\nUsage:");
                System.out.println("\tstart");
                break;
            case 24:
                System.out.println("Commits a transaction");
                System.out.println("Purpose:");
                System.out.println("\tCommits an atomic transaction. Must be inside a transaction");
                System.out.println("\nUsage:");
                System.out.println("\tcommit, txId");
                break;
            case 25:
                System.out.println("Aborts a transaction");
                System.out.println("Purpose:");
                System.out.println("\tAborts an atomic transaction. Must be inside a transaction");
                System.out.println("\nUsage:");
                System.out.println("\tabort, txId");
                break;
            case 26:
                System.out.println("Shuts down whole system");
                System.out.println("Purpose:");
                System.out.println("\tClean system shutdown.");
                System.out.println("\nUsage:");
                System.out.println("\tshutdown");
                break;

            default:
                System.out.println(command);
                System.out.println("The interface does not support this command.");
                break;
        }
    }

    public void wrongNumber() {
        System.out.println("The number of arguments provided in this command are wrong.");
        System.out.println("Type help, <commandname> to check usage of this command.");
    }



    public int getInt(Object temp) throws Exception {
        try {
            return (new Integer((String)temp)).intValue();
        }
        catch(Exception e) {
            throw e;
        }
    }

    public boolean getBoolean(Object temp) throws Exception {
        try {
            return (new Boolean((String)temp)).booleanValue();
        }
        catch(Exception e) {
            throw e;
        }
    }

    public String getString(Object temp) throws Exception {
        try {
            return (String)temp;
        }
        catch (Exception e) {
            throw e;
        }
    }
}
