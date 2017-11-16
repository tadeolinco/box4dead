package com.badgames.box4dead;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Iterator;

public class GameServer implements Runnable, Constants{
    int numOfPlayers;
    String data;
    DatagramSocket serverSocket;
    int gameStage = WAITING_FOR_PLAYERS;
    GameState game;

    public GameServer(int numOfPlayers) {
        System.out.println("Server: Creating game");
        this.numOfPlayers = numOfPlayers;
        try {
            serverSocket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            System.out.println(e);
        }

        game = new GameState();
        new Thread(this).start();
    }

    public void broadcast(String msg){
        for(Iterator ite = game.getPlayers().keySet().iterator(); ite.hasNext();){
            String name=(String)ite.next();
            NetPlayer player=(NetPlayer)game.getPlayers().get(name);
            send(player,msg);
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
            // Get the data from players
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try{
                serverSocket.receive(packet);
            }catch(IOException e) {
                e.printStackTrace();
            }

            data = new String(buf).trim();


            switch (gameStage) {
                // implement broadcasting here
            }
        }
    }

}
