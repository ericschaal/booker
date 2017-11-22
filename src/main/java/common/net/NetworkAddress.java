package common.net;

import java.io.Serializable;

public class NetworkAddress implements Serializable {
    int port;
    String ip;

    public NetworkAddress(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
