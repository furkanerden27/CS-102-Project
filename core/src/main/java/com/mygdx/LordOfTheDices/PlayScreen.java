package com.mygdx.LordOfTheDices;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PlayScreen implements Screen {
    private final Core game;
    private final Assets assets;
    private final ScreenManager screenManager;
    private FitViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Player player;
    private OrthographicCamera camera;
    private ArrayList<Entity> entities;
    private float stateTime = 0;
    private FloatingText goldDisplay;


    public PlayScreen(Core game) {
        this.game = game;
        this.assets = game.getAssets();
        this.screenManager = game.screen;
        camera = new OrthographicCamera();
        viewport = new FitViewport(450, 250, camera);
        map = assets.getMap(Assets.MAP_1);
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        initialiseEntities();
    }

    private void initialiseEntities() {
        entities = new ArrayList<>();
        player = new Player(200, 100, 200, map);
        entities.add(player);
        
        Pride pride = new Pride(300, 100);
        entities.add(pride);
        Envy envy = new Envy(1200, 60);
        entities.add(envy);
        Wrath wrath = new Wrath(1300, 60);
        entities.add(wrath);
        //Gluttony gluttony = new Gluttony(1400, 60);
        //gluttony.setEntity(map);
        //entities.add(gluttony);
        BasicMob mob1 = new BasicMob(1600, 60);
        mob1.setEntity(map);
        entities.add(mob1);
        Lust lust = new Lust(1000, 100);
        entities.add(lust);
        Sloth sloth = new Sloth(1100, 100);
        entities.add(sloth);

        goldDisplay = new FloatingText("Gold: 0", 0, 0, Color.GOLD);
        goldDisplay.setImmovable();
        goldDisplay.setDurationIndefinite(); 
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        for (Entity e : entities) {
            e.update(delta);
        }

        camera.position.set(player.getX() + (player.getWidth() / 2), 
                            player.getY() + (player.getHeight() / 2), 0);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        // --- Updating gold text ---
        float goldX = camera.position.x - (viewport.getWorldWidth() / 2) + 20;
        float goldY = camera.position.y + (viewport.getWorldHeight() / 2) - 15;

        goldDisplay.setPosition(goldX, goldY);
        goldDisplay.setText("Gold: " + player.getGold());
        // ----------------------------

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (Entity e : entities) {
            e.draw(game.batch);
        }

        if (goldDisplay != null) {
            goldDisplay.render(game.batch); 
        }
        game.batch.end();
        removeDeadEntities();

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) { 
            //TODO THIS IS JUST FOR TESTING REMOVE LATER
            
            screenManager.showScreen(Screens.BATTLE, new FightManager(player, null));
            //game.setScreen(new CombatScreen());
            
            
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            screenManager.showScreen(Screens.INVENTORY, player.getInventory());
        }
        // Test Gluttony's special attack remove after testing
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) { 
            
            for (Entity e : entities) {
                if (e instanceof Sloth) {
                    ((Sloth) e).specialAttack(player);
                    break;
                }
            }
        }
    }

     private void removeDeadEntities() {
        ArrayList<Entity> toRemove = new ArrayList<>();
        for (Entity e : entities) {
            if (!e.isAlive()) {
                toRemove.add(e);
            }
        }
        for (Entity e : toRemove) {
            entities.remove(e);
        }
    }

    @Override public void resize(int width, int height) { 
        viewport.update(width, height); 
    }

    @Override public void show() {} // when screen is initialized
    @Override public void hide() {} // when screen is changed
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}