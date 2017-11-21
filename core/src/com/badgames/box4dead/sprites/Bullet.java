package com.badgames.box4dead.sprites;

import com.badgames.box4dead.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.UUID;

public class Bullet extends Sprite implements Constants {
    static final float SPEED = 500f;
    String id;
    int facing;
    Texture texture = new Texture("bullet.png");

    public Bullet(float x, float y, int facing) {
        super();
        id = UUID.randomUUID().toString();
        this.facing = facing;
        this.setTexture(texture);
        this.setBounds(x, y, this.getTexture().getWidth(), this.getTexture().getHeight());
    }

    public void move() {
        float dt = Gdx.graphics.getDeltaTime();
        switch(facing) {
            case UP:
                this.setY(this.getY() + SPEED * dt);
                break;
            case DOWN:
                this.setY(this.getY() - SPEED * dt);
                break;
            case RIGHT:
                this.setX(this.getX() + SPEED * dt);
                break;
            case LEFT:
                this.setX(this.getX() - SPEED * dt);
        }
    }

    public boolean isOutOfWorld() {
        if (this.getX() + this.getWidth() > GAME_WIDTH)
            return true;

        if (this.getX() < 0)
            return true;

        if (this.getY() + this.getHeight() > GAME_HEIGHT)
            return true;

        if (this.getY() < 0)
            return true;

        return false;
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
