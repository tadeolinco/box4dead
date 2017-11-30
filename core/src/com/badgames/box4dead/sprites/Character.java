package com.badgames.box4dead.sprites;

import com.badgames.box4dead.Assets;
import com.badgames.box4dead.Constants;
import com.badgames.box4dead.GameClient;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Iterator;
import java.util.UUID;

public class Character extends Sprite implements Constants {


    public static float SPEED = 400f;
    public static int WIDTH = 75;
    public static int HEIGHT = 75;

    private String id, name;
    private int facing = DOWN;


    public Character(String name, float red, float green, float blue) {
        super();
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.setBounds(getX(), getY(), WIDTH, HEIGHT);
        setColor(new Color(red, green, blue, 1));
        setX((float) Math.random() * GAME_WIDTH);
    }

    public Character(String name) {
        super();
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.setBounds(getX(), getY(), WIDTH, HEIGHT);
        setColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));
        setX((float) Math.random() * GAME_WIDTH);
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

        boolean isOverlapping = false;
        for (Iterator ite = GameClient.characters.values(); ite.hasNext();) {
            Character character = (Character) ite.next();
            if (!id.equals(character.getId()) && getBoundingRectangle().overlaps(character.getBoundingRectangle())) {
                isOverlapping = true;
                break;
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


    public boolean handleShoot() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            return true;
        return false;
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

}
