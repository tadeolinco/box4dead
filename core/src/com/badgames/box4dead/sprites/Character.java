package com.badgames.box4dead.sprites;

import com.badgames.box4dead.Constants;
import com.badgames.box4dead.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Iterator;
import java.util.UUID;

public class Character extends Sprite implements Constants {


    public static float SPEED = 400f;
    public static int WIDTH = 60;
    public static int HEIGHT = 60;
    public static float MAX_HP = 100f;
    public static float REGEN_RATE = 0.5f;
    public static float SPAWN_RATE = 10f;

    private String id, name;
    private int facing = DOWN;
    private float hp = MAX_HP;
    private float regenCounter = 0;
    private int score = 0;
    private boolean isAlive = true;
    private float spawnTimer = 0f;


    public Character(String name, float red, float green, float blue) {
        super();
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.setBounds(getX(), getY(), WIDTH, HEIGHT);
        setColor(new Color(red, green, blue, 1));
        setX(GAME_WIDTH / 2 - WIDTH / 2);
        setY(GAME_HEIGHT / 2 - HEIGHT / 2);
    }

    public Character(String name) {
        super();
        this.name = name;
        this.id = UUID.randomUUID().toString();
        setX((float) Math.random() * GAME_WIDTH);
        setY((float) Math.random() * GAME_HEIGHT);
        this.setBounds(getX(), getY(), WIDTH, HEIGHT);
        setColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));
    }

    public boolean handleRegen() {
        if (this.hp < MAX_HP) {
            regenCounter += Gdx.graphics.getDeltaTime();
            if (regenCounter > REGEN_RATE) {
                regenCounter = regenCounter % REGEN_RATE;
                return true;
            }
        }
        return false;
    }


    public boolean handleMove() {
        float dt = Gdx.graphics.getDeltaTime();
        float x = 0f, y = 0f;
        int facing = STILL;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            facing = RIGHT;
            x = this.getX() + SPEED * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            facing = LEFT;
            x = this.getX() - SPEED * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            facing = UP;
            y = this.getY() + SPEED * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            facing = DOWN;
            y = this.getY() - SPEED * dt;
        }

        // if character moved, apply move
        if (facing != STILL) {
            if (x == 0) x = getX();
            if (y == 0) y = getY();
            move(x, y, facing);
            return true;
        }

        return false;
    }

    public void move(float x, float y, int facing) {
        this.facing = facing;
        float prevX = getX();
        float prevY = getY();


        this.setX(x);
        this.setY(y);

        boolean isOverlapping = wallCollision();

// removed to only have one spawn point for all characters, easier implementation for spawn points
//        if (!isOverlapping) {
    //        for (Iterator ite = GameState.characters.values(); ite.hasNext();) {
    //            Character character = (Character) ite.next();
    //            if (!id.equals(character.getId()) && getBoundingRectangle().overlaps(character.getBoundingRectangle())) {
    //                isOverlapping = true;
    //                break;
    //            }
    //        }
//        }

        if (!isOverlapping) {
            for (Iterator ite = GameState.zombies.values(); ite.hasNext(); ) {
                Zombie zombie = (Zombie) ite.next();
                if (getBoundingRectangle().overlaps(zombie.getBoundingRectangle())) {
                    isOverlapping = true;
                    break;
                }
            }
        }

        if (isOverlapping) {
            setX(prevX);
            setY(prevY);
            return;
        }

        // make sure that character won't go outside the game world
        if (x + this.getWidth() > GAME_WIDTH)
            this.setX(GAME_WIDTH - this.getWidth());
        else if (y + this.getHeight() > GAME_HEIGHT)
            this.setY(GAME_HEIGHT - this.getHeight());
        else if (x < 0)
            this.setX(0);
        else if (y < 0)
            this.setY(0);
        else {
            this.setX(x);
            this.setY(y);
        }

    }


    public boolean wallCollision(){
        MapLayer collisionObjectLayer = GameState.tiledMap.getLayers().get("Object Layer 1");
        MapObjects objects = collisionObjectLayer.getObjects();
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, getBoundingRectangle())) {
                return true;
            }
        }
        return false;
    }


    public boolean handleShoot() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            return true;
        return false;
    }

    public void spawn() {
        spawnTimer = 0;
        hp = MAX_HP;
        setX(GAME_WIDTH / 2 - WIDTH / 2);
        setY(GAME_HEIGHT / 2 - HEIGHT / 2);
        isAlive = true;
    }

    public void kill() {
        spawnTimer = SPAWN_RATE;
        isAlive = false;
        score -= Math.ceil(score / 5);
        if (score < 0) score = 0;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        this.hp = Math.max(0, Math.min(MAX_HP, hp));
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public float getSpawnTimer() {
        return spawnTimer;
    }

    public void setSpawnTimer(float spawnTimer) {
        this.spawnTimer = spawnTimer;
    }
}
