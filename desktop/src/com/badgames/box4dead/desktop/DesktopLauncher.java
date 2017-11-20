package com.badgames.box4dead.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badgames.box4dead.Box4Dead;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// launch window only in client mode
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 728;
		String type = arg[0];
		String server = arg[1];
		new LwjglApplication(new Box4Dead(type, server), config);
	}
}
