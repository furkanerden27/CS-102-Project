package io.github.furkanerden27.TestGameV3;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PlayScreen implements Screen {
    private final Core game; // Ana sınıfa erişim için
    private FitViewport viewport;
    private Dice dice1;
    private Dice dice2;
    private Vector3 touchPoint;
    private ArrayList<Dice> dices;

    private float stateTime = 0;


    public PlayScreen(Core game) {
        this.game = game;
        viewport = new FitViewport(300, 200);
        dice1 = new Dice(game.batch, -16, -16);
        dice2 = new Dice(game.batch, 50, 50);
        touchPoint = new Vector3();
        dices = new ArrayList<>(); 
        dices.add(dice1);
        dices.add(dice2);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        viewport.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        game.batch.begin();
        for (Dice dice : dices) {
            dice.draw(touchPoint.x, touchPoint.y);
        }
        game.batch.end();
    }

    private void handleInput() {}

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void show() {} // Ekran ilk açıldığında
    @Override public void hide() {} // Başka ekrana geçildiğinde
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {

    }
}