package com.badgames.box4dead.chat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatServer {
    private ServerSocket server;

    private Array<Client> clients;


    public ChatServer() throws GdxRuntimeException {
        try {
            ServerSocketHints hints = new ServerSocketHints();
            hints.acceptTimeout = 0;
            server = Gdx.net.newServerSocket(Net.Protocol.TCP, 9999, hints);
            clients = new Array<Client>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try {
                            // accept new clients
                            // this function is blocking so it will only continue down if it
                            // actually accepts a new client
                            final Socket socket = server.accept(null);

                            String name = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
                            final Client client = new Client(socket, name);

                            // add it to our array so we can broadcast to them later
                            clients.add(client);

                            // make a new thread that listens for messages for that client only
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // only run if it's still connected
                                    while (client.getSocket().isConnected()) {
                                        try {
                                            String message = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream())).readLine();
                                            for (Client c : clients) {
                                                c.getSocket().getOutputStream().write((client.getName()+"@@"+message + "\n").getBytes());
                                            }
                                        } catch (IOException e) {
                                            Gdx.app.log("Server","Failed to receive message from " + client.getName());
                                        }
                                    }
                                    // on disconnect, the thread will stop
                                    // and it should remove itself from the clients array
                                    clients.removeValue(client, false);
                                }
                            }).start();
                        } catch(IOException e) {
                            Gdx.app.log("Server", "Failed to receive name of client");
                        }
                    }
                }
            }).start();


        } catch (GdxRuntimeException e) {
            System.out.println("Cannot connect to server with port 9999");
        }
    }

}
