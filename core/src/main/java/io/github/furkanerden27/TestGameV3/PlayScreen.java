package io.github.furkanerden27.TestGameV3;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PlayScreen implements Screen {
    private final Core game; // to reach to the Core class
    private FitViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Player player; 
    private OrthographicCamera camera;
    private Dice dice1;
    private Dice dice2;
    private Vector3 touchPoint;
    private ArrayList<Dice> dices;

    private float stateTime = 0;


    public PlayScreen(Core game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(450, 250, camera);
        map = new TmxMapLoader().load("Maps/Map 1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map); 
        player = new Player(100, 200, 200, map);
        touchPoint = new Vector3();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.update(delta);

        camera.position.set(player.getX() + (player.getWidth() / 2), 
                            player.getY() + (player.getHeight() / 2), 0);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch); // Player Sprite olduğu için draw metoduna batch verebiliriz
        game.batch.end();

    }

    private void handleInput() {}

    @Override public void resize(int width, int height) { 
        viewport.update(width, height); 
    }

    @Override public void show() {} // when screen is initialized
    @Override public void hide() {} // when screen is changed
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {

    }
}