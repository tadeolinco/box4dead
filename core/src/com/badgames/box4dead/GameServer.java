package com.badgames.box4dead;

import com.badgames.box4dead.sprites.Bullet;
import com.badgames.box4dead.sprites.Character;
import com.badgames.box4dead.sprites.Zombie;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Iterator;

public class GameServer extends GameClient implements Constants {
    private ObjectMap players;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private String data, action, payload;
    private String[] tokens;
    private Assets assets;

    private float zombieTimer = 0f;

    public GameServer() {
        characters = new ObjectMap();
        players = new ObjectMap();
        bullets = new ObjectMap();
        zombies = new ObjectMap();
        assets = new Assets();

        try {
            socket = new DatagramSocket(PORT);
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException e){
            e.printStackTrace();
        }

    }


    @Override
    public void create() {
        assets.load();
        assets.getManager().finishLoading();

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
                                    allCharacters += payload(c.getId(), c.getName(), c.getX(), c.getY(), c.getColor().r, c.getColor().g, c.getColor().b);
                                }

                                // broadcast to every net player about that new character
                                broadcast(action(ADD_PLAYER, payload(character.getId(), character.getName(), character.getX(), character.getY(), character.getColor().r, character.getColor().g, character.getColor().b)));

                                // send to only the new player
                                send(player, action(RECEIVE_ALL, allCharacters));
                            }
                        });
                    }

                    // expected payload: character_id x y facing
                    else if (action.equals(ADD_BULLET)) {
                        final String[] tokens = payload.split(" ");
                        final Character character = (Character) characters.get(tokens[0]);
                        final float x = Float.parseFloat(tokens[1]);
                        final float y = Float.parseFloat(tokens[2]);
                        final int facing = Integer.parseInt(tokens[3]);
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Bullet bullet = new Bullet(tokens[0], x, y, facing, character.getColor());
                                bullets.put(bullet.getId(), bullet);
                                broadcast(action(ADD_BULLET, payload(bullet.getId(), tokens[0], x, y, facing)));
                            }
                        });
                    }

                    // expected payload: id x y facing
                    else if (action.equals(MOVE_PLAYER)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Character character = (Character) characters.get(tokens[0]);
                                character.move(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Integer.parseInt(tokens[3]));
                                broadcast(action(MOVE_PLAYER, payload(character.getId(), character.getX(), character.getY(), character.getFacing())));
                            }
                        });
                    }

                    // expected payload: id hp
                    else if (action.equals(MOVE_PLAYER)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Character character = (Character) characters.get(tokens[0]);
                                character.move(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Integer.parseInt(tokens[3]));
                                broadcast(action(MOVE_PLAYER, payload(character.getId(), character.getX(), character.getY(), character.getFacing())));
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
            String characterId = bullet.hitCharacter();
            if (!characterId.equals("")) {
                bullets.remove(bullet.getId());
                broadcast(action(KILL_BULLET, payload(bullet.getId())));
                Character character = (Character) characters.get(characterId);
                character.setHp(character.getHp() - bullet.getDamage());
                broadcast(action(CHANGE_HP_PLAYER, payload(characterId, character.getHp())));
                continue;
            }
            String zombieId = bullet.hitZombie();
            if (!zombieId.equals("")) {
                bullets.remove(bullet.getId());
                broadcast(action(KILL_BULLET, payload(bullet.getId())));
                Zombie zombie = (Zombie) zombies.get(zombieId);
                zombie.setHp(zombie.getHp() - bullet.getDamage());
                if (zombie.getHp() < 0) {
                    zombies.remove(zombie.getId());
                    broadcast(action(KILL_ZOMBIE, payload(zombie.getId())));
                } else {
                    broadcast(action(CHANGE_HP_ZOMBIE, payload(zombie.getId(), zombie.getHp())));
                    zombie.setStunDuration(0.5f);
                }
            }


            if (bullet.isOutOfWorld()) {
                bullets.remove(bullet.getId());
                broadcast(action(KILL_BULLET, payload(bullet.getId())));
            } else {
                broadcast(action(MOVE_BULLET, payload(bullet.getId(), bullet.getX(), bullet.getY())));
            }
        }

        for (Iterator ite = characters.values(); ite.hasNext(); ) {
            Character character = (Character) ite.next();
            if (character.handleRegen()) {
                float hp = 1;
                character.setHp(character.getHp() + hp);
                broadcast(action(CHANGE_HP_PLAYER, payload(character.getId(), character.getHp())));
            }
        }


        zombieTimer += Gdx.graphics.getDeltaTime();
        if (zombieTimer > 10) {
            Zombie zombie = new Zombie();
            zombies.put(zombie.getId(), zombie);
            broadcast(action(ADD_ZOMBIE, payload(zombie.getId(), zombie.getX(), zombie.getY())));
            zombieTimer = zombieTimer % 10;
        }
        for (Iterator ite = zombies.values(); ite.hasNext(); ) {
            Zombie zombie = (Zombie) ite.next();
            zombie.move();
            broadcast(action(MOVE_ZOMBIE, payload(zombie.getId(), zombie.getX(), zombie.getY())));
            String characterId = zombie.handleAttack();
            if (!characterId.equals("")) {;
                Character character = (Character) characters.get(characterId);
                character.setHp(character.getHp() - zombie.getDamage());
                broadcast(action(CHANGE_HP_PLAYER, payload(character.getId(), character.getHp())));
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
