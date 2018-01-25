package middleware.config;

import common.net.NetworkAddress;

public class MiddlewareConfig {


    NetworkAddress carRegistryAddress;
    NetworkAddress customerRegistryAddress;
    NetworkAddress flightRegistryAddress;
    NetworkAddress roomRegistryAddress;

    NetworkAddress bindAddress;


    public MiddlewareConfig() {
    }

    public MiddlewareConfig(NetworkAddress carRegistryAddress, NetworkAddress customerRegistryAddress, NetworkAddress flightRegistryAddress, NetworkAddress roomRegistryAddress, NetworkAddress bindAddress) {
        this.carRegistryAddress = carRegistryAddress;
        this.customerRegistryAddress = customerRegistryAddress;
        this.flightRegistryAddress = flightRegistryAddress;
        this.roomRegistryAddress = roomRegistryAddress;
        this.bindAddress = bindAddress;
    }

    public NetworkAddress getCarRegistryAddress() {
        return carRegistryAddress;
    }

    public void setCarRegistryAddress(NetworkAddress carRegistryAddress) {
        this.carRegistryAddress = carRegistryAddress;
    }

    public NetworkAddress getCustomerRegistryAddress() {
        return customerRegistryAddress;
    }

    public void setCustomerRegistryAddress(NetworkAddress customerRegistryAddress) {
        this.customerRegistryAddress = customerRegistryAddress;
    }

    public NetworkAddress getFlightRegistryAddress() {
        return flightRegistryAddress;
    }

    public void setFlightRegistryAddress(NetworkAddress flightRegistryAddress) {
        this.flightRegistryAddress = flightRegistryAddress;
    }

    public NetworkAddress getRoomRegistryAddress() {
        return roomRegistryAddress;
    }

    public void setRoomRegistryAddress(NetworkAddress roomRegistryAddress) {
        this.roomRegistryAddress = roomRegistryAddress;
    }

    public NetworkAddress getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(NetworkAddress bindAddress) {
        this.bindAddress = bindAddress;
    }
}
