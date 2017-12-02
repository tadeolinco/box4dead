package com.badgames.box4dead.sprites;

import com.badgames.box4dead.Constants;
import com.badgames.box4dead.GameClient;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

import java.util.Iterator;
import java.util.UUID;

public class Zombie extends Sprite implements Constants {
    public static float SPEED = 50f;
    public static int WIDTH = 75;
    public static int HEIGHT = 75;
    public static float MAX_HP = 50f;

    private float damage = 10f;
    private float attackTimer = 0f;

    private String id;
    private float hp = MAX_HP;

    public Zombie() {
        this.id = UUID.randomUUID().toString();
        setX((float) Math.random() * GAME_WIDTH);
        setY((float) Math.random() * GAME_HEIGHT);
        this.setBounds(getX(), getY(), WIDTH, HEIGHT);
        setColor(new Color(0.5f, 0.5f, 0.5f, 1));
    }


    public void move() {
        float prevX = getX();
        double minDistance = Double.POSITIVE_INFINITY;
        float characterX = 0, characterY = 0, dx = 0, dy = 0;
        for (Iterator ite = GameClient.characters.values(); ite.hasNext(); ) {
            Character character = (Character) ite.next();
            double distance = Math.sqrt(
                    Math.pow((getX() + WIDTH / 2) - (character.getX() + character.getWidth() / 2), 2)
                            + Math.pow((getY() + HEIGHT / 2) - (character.getY() + character.getHeight() / 2), 2)
            );
            if (distance < minDistance) {
                minDistance = distance;
                characterX = character.getX();
                characterY = character.getY();
            }
        }
        if (characterX > getX()) {
            dx = getX() + SPEED * Gdx.graphics.getDeltaTime();
        }
        if (characterX < getX()) {
            dx = getX() - SPEED * Gdx.graphics.getDeltaTime();
        }

        if (characterY > getY()) {
            dy = getY() + SPEED * Gdx.graphics.getDeltaTime();
        }
        if (characterY < getY()) {
            dy = getY() - SPEED * Gdx.graphics.getDeltaTime();
        }

        boolean moveableX = true;
        boolean moveableY = true;
        for (Iterator ite = GameClient.zombies.keys(); ite.hasNext(); ) {
            Zombie zombie = (Zombie) GameClient.zombies.get(ite.next());
            if (!id.equals(zombie.getId())) {
                Rectangle bounds = new Rectangle(getBoundingRectangle());
                bounds.setX(dx);
                if (moveableX && bounds.overlaps(zombie.getBoundingRectangle())) {
                    moveableX = false;
                }
                bounds.setX(prevX);
                bounds.setY(dy);
                if (moveableY && bounds.overlaps(zombie.getBoundingRectangle())) {
                    moveableY = false;
                }
                if (!moveableX && !moveableY)break;
            }
        }
        if (moveableX || moveableY) {
            for (Iterator ite = GameClient.characters.values(); ite.hasNext(); ) {
                Character character = (Character) ite.next();
                Rectangle bounds = new Rectangle(getBoundingRectangle());
                bounds.setX(dx);
                if (moveableX && bounds.overlaps(character.getBoundingRectangle())) {
                    moveableX = false;
                }
                bounds.setX(prevX);
                bounds.setY(dy);
                if (moveableY && bounds.overlaps(character.getBoundingRectangle())) {
                    moveableY = false;
                }
                if (!moveableX && !moveableY) break;
            }
        }
        if (moveableX) setX(dx);
        if (moveableY) setY(dy);
    }

    public String handleAttack() {
        String characterId = "";
        attackTimer += Gdx.graphics.getDeltaTime();
        if (attackTimer > 1) {
            attackTimer = attackTimer % 1;

            double minDistance = (Character.WIDTH / 2 + WIDTH / 2) * 1.2;
            for (Iterator ite = GameClient.characters.values(); ite.hasNext(); ) {
                Character character = (Character) ite.next();
                double distance = Math.sqrt(
                        Math.pow((getX() + WIDTH / 2) - (character.getX() + character.getWidth() / 2), 2)
                        + Math.pow((getY() + HEIGHT / 2) - (character.getY() + character.getHeight() / 2), 2)
                );
                if (distance < minDistance) {
                    minDistance = distance;
                    characterId = character.getId();
                }
            }
        }
        return characterId;
    }

    public boolean handleKnockBack(int bulletFacing, float force) {
        float prevX = getX();
        float prevY = getY();

        if (bulletFacing == RIGHT) {
            setX(getX() + force);
        }
        if (bulletFacing == LEFT) {
            setX(getX() - force);
        }
        if (bulletFacing == UP) {
            setY(getY() + force);
        }
        if (bulletFacing == DOWN) {
            setY(getY() - force);
        }

        boolean overlapping = false;
        for (Iterator ite = GameClient.zombies.values(); ite.hasNext(); ) {
            Zombie zombie = (Zombie) ite.next();
            if (!id.equals(zombie.getId()) && getBoundingRectangle().overlaps(zombie.getBoundingRectangle())) {
                overlapping = true;
                break;
            }
        }

        if (!overlapping) {
            for (Iterator ite = GameClient.characters.values(); ite.hasNext(); ) {
                Character character = (Character) ite.next();
                if (getBoundingRectangle().overlaps(character.getBoundingRectangle())) {
                    overlapping = true;
                    break;
                }
            }
        }

        if (overlapping) {
            setX(prevX);
            setY(prevY);
            return false;
        }
        return true;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        this.hp = hp;
        if (hp < 10) {
            setColor(0.3f, 0.3f, 0.3f, 1);
        }
        else if (hp < 20) {
            setColor(0.35f, 0.35f, 0.35f, 1);
        }
        else if (hp < 30) {
            setColor(0.4f, 0.4f, 0.4f, 1);
        }
        else if (hp < 40) {
            setColor(0.45f, 0.45f, 0.45f, 1);
        }
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

}
