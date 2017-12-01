package com.badgames.box4dead.sprites;

import com.badgames.box4dead.Assets;
import com.badgames.box4dead.Constants;
import com.badgames.box4dead.GameClient;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Iterator;
import java.util.UUID;

public class Bullet extends Sprite implements Constants {
    public static final float SPEED = 1000f;
    public static final float WIDTH = 10;
    public static final float HEIGHT = 10;
    String id, characterId  ;
    int facing;
    float damage = 10f;


    public Bullet(String characterId, float x, float y, int facing, Color color) {
        super();
        id = UUID.randomUUID().toString();
        this.facing = facing;
        this.characterId = characterId;
        this.setBounds(x, y, WIDTH, HEIGHT);
        setColor(color);
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

    public String hitCharacter() {
        for (Iterator ite = GameClient.characters.values(); ite.hasNext(); ) {
            Character character = (Character) ite.next();
            if (!characterId.equals(character.getId()) && getBoundingRectangle().overlaps(character.getBoundingRectangle())) {
                return character.getId();
            }
        }
        return "";
    }

    public String hitZombie() {
        for (Iterator ite = GameClient.zombies.values(); ite.hasNext(); ) {
            Zombie zombie = (Zombie) ite.next();
            if (getBoundingRectangle().overlaps(zombie.getBoundingRectangle())) {
                return zombie.getId();
            }
        }
        return "";
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

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }
}
