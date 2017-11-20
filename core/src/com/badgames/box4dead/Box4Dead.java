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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class Box4Dead extends ApplicationAdapter implements Constants {
	SpriteBatch batch;
	String server, data, name;
	boolean connected;
	DatagramSocket socket;

	Array<Character> characters;


	public Box4Dead(String server, String name) {
		this.server = server;
		this.name = name;
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(10);
		} catch (SocketException e) {
			e.printStackTrace();
		}


        characters = new Array<Character>();

	}


	public void send(String msg){
		try{
			byte[] buf = msg.getBytes();
			InetAddress address = InetAddress.getByName(server);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
			socket.send(packet);
		}catch(Exception e){}

	}

	@Override
	public void create () {


		batch = new SpriteBatch();

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

                if (!connected && data.startsWith("CONNECTED")) {
                    connected = true;
                    characters.add(new Character(name));
                    System.out.println("CONNECTED");
                } else if (!connected) {
                    System.out.println("CONNECTING");
                    send("CONNECT " + name);
                } else if (connected) {
                    // do logic here

                }

            }
        });
 }



//	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		update();

        for (Character character : characters) {
            character.handleInput(Gdx.graphics.getDeltaTime());
        }

		batch.begin();
		for (Character character : characters) {
		    System.out.println("Drawing");
		    batch.draw(character.getTexture(), character.getX(), character.getY());
        }
		batch.end();



	}
	
	@Override
	public void dispose () {
		batch.dispose();
        for (Character character : characters) {
            character.getTexture().dispose();
        }
	}
}
