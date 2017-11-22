package common.resource;

import common.resource.Resource;

public class RMI {

    private static final String prefix = "ERIC_SCHAAL_BOOKER_19789489.";

    public static final String CAR = prefix + "CAR";
    public static final String CUSTOMER = prefix + "CUSTOMER";
    public static final String FLIGHT = prefix + "FLIGHT";
    public static final String ROOM = prefix + "ROOM";
    public static final String MIDDLEWARE = prefix + "MIDDLEWARE";


    public static String toRMIName(Resource resource) {
        switch (resource) {
            case CAR:
                return CAR;
            case CUSTOMER:
                return CUSTOMER;
            case FLIGHT:
                return FLIGHT;
            case ROOM:
                return ROOM;
            default:
                throw new IllegalArgumentException();
        }
    }

}
