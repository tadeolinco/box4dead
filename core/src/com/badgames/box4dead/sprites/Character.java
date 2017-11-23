package com.badgames.box4dead.sprites;

import com.badgames.box4dead.Assets;
import com.badgames.box4dead.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.UUID;

public class Character extends Sprite implements Constants {


    public static float SPEED = 400f;

    private String id, name;
    private int facing = DOWN;
    String texture;


    public Character(String name) {
        super();
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.setTexture(Assets.getManager().get(CHARACTER_DOWN, Texture.class));
        this.setBounds(0, 0, this.getTexture().getWidth(), this.getTexture().getHeight());
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
        this.setX(x);
        this.setY(y);

        // make sure that character won't go outside the game world
        if (x + this.getWidth() > GAME_WIDTH)
            this.setX(GAME_WIDTH - this.getWidth());
        if (y + this.getHeight() > GAME_HEIGHT)
            this.setY(GAME_HEIGHT - this.getHeight());
        if (x < 0)
            this.setX(0);
        if (y < 0)
            this.setY(0);

        // only change texture and bounds if needed
        if (this.facing != facing) {
            this.facing = facing;
            texture = "";
            switch (facing) {
                case UP: texture = CHARACTER_UP; break;
                case DOWN: texture = CHARACTER_DOWN; break;
                case RIGHT: texture = CHARACTER_RIGHT; break;
                case LEFT: texture = CHARACTER_LEFT; break;
            }
            this.setTexture(Assets.getManager().get(texture, Texture.class));
            this.setBounds(this.getX(), this.getY(), this.getTexture().getWidth(), this.getTexture().getHeight());
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
