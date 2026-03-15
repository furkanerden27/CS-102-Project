package io.github.furkanerden27.TestGameV3;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PlayScreen implements Screen {
    private final Core game; // to reach to the Core class
    private FitViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Player player; 
    private OrthographicCamera camera;
    private Gluttony gluttony;
    private ArrayList<Entity> entities;

    private float stateTime = 0;


    public PlayScreen(Core game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(450, 250, camera);
        map = new TmxMapLoader().load("Maps/Map 1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map); 
        initialiseEntities();

    }

    private void initialiseEntities() {
        entities = new ArrayList<>();
        player = new Player(100, 200, 200, map);
        entities.add(player);
        gluttony = new Gluttony(1000, 100);
        entities.add(gluttony);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        // Update all entities, not just player
        for (Entity e : entities) {
            e.update(delta);
        }

        camera.position.set(player.getX() + (player.getWidth() / 2), 
                            player.getY() + (player.getHeight() / 2), 0);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (Entity e : entities) {
            e.draw(game.batch);
        }
        game.batch.end();

    }

    private void handleInput() {
        /* getting the input to update the player movement (S is not implemented yet)*/
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.moveLeft();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.moveRight();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) { 
            player.jump();
        }
    }

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