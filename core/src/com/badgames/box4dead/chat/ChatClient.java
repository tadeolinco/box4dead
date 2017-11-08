package com.badgames.box4dead.chat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ChatClient {
    private Socket client;
    private Scanner scanner;

    public ChatClient() {
        SocketHints hints = new SocketHints();
        client = Gdx.net.newClientSocket(Net.Protocol.TCP, "localhost", 9999, hints);

        scanner = new Scanner(System.in);
        Gdx.app.log("Client", "What is your name?");
        final String name = scanner.nextLine();
        try {
            client.getOutputStream().write((name + "\n").getBytes());
        } catch(IOException e) {
            Gdx.app.log(name, "Error in sending name");
        }

        // create a thread that will continuously listen for incoming message from the socket server
        new Thread(new Runnable() {
            @Override
            public void run() {
                // only run if the socket is still connected
                while (client.isConnected()) {
                    try {

                        // data will have the form of "<name>@@<message>"
                        String data = new BufferedReader(new InputStreamReader(client.getInputStream())).readLine();
                        String[] parts = data.split("@@");
                        String name = parts[0];
                        String message = parts[1];

                        // print in console as "<name>: <message>"
                        Gdx.app.log(name, message);
                    } catch (IOException e) {
                        Gdx.app.log(name, "Error while receiving message", e);
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
                        String message = scanner.nextLine() + "\n";
                        client.getOutputStream().write(message.getBytes());
                    } catch (IOException e) {
                        Gdx.app.log(name, "Error while sending message", e);
                    }
                }
            }
        }).start();
    }
}
