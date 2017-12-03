package com.badgames.box4dead.util;

import com.badlogic.gdx.graphics.Color;

public class PlayerScore implements Comparable {
    public String name;
    public int score;
    public Color color;

    public PlayerScore(String name, int score, Color color) {
        this.name = name;
        this.score = score;
        this.color = color;
    }

    @Override
    public int compareTo(Object o) {
        return score - ((PlayerScore) o).score;
    }
}
