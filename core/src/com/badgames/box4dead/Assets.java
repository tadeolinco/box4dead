package com.badgames.box4dead;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets implements Constants {
    public static AssetManager manager;

    public Assets() {
        manager = new AssetManager();
    }

    public void load() {
    }

    public static AssetManager getManager() {
        return manager;
    }
}
