package com.badgames.box4dead.chat;


import java.io.*;
import java.util.Scanner;
import java.net.*;

public class ChatClient {
    private Socket client;
    private Scanner scanner;

    public ChatClient() throws IOException {

        client = new Socket("localhost", 9999);

        scanner = new Scanner(System.in);
        System.out.println("What is your name?");

        final String name = scanner.nextLine();
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

                        // print in console as "<name>: <message>"
                        System.out.println(name + ": " + message);
//                        Gdx.app.log(name, message);
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
                        String message = scanner.nextLine();
                        new DataOutputStream(client.getOutputStream()).writeUTF(message);
                    } catch (IOException e) {
                        e.printStackTrace();
//                        Gdx.app.log(name, "Error while sending message", e);
                    }
                }
            }
        }).start();
    }
}
