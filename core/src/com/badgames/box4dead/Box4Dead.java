package com.badgames.box4dead;

import com.badgames.box4dead.sprites.Bullet;
import com.badgames.box4dead.sprites.Character;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Iterator;
import java.util.UUID;


public class Box4Dead extends GameClient implements Constants {
	SpriteBatch batch;
	String server, name, data, action, payload, id;
	boolean connected;
	DatagramSocket socket;
	DatagramPacket packet;
	String[] tokens;
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    ShapeRenderer shapeRenderer;

    private Assets assets;
    private OrthographicCamera camera;


	public Box4Dead(String server, String name) {
		this.server = server;
		this.name = name;
		this.id = UUID.randomUUID().toString();
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException e){
            e.printStackTrace();
        }

        characters = new ObjectMap();
        bullets = new ObjectMap();
        assets = new Assets();
	}


	@Override
	public void create () {
	    assets.load();
        assets.getManager().finishLoading();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        tiledMap = new TmxMapLoader().load("map/gameMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        shapeRenderer = new ShapeRenderer();

		new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
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

                    if (data.equals("")) continue;

                    System.out.println("Received: " + data);
                    tokens = data.split(DELIMITER);
                    action = tokens[0];
                    payload = tokens[1];

                    // expected payload (id name red green blue)++
                    if (action.equals(RECEIVE_ALL)) {
                        final int tokenSize = 5;
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < tokens.length / tokenSize; ++i) {
                                    float red = Float.parseFloat(tokens[tokenSize * i + 2]);
                                    float green = Float.parseFloat(tokens[tokenSize * i + 3]);
                                    float blue = Float.parseFloat(tokens[tokenSize * i + 4]);
                                    Character character = new Character(tokens[tokenSize * i + 1], red, green, blue);
                                    character.setId(tokens[tokenSize * i]);
                                    characters.put(character.getId(), character);
                                }
                            }
                        });
                    }

                    // expected payload: id name red green blue
                    if (action.equals(ADD_PLAYER)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                float red = Float.parseFloat(tokens[2]);
                                float green = Float.parseFloat(tokens[3]);
                                float blue = Float.parseFloat(tokens[4]);
                                Character character = new Character(tokens[1], red, green, blue);
                                character.setId(tokens[0]);
                                characters.put(character.getId(), character);
                            }
                        });
                    }

                    // expected payload: id x y facing
                    if (action.equals(MOVE_PLAYER)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Character character = (Character) characters.get(tokens[0]);
                                if (character != null) {
                                    character.move(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Integer.parseInt(tokens[3]));
                                }
                            }
                        });
                    }

                    // expected payload: id character_id x y facing
                    if (action.equals(ADD_BULLET)) {
                        final String[] tokens = payload.split(" ");
                        final Character  character = (Character) characters.get(tokens[1]);
                        final float x = Float.parseFloat(tokens[2]);
                        final float y = Float.parseFloat(tokens[3]);
                        final int facing = Integer.parseInt(tokens[4]);
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Bullet bullet = new Bullet(tokens[1], x, y, facing, character.getColor());
                                bullet.setId(tokens[0]);
                                bullets.put(bullet.getId(), bullet);
                            }
                        });
                    }

                    // expected payload: id, x, y
                    if (action.equals(MOVE_BULLET)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Bullet bullet = (Bullet) bullets.get(tokens[0]);
                                if (bullet != null) {
                                    bullet.setX(Float.parseFloat(tokens[1]));
                                    bullet.setY(Float.parseFloat(tokens[2]));
                                }
                            }
                        });
                    }

                    // expected payload: id
                    if (action.equals(KILL_BULLET)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                bullets.remove(tokens[0]);
                            }
                        });
                    }
                }
            }
        }).start();
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

    public void update() {
	    Character character = (Character) characters.get(id);
        if (character != null) {
            boolean hasMoved = character.handleMove();
             if (hasMoved) {
                send(action(MOVE_PLAYER, payload(character.getId(), character.getX(), character.getY(), character.getFacing())));
            }
            if (character.handleShoot()) {
                float x = character.getX() + Character.WIDTH / 2 - Bullet.WIDTH / 2;
                float y = character.getY() + Character.HEIGHT / 2 - Bullet.HEIGHT / 2;
                send(action(ADD_BULLET, payload(character.getId(), x, y, character.getFacing())));
            }
        }
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
        update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        for (Iterator ite = characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(character.getColor());
            shapeRenderer.rect(character.getX(), character.getY(), character.getWidth(), character.getHeight());
            shapeRenderer.end();
        }


        for (Iterator ite = bullets.values(); ite.hasNext();) {
            Bullet bullet = (Bullet) ite.next();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(bullet.getColor());
            shapeRenderer.rect(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
            shapeRenderer.end();
        }
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
        for (int i = 0; i < args.length; ++i) {
            value += args[i].toString() + " ";
        }
        return value;
    }

    public void send(String msg) {
        try{
            byte[] buf = msg.getBytes();
            InetAddress address = InetAddress.getByName(server);
            packet = new DatagramPacket(buf, buf.length, address, PORT);
            socket.send(packet);
        }catch(IOException e){
            e.printStackTrace();
        }

    }



}
