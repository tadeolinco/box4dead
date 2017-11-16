package com.badgames.box4dead;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class GameClient implements Runnable, Constants {
    String server, data, name;
    boolean connected;
    DatagramSocket socket;

    public GameClient(String server, String name) {
        this.server = server;
        this.name = name;
        new Thread(this).start();
    }


    public void send(String msg){
        try{
            byte[] buf = msg.getBytes();
            InetAddress address = InetAddress.getByName(server);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
            socket.send(packet);
        }catch(Exception e){}

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            data = new String(buf).trim();

            if (!connected && data.startsWith("CONNECTED")) {
                connected = true;
                System.out.println("CONNECTED");
            } else if (!connected) {
                System.out.println("CONNECTING");
                send("CONNECT " + name);
            } else if (connected) {
                // do logic here
            }

        }
    }


}
