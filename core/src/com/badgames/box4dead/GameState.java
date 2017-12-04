package com.badgames.box4dead;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ObjectMap;

public class GameState extends Game {
    public static ObjectMap characters, bullets, zombies;
    public static TiledMap tiledMap;
    public static boolean isGamePlaying = true;
    @Override
    public void create() {

    }
}
