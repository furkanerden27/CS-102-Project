package com.mygdx.LordOfTheDices;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    private Texture pauseBtnTexture;


    private int startingGold;
    private String saveName;
    private Level level;

    public PlayScreen(Core game) {
        this(game, 100, "", Level.LEVEL_1);
    }

    public PlayScreen(Core game, int gold, String saveName, Level level) {
        this.game = game;
        this.assets = game.getAssets();
        this.screenManager = game.screen;
        this.startingGold = gold;
        this.saveName = saveName;
        this.level = level;
        camera = new OrthographicCamera();
        viewport = new FitViewport(405, 225, camera);
        map = assets.getMap(level.getMapFile());
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        pauseBtnTexture = assets.getTexture(Assets.TEX_PAUSE_BTN);
        initialiseEntities();
    }

    private void initialiseEntities() {
        entities = new ArrayList<>();
        player = new Player(200, 300, 350, map);
        player.getInventory().setGold(startingGold);
        entities.add(player);

        BasicMob mob = new BasicMob(0, 0);
        mob.setEntity(map);
        entities.add(mob);

        Boss boss = createBoss(level.getBossName(), 0, 0);
        if (boss != null) {
            String originalName = boss.name;
            boss.name = "Boss";
            boss.setEntity(map);
            boss.name = originalName;
            entities.add(boss);
        }

        goldDisplay = new FloatingText("Gold: 0", 0, 0, Color.GOLD);
        goldDisplay.setImmovable();
        goldDisplay.setDurationIndefinite();
    }

    private Boss createBoss(String bossName, float x, float y) {
        switch (bossName) {
            case "Pride":    return new Pride(x, y);
            case "Envy":     return new Envy(x, y);
            case "Wrath":    return new Wrath(x, y);
            case "Lust":     return new Lust(x, y);
            case "Sloth":    return new Sloth(x, y);
            case "Gluttony": return new Gluttony(x, y);
            default:         return null;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (Entity e : entities) {
            e.update(delta);
        }

        camera.position.set(player.getX() + (player.getWidth() / 2),
                            player.getY() + (player.getHeight() / 2), 0);
        camera.update();

        handleInput();

        mapRenderer.setView(camera);
        mapRenderer.render();

        float goldX = camera.position.x - (viewport.getWorldWidth() / 2) + 20;
        float goldY = camera.position.y + (viewport.getWorldHeight() / 2) - 15;
        goldDisplay.setPosition(goldX, goldY);
        goldDisplay.setText("Gold: " + player.getGold());

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (Entity e : entities) {
            e.draw(game.batch);
        }

        if (goldDisplay != null) {
            goldDisplay.render(game.batch);
        }

        float btnScale = 0.1f;
        float btnW = pauseBtnTexture.getWidth() * btnScale;
        float btnH = pauseBtnTexture.getHeight() * btnScale;
        float btnX = camera.position.x + (viewport.getWorldWidth() / 2) - btnW + 5;
        float btnY = camera.position.y + (viewport.getWorldHeight() / 2) - btnH + 5;
        game.batch.draw(pauseBtnTexture, btnX, btnY, btnW, btnH);

        game.batch.end();
        removeDeadEntities();

    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            float btnScale = 0.1f;
            float btnW = pauseBtnTexture.getWidth() * btnScale;
            float btnH = pauseBtnTexture.getHeight() * btnScale;
            float btnX = camera.position.x + (viewport.getWorldWidth() / 2) - btnW - 10;
            float btnY = camera.position.y + (viewport.getWorldHeight() / 2) - btnH;

            com.badlogic.gdx.math.Vector3 touch = new com.badlogic.gdx.math.Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

            if (touch.x >= btnX && touch.x <= btnX + btnW && touch.y >= btnY && touch.y <= btnY + btnH) {
                screenManager.showScreen(Screens.PAUSE, this);
                return;
            }
        }

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
            Mob nearestMob = findNearestMob();
            if (nearestMob != null) {
                new FightManager(player, nearestMob, screenManager);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            screenManager.showScreen(Screens.INVENTORY, player.getInventory());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            for (Entity e : entities) {
                if (e instanceof BasicMob) {
                    ((BasicMob) e).specialAttack(player);
                    break;
                }
            }
        }
    }

    private Mob findNearestMob() {
        float interactionRange = 50f;
        Mob nearest = null;
        float minDist = Float.MAX_VALUE;
        for (Entity e : entities) {
            if (e instanceof Mob && e.isAlive()) {
                float dist = Math.abs(e.getX() - player.getX());
                if (dist < interactionRange && dist < minDist) {
                    minDist = dist;
                    nearest = (Mob) e;
                }
            }
        }
        return nearest;
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

    public String getSaveName() { return saveName; }

    public int getCurrentGold() { return player.getInventory().getGold(); }

    public Level getLevel() { return level; }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}