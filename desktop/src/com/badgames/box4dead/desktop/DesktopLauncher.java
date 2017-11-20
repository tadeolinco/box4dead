package com.badgames.box4dead.desktop;

import com.badgames.box4dead.GameServer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badgames.box4dead.Box4Dead;

public class DesktopLauncher {
	public static void main (String[] arg) {
		String type = arg[0];
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		if (type.equals("client")) {
			String server = arg[1];
			String name = arg[2];
			config.width = 800;
			config.height = 600;
			new LwjglApplication(new Box4Dead(server, name), config);
		} else {
            config.width = 50;
            config.height = 50;
			new LwjglApplication(new GameServer(), config);
		}

	}
}
