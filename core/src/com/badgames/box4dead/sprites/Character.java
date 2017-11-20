package com.badgames.box4dead.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Character extends Sprite{
    String name;
    Vector2 previousPosition;
    public Character(String name){
        super();
        this.name = name;
        previousPosition = new Vector2(getX(), getY());
        this.setTexture(new Texture("char.png"));
        this.setBounds(0, 0, this.getWidth(), this.getHeight());
    }

    public boolean hasMoved(){
        if(previousPosition.x != getX() || previousPosition.y != getY()){
            previousPosition.x = getX();
            previousPosition.y = getY();
            return true;
        }
        return false;
    }

    public void handleInput(float dt){
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.setPosition(this.getX() + (-200 * dt), this.getY());
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            this.setPosition(this.getX() + (+200 * dt), this.getY());
        }else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            this.setPosition(this.getX(), this.getY() + (+200 * dt));
        }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            this.setPosition(this.getX(), this.getY() + (-200 * dt));
        }
        this.setBounds(this.getX(), this.getY(),this.getWidth(), this.getHeight());
    }

}
