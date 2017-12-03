package com.badgames.box4dead;

import com.badgames.box4dead.effects.Effect;
import com.badgames.box4dead.effects.DeathEffect;
import com.badgames.box4dead.sprites.Bullet;
import com.badgames.box4dead.sprites.Character;
import com.badgames.box4dead.sprites.Zombie;
import com.badgames.box4dead.util.PlayerScore;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import sun.security.provider.SHA;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Iterator;
import java.util.UUID;


public class Box4Dead extends GameState implements Constants {
	SpriteBatch batch;
	String server, name, data, action, payload, id;
	boolean connected;
	DatagramSocket socket;
	DatagramPacket packet;
	String[] tokens;
    TiledMapRenderer tiledMapRenderer;
//    TiledMapTileLayerz
    ShapeRenderer shapeRenderer;
    BitmapFont font;

    private Array<PlayerScore> playerScores;
    private Assets assets;
    private OrthographicCamera camera;
    private Array<Effect> effects;


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
        zombies = new ObjectMap();
        assets = new Assets();
        effects = new Array<Effect>();
        playerScores = new Array<PlayerScore>();
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
        font = new BitmapFont();
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

                    // expected payload: id hp
                    if (action.equals(CHANGE_HP_PLAYER)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Character character = (Character) characters.get(tokens[0]);
                                character.setHp(Float.parseFloat(tokens[1]));
                            }
                        });
                    }

                    // expected payload: id score
                    if (action.equals(CHANGE_SCORE)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Character character = (Character) characters.get(tokens[0]);
                                character.setScore(Integer.parseInt(tokens[1]));
                            }
                        });
                    }

                    // expected payload: id
                    if (action.equals(KILL_PLAYER)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Character character = (Character) characters.get(tokens[0]);
                                character.kill();
                                effects.add(new DeathEffect(character.getX(), character.getY(), Character.WIDTH, Character.HEIGHT, character.getColor()));
                            }
                        });
                    }

                    // expected payload: id spawnTimer
                    if (action.equals(CHANGE_SPAWN_TIMER)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Character character = (Character) characters.get(tokens[0]);
                                character.setSpawnTimer(Float.parseFloat(tokens[1]));
                            }
                        });
                    }

                    // expected payload: id
                    if (action.equals(SPAWN_PLAYER)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Character character = (Character) characters.get(tokens[0]);
                                character.spawn();
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

                    // expected payload: id x y
                    if (action.equals(ADD_ZOMBIE)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Zombie zombie = new Zombie();
                                zombie.setX(Float.parseFloat(tokens[1]));
                                zombie.setY(Float.parseFloat(tokens[2]));
                                zombie.setId(tokens[0]);
                                zombies.put(zombie.getId(), zombie);
                            }
                        });
                    }

                    // expected payload: id x y
                    if (action.equals(MOVE_ZOMBIE)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Zombie zombie = (Zombie) zombies.get(tokens[0]);
                                zombie.setX(Float.parseFloat(tokens[1]));
                                zombie.setY(Float.parseFloat(tokens[2]));
                            }
                        });
                    }

                    // expected payload: id
                    if (action.equals(KILL_ZOMBIE)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Zombie zombie = (Zombie) zombies.get(tokens[0]);
                                effects.add(new DeathEffect(zombie.getX(), zombie.getY(), Zombie.WIDTH, Zombie.HEIGHT, zombie.getColor()));
                                zombies.remove(tokens[0]);
                            }
                        });
                    }

                    // expected payload: id hp
                    if (action.equals(CHANGE_HP_ZOMBIE)) {
                        final String[] tokens = payload.split(" ");
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Zombie zombie = (Zombie) zombies.get(tokens[0]);
                                zombie.setHp(Float.parseFloat(tokens[1]));
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
        if (character != null && character.isAlive()) {
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

        for (Iterator ite = effects.iterator(); ite.hasNext(); ) {
            Effect effect = (Effect) ite.next();
            effect.update();
            if (effect.getTimer() < 0) {
                effects.removeValue(effect, false);
            }
        }
    }

	@Override
	public void render () {
	    playerScores.clear();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
        update();

        shapeRenderer.setProjectionMatrix(camera.combined);

        for (Iterator ite = effects.iterator(); ite.hasNext(); ) {
            Effect effect = (Effect) ite.next();
            effect.draw(shapeRenderer);
        }


        for (Iterator ite = zombies.values(); ite.hasNext(); ) {
            Zombie zombie = (Zombie) ite.next();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(zombie.getColor());
            shapeRenderer.rect(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight());
            shapeRenderer.end();
        }


        for (Iterator ite = characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            if (character.getId().equals(id)) continue;
            if (character.isAlive()) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(character.getColor());

                shapeRenderer.rect(character.getX(), character.getY(), character.getWidth(), character.getHeight());
                shapeRenderer.end();

                float hpPercent = character.getHp() / 100;
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                if (hpPercent < 0.3f) shapeRenderer.setColor(Color.RED);
                else if (hpPercent < 0.5f) shapeRenderer.setColor(Color.ORANGE);
                else shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.rect(character.getX(), character.getY() + character.getHeight() + 10, hpPercent * character.getWidth(), 5f);
                shapeRenderer.end();
            }

            playerScores.add(new PlayerScore(character.getName(), character.getScore(), character.getColor()));
        }

        Character character = (Character) characters.get(id);
        if (character != null)  {
            if (character.isAlive()) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(character.getColor());

                shapeRenderer.rect(character.getX(), character.getY(), character.getWidth(), character.getHeight());
                shapeRenderer.end();

                float hpPercent = character.getHp() / 100;
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                if (hpPercent < 0.3f) shapeRenderer.setColor(Color.RED);
                else if (hpPercent < 0.5f) shapeRenderer.setColor(Color.ORANGE);
                else shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.rect(character.getX(), character.getY() + character.getHeight() + 10, hpPercent * character.getWidth(), 5f);
                shapeRenderer.end();
            }

            playerScores.add(new PlayerScore(character.getName(), character.getScore(), character.getColor()));
        }


        for (Iterator ite = bullets.values(); ite.hasNext();) {
            Bullet bullet = (Bullet) ite.next();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(bullet.getColor());
            shapeRenderer.rect(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
            shapeRenderer.end();
        }
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.75f);
        shapeRenderer.rect(GAME_WIDTH - 150, GAME_HEIGHT - (50 + playerScores.size * 20), 150, 50 + playerScores.size * 20);
        shapeRenderer.end();

        if (character != null && !character.isAlive()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.75f);
            shapeRenderer.rect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            shapeRenderer.end();
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();

        playerScores.sort();
        for (int i = 0; i < playerScores.size; ++i) {
            font.setColor(playerScores.get(i).color);
            font.draw(batch, playerScores.get(i).name +":", GAME_WIDTH - 140, GAME_HEIGHT - 20 * (i + 1));
            font.draw(batch, playerScores.get(i).score + "", GAME_WIDTH - 40, GAME_HEIGHT - 20 * (i + 1));
        }


        if (character != null && !character.isAlive()) {
            font.draw(batch, "YOU ARE DEAD (" + Math.round(character.getSpawnTimer()) + ")", GAME_WIDTH / 2 - 65, GAME_HEIGHT / 2);
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
