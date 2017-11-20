package com.badgames.box4dead;

import java.net.InetAddress;
import java.util.UUID;

public class NetPlayer {
    private InetAddress address;
    private String id;
    private int port;

    public NetPlayer(InetAddress address, int port) {
        this.id = UUID.randomUUID().toString();
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getID() {
        return id;
    }

    public int getPort() { return port; }

    // string format: "PLAYER <name>"
    public String toString() {
        String retVal = "";
        retVal += "PLAYER ";
        retVal += id;
        return retVal;
    }

}
