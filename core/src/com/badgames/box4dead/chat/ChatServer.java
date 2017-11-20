package com.badgames.box4dead.chat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private ServerSocket server;

    private Array<Client> clients;


    public ChatServer() throws IOException {
        try {
            server = new ServerSocket(9999);
            clients = new Array<Client>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try {
                            // accept new clients
                            // this function is blocking so it will only continue down if it
                            // actually accepts a new client
                            final Socket socket = server.accept();
                            String name = new DataInputStream(socket.getInputStream()).readUTF();
//                            String name = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
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
                                            // gets message from client
                                            String message = new DataInputStream(client.getSocket().getInputStream()).readUTF();
                                            for (Client c : clients) {
                                                // send to all clients
                                                DataOutputStream out = new DataOutputStream(c.getSocket().getOutputStream());
                                                out.writeUTF(client.getName() + "@@" + message);
                                            }
                                        } catch (IOException e) {
                                            // breaks out of loop just in case somebody in room exits
                                            break;
                                        }
                                    }
                                    // on disconnect, the thread will stop
                                    // and it should remove itself from the clients array
                                    try {
                                        client.getSocket().close();
                                    } catch(IOException e) {
                                        Gdx.app.log("GameServer", "Error in closing socket");
                                    }
                                    clients.removeValue(client, false);
                                }
                            }).start();
                        } catch(IOException e) {
                            Gdx.app.log("GameServer", "Failed to receive name of client");
                        }
                    }
                }
            }).start();


        } catch (GdxRuntimeException e) {
            System.out.println("Cannot connect to server with port 9999");
        }
    }

}
