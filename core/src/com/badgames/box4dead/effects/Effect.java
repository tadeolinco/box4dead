package com.badgames.box4dead.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Effect {
    private float x, y, width, height, timer;

    public Effect(float x, float y, float width, float height, float timer) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.timer = timer;
    }

    public void update() {
        setTimer(getTimer() - Gdx.graphics.getDeltaTime());
    }

    public void draw(ShapeRenderer shapeRenderer) {}

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getTimer() {
        return timer;
    }

    public void setTimer(float timer) {
        this.timer = timer;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
