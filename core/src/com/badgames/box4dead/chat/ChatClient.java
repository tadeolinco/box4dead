package com.badgames.box4dead.chat;


import com.badgames.box4dead.Constants;
import com.badlogic.gdx.utils.Array;

import java.io.*;
import java.util.Scanner;
import java.net.*;

public class ChatClient implements Constants {
    private Socket client;
    private String message;
    private Array<String> messages;

    public ChatClient(String server, String name) throws IOException {
        message = "";
        messages = new Array<String>();
        client = new Socket(server, PORT - 1);

        new DataOutputStream(client.getOutputStream()).writeUTF(name);

        // create a thread that will continuously listen for incoming message from the socket server
        new Thread(new Runnable() {
            @Override
            public void run() {
                // only run if the socket is still connected
                while (client.isConnected()) {
                    try {

                        // data will have the form of "<name>@@<message>"
                        String data = new DataInputStream(client.getInputStream()).readUTF();
                        String[] parts = data.split("@@");
                        String name = parts[0];
                        String message = parts[1];

                        messages.insert(0, name + ": " + message);
                    } catch (IOException e) {
                        e.printStackTrace();
//                        Gdx.app.log(name, "Error while receiving message", e);
                    }
                }
            }
        }).start();

        // create a thread that will continuously get input from the terminal
        // and send it to the server socket
        new Thread(new Runnable() {
            @Override
            public void run() {
                // only run if the socket is still connected
                while (client.isConnected()) {
                    try {
                        try{Thread.sleep(10);}catch(InterruptedException e){System.out.println(e);}
                        if (!message.equals("")) {
                            new DataOutputStream(client.getOutputStream()).writeUTF(message);
                            message = "";
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
//                        Gdx.app.log(name, "Error while sending message", e);
                    }
                }
            }
        }).start();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Array<String> getMessages() {
        return messages;
    }

    public void setMessages(Array<String> messages) {
        this.messages = messages;
    }
}
