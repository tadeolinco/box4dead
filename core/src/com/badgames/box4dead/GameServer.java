package com.badgames.box4dead;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameServer implements Runnable, Constants {
    DatagramSocket serverSocket;
    String data;

    private Map players;
    int playerCount, numOfPlayers,gameStage = WAITING_FOR_PLAYERS;

    public GameServer(int numOfPlayers) {
        this.playerCount = 0;
        System.out.println("Server: Creating game");
        this.numOfPlayers = numOfPlayers;
        try {
            serverSocket = new DatagramSocket(PORT);
            // set the timeout for the socket so it wont block
            serverSocket.setSoTimeout(100);

        } catch (SocketException e) {
            System.out.println(e);
        }

        players = new HashMap<String, NetPlayer>();
        new Thread(this).start();
    }

    // copied from CircleWards
    public void broadcast(String msg){
        for(Iterator ite = players.keySet().iterator(); ite.hasNext();){
            String name=(String)ite.next();
            NetPlayer player=(NetPlayer)players.get(name);
            send(player, msg);
        }
    }

    public void send(NetPlayer player, String msg){
        DatagramPacket packet;
        byte buf[] = msg.getBytes();
        packet = new DatagramPacket(buf, buf.length, player.getAddress(),player.getPort());
        try{
            serverSocket.send(packet);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            // Get the data from a client
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try{
                serverSocket.receive(packet);
            }catch(IOException e) {
            }

            // trim removes excess whitespace
            data = new String(buf).trim();

            switch (gameStage) {
                case WAITING_FOR_PLAYERS:
                    // tokens format: "CONNECT <player_name>"
                    if (data.startsWith("CONNECT")) {
                        String tokens[] = data.split(" ");
                        NetPlayer player = new NetPlayer(tokens[1], packet.getAddress(), packet.getPort());
                        System.out.println("Player connected: " + tokens[1]);
                        players.put(tokens[1].trim(),player);
                        broadcast("CONNECTED " + tokens[1]);
                        playerCount++;
                        if (playerCount == numOfPlayers){
                            gameStage = GAME_START;
                        }
                    }
                    break;
            }
        }
    }

}
