package com.badgames.box4dead.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.UUID;

public class Character extends Sprite {
    String id, name;

    int hDirection; //0 - LEFT, 1 - RIGHT
    int vDirection; //-1 left or right, 0 - down, 1 - up
    public Character(String name){
        super();
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.setTexture(new Texture("char.png"));
        this.setBounds(0, 0, this.getWidth(), this.getHeight());
        hDirection = 0;       //initial direction of where the sprite is facing
        vDirection = -1;
    }

    public boolean handleInput(float dt){
        float x = this.getX();
        float y = this.getY();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.setPosition(this.getX() + (-200 * dt), this.getY());

            //this.flipSprite(hDirection,vDirection);
            if(hDirection == 1){ //face the sprite to the left, if facing right
                hDirection =0;
                this.flip(true, false);
            }

            if(vDirection == 1){ //if the sprite is facing upward, make it face downward
                vDirection =0;
                this.flip(false, true);
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            this.setPosition(this.getX() + (+200 * dt), this.getY());

            if(hDirection == 0){ //face the sprite to the right, if facing left
                hDirection =1;
                this.flip(true, false);
            }

            if(vDirection == 1){    //if the sprite is facing upward, make it face downward
                vDirection =0;
                this.flip(false, true);
            }
        }else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            this.setPosition(this.getX(), this.getY() + (+200 * dt));

            if(vDirection == -1){  //change the initial value
                vDirection =1;
                this.flip(false, true);
            }
            else if(vDirection == 0){ //if the sprite is facing downward, make it face upward
                vDirection =1;
                this.flip(false, true);
            }
        }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            this.setPosition(this.getX(), this.getY() + (-200 * dt));

            if(vDirection == -1){  //change the initial value
                vDirection =0;
                this.flip(false, true);
            }
            else if(vDirection == 1){ //if the sprite is facing upward, make it face downward
                vDirection =0;
                this.flip(false, true);
            }
        }

        if (x == this.getX() && y == this.getY()) {
            return false;
        } else {
            return true;
        }
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

    public int gethDirection() {
        return hDirection;
    }

    public void sethDirection(int hDirection) {
        this.hDirection = hDirection;
    }

    public int getvDirection() {
        return vDirection;
    }

    public void setvDirection(int vDirection) {
        this.vDirection = vDirection;
    }
}
