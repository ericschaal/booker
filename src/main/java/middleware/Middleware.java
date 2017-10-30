package middleware;

import common.RemoteResourceManager;



public class Middleware {

    MiddlewareResourceManager rm;

    public Middleware(RemoteResourceManager flightRm, RemoteResourceManager customerRm, RemoteResourceManager roomRm, RemoteResourceManager carRm) {
        this.rm = new MiddlewareResourceManager(carRm, flightRm, customerRm, roomRm);
    }



    public static void main(String[] args) {

    }

}
