package com.badgames.box4dead.desktop;

import com.badgames.box4dead.GameServer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badgames.box4dead.Box4Dead;

public class DesktopLauncher {
	public static void main (String[] arg) {
		if (arg[0].equals("client")) {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.width = 1024;
			config.height = 728;
			String server = arg[1];
			String name = arg[2];
			new LwjglApplication(new Box4Dead(server, name), config);
		} else {
			// Server side code here
			int numOfPlayers = Integer.parseInt(arg[1]);
			new GameServer(numOfPlayers);
		}
	}
}
