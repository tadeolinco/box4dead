package com.badgames.box4dead.effects;

import com.badgames.box4dead.sprites.Zombie;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DeathEffect extends Effect {
    private Color color;

    public DeathEffect(float x, float y, Color color) {
        super(x, y, 0.4f);
        this.color = new Color(0.3f, 0.3f, 0.3f, 1);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        if (getTimer() <= 0.3f && getTimer() > 0.2f || getTimer() <= 0.1f && getTimer() > 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(color);
            shapeRenderer.rect(getX(), getY(), Zombie.WIDTH, Zombie.HEIGHT);
            shapeRenderer.end();
        }
    }

}
