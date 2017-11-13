package com.badgames.box4dead;

import com.badgames.box4dead.chat.ChatClient;
import com.badgames.box4dead.chat.ChatServer;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.IOException;

public class Box4Dead extends ApplicationAdapter {
	SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		try {
		    new ChatClient();
        } catch(IOException e) {
			try {
				Gdx.app.log("Server", "You are the server");
				new ChatServer();
				new ChatClient();
			}catch (IOException ex){}
        }
	}
//
//	@Override
//	public void render () {
//		Gdx.gl.glClearColor(0, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.end();
//	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
