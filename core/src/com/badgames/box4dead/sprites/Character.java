package com.badgames.box4dead.sprites;

import com.badgames.box4dead.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.UUID;

public class Character extends Sprite implements Constants {
    String id, name;
    float SPEED = 200f;

    int hDirection; //0 - LEFT, 1 - RIGHT
    int vDirection; //-1 left or right, 0 - down, 1 - up
    Texture texture = new Texture("char.png");

    public Character(String name){
        super();
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.setTexture(texture);
        this.setBounds(0, 0, this.getTexture().getWidth(), this.getTexture().getHeight());
        hDirection = 0;       //initial direction of where the sprite is facing
        vDirection = -1;
    }

//    public boolean handleMove(float dt){
//        float x = this.getX();
//        float y = this.getY();
//        dt = SPEED;
//        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            this.setPosition(this.getX() + (-200 * dt), this.getY());
//
//            //this.flipSprite(hDirection,vDirection);
//            if(hDirection == 1){ //face the sprite to the left, if facing right
//                hDirection =0;
//                this.flip(true, false);
//            }
//
//            if(vDirection == 1){ //if the sprite is facing upward, make it face downward
//                vDirection =0;
//                this.flip(false, true);
//            }
//        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
//            this.setPosition(this.getX() + (+200 * dt), this.getY());
//
//            if(hDirection == 0){ //face the sprite to the right, if facing left
//                hDirection =1;
//                this.flip(true, false);
//            }
//
//            if(vDirection == 1){    //if the sprite is facing upward, make it face downward
//                vDirection =0;
//                this.flip(false, true);
//            }
//        }else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
//            this.setPosition(this.getX(), this.getY() + (+200 * dt));
//
//            if(vDirection == -1){  //change the initial value
//                vDirection =1;
//                this.flip(false, true);
//            }
//            else if(vDirection == 0){ //if the sprite is facing downward, make it face upward
//                vDirection =1;
//                this.flip(false, true);
//            }
//        }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
//            this.setPosition(this.getX(), this.getY() + (-200 * dt));
//
//            if(vDirection == -1){  //change the initial value
//                vDirection =0;
//                this.flip(false, true);
//            }
//            else if(vDirection == 1){ //if the sprite is facing upward, make it face downward
//                vDirection =0;
//                this.flip(false, true);
//            }
//        }
//
//        if (x == this.getX() && y == this.getY()) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    public int handleMove2() {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            return RIGHT;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            return LEFT;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            return UP;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            return DOWN;
        return STILL;
    }

    public void move(int direction) {
        float dt = Gdx.graphics.getDeltaTime();
        switch(direction) {
            case RIGHT:
                this.setX(this.getX() + SPEED * dt);
                break;
            case LEFT:
                this.setX(this.getX() - SPEED * dt);
                break;
            case UP:
                this.setY(this.getY() + SPEED * dt);
                break;
            case DOWN:
                this.setY(this.getY() - SPEED * dt);
                break;
        }
        if (this.getX() + this.getWidth() > GAME_WIDTH)
            this.setX(GAME_WIDTH - this.getWidth());
        if (this.getY() + this.getHeight() > GAME_HEIGHT)
            this.setY(GAME_HEIGHT - this.getHeight());
        if (this.getX() < 0)
            this.setX(0);
        if (this.getY() < 0)
            this.setY(0);
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

    public int getHDirection() {
        return hDirection;
    }

    public void setHDirection(int hDirection) {
        this.hDirection = hDirection;
    }

    public int getVDirection() {
        return vDirection;
    }

    public void setVDirection(int vDirection) {
        this.vDirection = vDirection;
    }
}
