package com.badgames.box4dead.chat;


import com.badlogic.gdx.net.Socket;

public class Client {
    private Socket socket;
    private String name;

    public Client(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }


    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

}
