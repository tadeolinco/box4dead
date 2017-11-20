package com.badgames.box4dead;

import com.badgames.box4dead.sprites.Character;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.UUID;

public class GameServer extends ApplicationAdapter implements Constants {
    private ObjectMap characters, players;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private String data, action, payload;
    private String[] tokens;

    @Override
    public void create() {
        super.create();
        characters = new ObjectMap();
        players = new ObjectMap();

        try {
            socket = new DatagramSocket(PORT);
            socket.setSoTimeout(1);
        } catch (SocketException e){
            e.printStackTrace();
        }
    }

    public void listen() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);

                try {
                    socket.receive(packet);
                } catch(IOException e) {}

                data = new String(buf).trim();

                if (data.equals("")) return;

                tokens = data.split(DELIMITER);
                action = tokens[0];
                payload = tokens[1];
                tokens = payload.split(" ");

                // expected payload: id name
                if (action.equals(CONNECT)) {
                    // create a new net player
                    NetPlayer player = new NetPlayer(packet.getAddress(), packet.getPort());
                    players.put(player.getID(), player);

                    // create a new character
                    Character character = new Character(tokens[1]);
                    character.setId(tokens[0]);
                    characters.put(character.getId(), character);

                    // Get all characters into one string
                    String allCharacters = "";
                    for (Iterator ite = characters.values(); ite.hasNext();) {
                        Character c = (Character) ite.next();
                        allCharacters += payload(c.getId(), c.getName());
                    }

                    // broadcast to every net player about that new character
                    broadcast(action(ADD_PLAYER, payload(character.getId(), character.getName())));

                    // send to only the new player
                    send(player, action(RECEIVE_ALL, allCharacters));
                }
                else {
                    // enumerate all catch cases here:
                    // MOVE_PLAYER
                    broadcast(data);
                }
            }
        });
    }



    @Override
    public void render() {
        super.render();

        listen();
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Iterator ite = characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            character.getTexture().dispose();
        }
    }

    public void broadcast(String msg){
        for(Iterator ite = players.values(); ite.hasNext();){
            NetPlayer player = (NetPlayer) ite.next();
            send(player, msg);
        }
    }

    public void send(NetPlayer player, String msg) {
        byte buf[] = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, player.getAddress(), player.getPort());
        try{
            socket.send(packet);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public String action(String action, String payload) {
        return action + DELIMITER + payload;
    }

    public String payload(Object ...args) {
        String value = "";
        for (Object arg : args) {
            value += arg.toString() + " ";
        }
        return value;
    }
}
