package com.badgames.box4dead;

import com.badgames.box4dead.sprites.Bullet;
import com.badgames.box4dead.sprites.Character;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Iterator;

public class GameServer extends ApplicationAdapter implements Constants {
    private ObjectMap characters, players, bullets;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private String data, action, payload;
    private String[] tokens;

    @Override
    public void create() {
        super.create();
        characters = new ObjectMap();
        players = new ObjectMap();
        bullets = new ObjectMap();

        try {
            socket = new DatagramSocket(PORT);
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException e){
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] buf = new byte[256];
                    packet = new DatagramPacket(buf, buf.length);

                    try {
                        socket.receive(packet);
                    } catch(IOException e) {}

                    data = new String(buf).trim();

                    if (data.equals("")) continue;

                    tokens = data.split(DELIMITER);
                    action = tokens[0];
                    payload = tokens[1];

                    // expected payload: id name
                    if (action.equals(CONNECT)) {
                        final String[] tokens = payload.split(" ");

                        // create a new net player
                        final NetPlayer player = new NetPlayer(packet.getAddress(), packet.getPort());
                        players.put(player.getID(), player);

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
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
                        });
                    }

                    // expected payload: x y hDirection vDirection
                    else if (action.equals(ADD_BULLET)) {
                        final String[] tokens = payload.split(" ");
                        final float x = Float.parseFloat(tokens[0]);
                        final float y = Float.parseFloat(tokens[1]);
                        final int facing = RIGHT;
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Bullet bullet = new Bullet(x, y, facing);
                                bullets.put(bullet.getId(), bullet);
                                broadcast(action(ADD_BULLET, payload(bullet.getId(), x, y, facing)));
                            }
                        });
                    }


                    else if (action.equals(MOVE_PLAYER)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Character character = (Character) characters.get(tokens[0]);
                                character.move(Integer.parseInt(tokens[1]));
                                broadcast(action(MOVE_PLAYER, payload(character.getId(), character.getX(), character.getY())));
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public void update() {
        for (Iterator ite = bullets.values(); ite.hasNext();) {
            Bullet bullet = (Bullet) ite.next();
            bullet.move();
            if (bullet.isOutOfWorld()) {
                bullets.remove(bullet.getId());
                broadcast(action(KILL_BULLET, payload(bullet.getId())));
            } else {
                broadcast(action(MOVE_BULLET, payload(bullet.getId(), bullet.getX(), bullet.getY())));
            }
        }
    }

    @Override
    public void render() {
        super.render();
        update();
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Iterator ite = characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            character.getTexture().dispose();
        }
        for (Iterator ite = bullets.values(); ite.hasNext();) {
            Bullet bullet = (Bullet) ite.next();
            bullet.getTexture().dispose();
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
        packet = new DatagramPacket(buf, buf.length, player.getAddress(), player.getPort());
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
