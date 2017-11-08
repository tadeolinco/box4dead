package com.badgames.box4dead.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badgames.box4dead.Box4Dead;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.width = 1024;
//		config.height = 728;
		config.height = 50;
		config.width = 50;
		new LwjglApplication(new Box4Dead(), config);
	}
}
