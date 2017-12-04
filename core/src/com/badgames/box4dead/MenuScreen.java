package com.badgames.box4dead;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class MenuScreen implements Screen {

    Box4Dead game;
    SpriteBatch batch;
    Texture backgroundTexture;
    Sprite backgroundSprite;
    Stage stage;

    public MenuScreen(Box4Dead box4dead) {

        game = box4dead;

        backgroundTexture = new Texture("menu/TUTORIALS.png");
        backgroundSprite =new Sprite(backgroundTexture);
        stage = new Stage();

    }

    @Override
    public void dispose() {

        try {
            stage.getBatch().dispose();
            stage.dispose();
        } catch (Exception e) {

        }


    }

    @Override
    public void hide() {


    }
    @Override
    public void pause() {



    }
    @Override
    public void render(float delta) {

        stage.getBatch().begin();
            backgroundSprite.draw(stage.getBatch());
        stage.getBatch().end();

    }
    @Override
    public void resize(int height, int width) {
    }

    @Override
    public void resume() {



    }
    @Override
    public void show() {



    }

}
