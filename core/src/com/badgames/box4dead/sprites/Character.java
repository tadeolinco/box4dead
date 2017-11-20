package com.badgames.box4dead.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.UUID;

public class Character extends Sprite {
    String id, name;

    public Character(String name){
        super();
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.setTexture(new Texture("char.png"));
        this.setBounds(0, 0, this.getWidth(), this.getHeight());
    }


    public boolean handleInput(float dt){
        boolean touched = false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.setPosition(this.getX() + (-200 * dt), this.getY());
            touched = true;
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            this.setPosition(this.getX() + (+200 * dt), this.getY());
            touched = true;
        }else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            this.setPosition(this.getX(), this.getY() + (+200 * dt));
            touched = true;
        }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            this.setPosition(this.getX(), this.getY() + (-200 * dt));
            touched = true;
        }
        this.setBounds(this.getX(), this.getY(),this.getWidth(), this.getHeight());
        return touched;
    }


    public String getID() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
