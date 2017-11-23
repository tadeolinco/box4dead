package com.badgames.box4dead;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets implements Constants {
    public static AssetManager manager;

    public Assets() {
        manager = new AssetManager();
    }

    public void load() {
        manager.load(CHARACTER_UP, Texture.class);
        manager.load(CHARACTER_DOWN, Texture.class);
        manager.load(CHARACTER_LEFT, Texture.class);
        manager.load(CHARACTER_RIGHT, Texture.class);
        manager.load(BULLET, Texture.class);
    }

    public static AssetManager getManager() {
        return manager;
    }
}
