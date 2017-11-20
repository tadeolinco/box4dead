package com.badgames.box4dead;

import com.badgames.box4dead.chat.ChatClient;
import com.badgames.box4dead.chat.ChatServer;
import com.badgames.box4dead.sprites.Character;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


public class Box4Dead extends ApplicationAdapter implements Constants {
	SpriteBatch batch;
	String server, data, type;
	boolean connected;
	DatagramSocket socket;


    private ObjectMap players;
    private ObjectMap characters;


	public Box4Dead(String type, String server) {
		this.server = server;
        this.type = type;
        try {
            if (type.equals("server")) {
                socket = new DatagramSocket(PORT);
            } else {
                socket = new DatagramSocket();
            }
            socket.setSoTimeout(10);
        } catch (SocketException e){
            e.printStackTrace();
        }

        characters = new ObjectMap();
        players = new ObjectMap();
	}

    // copied from CircleWars
    public void broadcast(String msg){
        for(Iterator ite = players.keys(); ite.hasNext();){
            String id = (String)ite.next();
            NetPlayer player = (NetPlayer)players.get(id);
            System.out.println(player.getAddress());
            send(player, msg);
        }
    }

    public void send(NetPlayer player, String msg){
        byte buf[] = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, player.getAddress(), player.getPort());
        try{
            socket.send(packet);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public void send(String msg){
		try{
			byte[] buf = msg.getBytes();
			InetAddress address = InetAddress.getByName(server);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
			socket.send(packet);
		}catch(IOException e){
            e.printStackTrace();
        }

	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		if (type.equals("server")) {
            Character character = new Character(UUID.randomUUID().toString(), "Sam");
            characters.put(character.getID(), character);
        }

//		try {
//		    new ChatClient();
//        } catch(IOException e) {
//			try {
//				Gdx.app.log("Server", "You are the server");
//				new ChatServer();
//				new ChatClient();
//			}catch (IOException ex){}
//        }
	}

	public void update() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {

                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                try {
                    socket.receive(packet);
                } catch (IOException e) {}

                data = new String(buf).trim();
                System.out.println(data);
                // server
                if (data.startsWith("CONNECT")) {
                    NetPlayer player = new NetPlayer(packet.getAddress(), packet.getPort());
                    players.put(player.getID(), player);
                    String id = UUID.randomUUID().toString();
                    Character character = new Character(id, "Sam");
                    characters.put(id, character);
                    broadcast("ADD_PLAYER " + character.toString());
                    System.out.println(character.toString());
                }

                // client
                if (type.equals("client")) {
                    if (!connected && data.startsWith("ADD_PLAYER")) {
                        connected = true;
                        String[] tokens = data.split(" ");
                        characters.put(tokens[1], new Character(tokens[1], "sam"));
                    } else if (!connected) {
                        System.out.println("CONNECTING");
                        send("CONNECT");
                    } else if (connected) {

                    }
                }

            }
        });
 }



	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		update();


        for (Iterator ite = characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            character.handleInput(Gdx.graphics.getDeltaTime());
            broadcast("MOVE_PLAYER " + character.getID() + " " + character.getX() + " " + character.getY());
        }

		batch.begin();
        for (Iterator ite = characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            character.handleInput(Gdx.graphics.getDeltaTime());
		    batch.draw(character.getTexture(), character.getX(), character.getY());
        }
		batch.end();




	}
	
	@Override
	public void dispose () {
		batch.dispose();
        for (Iterator ite = characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            character.handleInput(Gdx.graphics.getDeltaTime());
            character.getTexture().dispose();
        }
	}
}
