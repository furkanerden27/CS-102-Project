package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class BattleScreen1 implements Screen{
    
    private Stage stage;
    private TextureAtlas atlas;

    private Texture cardSpades;
    private Texture cardClubs;
    private Texture cardHearts;
    private Texture cardDiamonds;// TODO burasi atlasla degistirilecek? nasil olcak bilmiyom ama bisiler yapilcak
    private Texture background;
    

    public BattleScreen1(int sizeX, int sizeY) {
        stage = new Stage(new FitViewport(sizeX, sizeY));

        cardSpades = new Texture("Cards\\card-spades.png");
        cardClubs = new Texture("Cards\\card-clubs.png");
        cardHearts = new Texture("Cards\\card-hearts.png");
        cardDiamonds = new Texture("Cards\\card-diamonds.png");
        background = new Texture("FightBackground.png");


        Gdx.input.setInputProcessor(stage);
        
        createUI();
    }

    private void createUI(){
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.setDebugAll(true); // isin bitince degistir TODO
        
        Image backGroundImage = new Image(background);
        backGroundImage.setFillParent(true);
        stage.addActor(backGroundImage);
        stage.addActor(mainTable);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }



    @Override
    public void show() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
    
}
