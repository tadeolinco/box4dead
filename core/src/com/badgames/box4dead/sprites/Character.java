package com.badgames.box4dead.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Character extends Sprite{
    Vector2 previousPosition;   

    int h_direction; //0 - LEFT, 1 - RIGHT
    int v_direction; //-1 left or right, 0 - down, 1 - up
    public Character(Texture texture){
        super(texture);

        h_direction = 0;       //initial direction of where the sprite is facing
        v_direction = -1;
        previousPosition = new Vector2(getX(), getY());
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
//        if(player != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                this.setPosition(this.getX() + (-200 * dt), this.getY());

                //this.flipSprite(h_direction,v_direction);
                if(h_direction == 1){ //face the sprite to the left, if facing right
                    h_direction=0;
                    this.flip(true, false);
                }

                if(v_direction == 1){ //if the sprite is facing upward, make it face downward
                    v_direction=0;
                    this.flip(false, true);
                }
            } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                this.setPosition(this.getX() + (+200 * dt), this.getY());

                if(h_direction == 0){ //face the sprite to the right, if facing left
                    h_direction=1;
                    this.flip(true, false);
                }

                if(v_direction == 1){    //if the sprite is facing upward, make it face downward
                    v_direction=0;
                    this.flip(false, true);
                }
            }else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
                this.setPosition(this.getX(), this.getY() + (+200 * dt));

                if(v_direction == -1){  //change the initial value
                    v_direction=1;
                    this.flip(false, true);
                }
                else if(v_direction == 0){ //if the sprite is facing downward, make it face upward
                    v_direction=1;
                    this.flip(false, true);
                }
            }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                this.setPosition(this.getX(), this.getY() + (-200 * dt));

                if(v_direction == -1){  //change the initial value
                    v_direction=0;
                    this.flip(false, true);
                }
                else if(v_direction == 1){ //if the sprite is facing upward, make it face downward
                    v_direction=0;
                    this.flip(false, true);
                }
            }
//        }
    }
}
