package com.badgames.box4dead;

import java.net.InetAddress;

public class NetPlayer {
    private InetAddress address;
    private int port;
    private String name;

    public NetPlayer(String name, InetAddress address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        String retVal = "";
        retVal += "PLAYER ";
        retVal += name;
        return retVal;
    }

}
