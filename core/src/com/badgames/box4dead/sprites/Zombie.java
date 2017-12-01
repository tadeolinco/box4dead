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
    public static float MAX_HP = 100f;

    private float damage = 10f;
    private float attackTimer = 0f;
    private float stunDuration = 0;

    private String id;
    private float hp = 100;

    public Zombie() {
        this.id = UUID.randomUUID().toString();
        setX((float) Math.random() * GAME_WIDTH);
        setY((float) Math.random() * GAME_HEIGHT);
        this.setBounds(getX(), getY(), WIDTH, HEIGHT);
        setColor(Color.GRAY);
    }


    public void move() {
        stunDuration -= Gdx.graphics.getDeltaTime();
        if (stunDuration > 0) return;
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
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setStunDuration(float stunDuration) {
        this.stunDuration = stunDuration;
    }
}
