package com.badgames.box4dead;

import com.badgames.box4dead.sprites.Character;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Iterator;
import java.util.UUID;


public class Box4Dead extends ApplicationAdapter implements Constants {
	SpriteBatch batch;
	String server, name, data, action, payload, id;
	boolean connected;
	DatagramSocket socket;
	DatagramPacket packet;
	String[] tokens;

    private ObjectMap characters;


	public Box4Dead(String server, String name) {
		this.server = server;
		this.name = name;
		this.id = UUID.randomUUID().toString();
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(1);
        } catch (SocketException e){
            e.printStackTrace();
        }

        characters = new ObjectMap();
	}


	@Override
	public void create () {
		batch = new SpriteBatch();

//		try {
//		    new ChatClient();
//        } catch(IOException e) {
//			try {
//				Gdx.app.log("GameServer", "You are the server");
//				new ChatServer();
//				new ChatClient();
//			}catch (IOException ex){}
//        }
	}

	public void listen() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);

                try {
                    socket.receive(packet);
                } catch (IOException e) {}

                data = new String(buf).trim();


                if (!connected) {
                    System.out.println("CONNECTING");
                    send(action(CONNECT, payload(id, name)));
                    connected = true;
                }

                if (data.equals("")) return;

                System.out.println("Received: " + data);
                tokens = data.split(DELIMITER);
                action = tokens[0];
                payload = tokens[1];
                tokens = payload.split(" ");

                // expected payload (id name )++
                if (action.equals(RECEIVE_ALL)) {
                    for (int i = 0; i < tokens.length / 2; ++i) {
                        Character character = new Character(tokens[2 * i + 1]);
                        character.setId(tokens[2 * i + 0]);
                        characters.put(character.getID(), character);
                    }
                }

                // expected payload: id name
                if (action.equals(ADD_PLAYER)) {
                    Character character = new Character(tokens[1]);
                    character.setId(tokens[0]);
                    characters.put(character.getID(), character);
                }

                // expected payload: id x y
                if (action.equals(MOVE_PLAYER)) {
                    Character character = (Character) characters.get(tokens[0]);
                    character.setX(Float.parseFloat(tokens[1]));
                    character.setY(Float.parseFloat(tokens[2]));
                }


            }
        });
    }

    public void update() {
        for (Iterator ite = characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            if (id.equals(character.getID())) {
                boolean touched = character.handleInput(Gdx.graphics.getDeltaTime());
                if (touched) {
                    send(action(MOVE_PLAYER, payload(character.getID(), character.getX(), character.getY())));
                }
            }
        }
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		listen();
        update();

        batch.begin();
        for (Iterator ite = characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            batch.draw(character.getTexture(), character.getX(), character.getY());
        }
        batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
        for (Iterator ite = characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            character.getTexture().dispose();
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

    public void send(String msg) {
        try{
            byte[] buf = msg.getBytes();
            InetAddress address = InetAddress.getByName(server);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
            socket.send(packet);
        }catch(IOException e){
            e.printStackTrace();
        }

    }



}
