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
    private Shop shop;
    private float merchantX = -1;

    private int startingGold;
    private float startingHealth;
    private float startingX, startingY;
    private String saveName;
    private Level level;
    private String savedCards, savedDice, savedRelics;

    public PlayScreen(Core game) {
        this(game, 100, 200, 300, 350, "", Level.LEVEL_1, "", "", "");
    }

    public PlayScreen(Core game, int gold, float health, float playerX, float playerY,
                      String saveName, Level level, String cards, String dice, String relics) {
        this.game = game;
        this.assets = game.getAssets();
        this.screenManager = game.screen;
        this.startingGold = gold;
        this.startingHealth = health;
        this.startingX = (playerX == 0 && playerY == 0) ? 300 : playerX;
        this.startingY = (playerX == 0 && playerY == 0) ? 350 : playerY;
        this.saveName = saveName;
        this.level = level;
        this.savedCards = cards;
        this.savedDice = dice;
        this.savedRelics = relics;
        camera = new OrthographicCamera();
        viewport = new FitViewport(405, 225, camera);
        map = assets.getMap(level.getMapFile());
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        pauseBtnTexture = assets.getTexture(Assets.TEX_PAUSE_BTN);
        initialiseEntities();
        shop = new Shop(player.getInventory());
    }

    private void initialiseEntities() {
        entities = new ArrayList<>();
        if (savedCards != null && !savedCards.isEmpty()) {
            Inventory loadedInv = Inventory.deserialize(savedCards, savedDice, savedRelics, startingGold);
            player = new Player(startingHealth, startingX, startingY, map, loadedInv);
        } else {
            player = new Player(startingHealth, startingX, startingY, map);
            player.getInventory().setGold(startingGold);
        }
        entities.add(player);

        BasicMob mob = new BasicMob(0, 0);
        mob.setEntity(map);
        entities.add(mob);

        Merchant merchant = new Merchant(0, 0);
        merchant.setEntity(map);
        merchantX = merchant.getX();
        entities.add(merchant);

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
        checkInteraction();
    }

    private void checkInteraction() {
        com.badlogic.gdx.maps.tiled.TiledMapTileLayer endGameLayer =
            (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) map.getLayers().get("EndGame");
        if (endGameLayer == null) return;
        int tileX = (int) (player.getX() / endGameLayer.getTileWidth());
        int tileY = (int) (player.getY() / endGameLayer.getTileHeight());
        if (endGameLayer.getCell(tileX, tileY) == null) return;

        Level next = level.next();
        if (next == null) {
            deleteFromFirebase();
            screenManager.showScreen(Screens.STORY_END);
        } else {
            saveToFirebase(next.getNumber());
            Inventory inv = player.getInventory();
            screenManager.showScreen(Screens.PLAY, inv.getGold(), player.getHealth(), 300, 350,
                saveName, next, inv.serializeCards(), inv.serializeDice(), inv.serializeRelics());
        }
    }

    private void saveToFirebase(int nextLevel) {
        if (saveName == null || saveName.isEmpty()) return;
        String url = "https://lord-of-the-dices-default-rtdb.europe-west1.firebasedatabase.app/saves/" + saveName + ".json";
        Inventory inv = player.getInventory();
        String json = "{\"saveName\":\"" + saveName + "\"," +
                      "\"currentLevel\":" + nextLevel + "," +
                      "\"currentHealth\":" + (int) player.getHealth() + "," +
                      "\"currentMoney\":" + inv.getGold() + "," +
                      "\"playerX\":" + (int) player.getX() + "," +
                      "\"playerY\":" + (int) player.getY() + "," +
                      "\"cards\":\"" + inv.serializeCards() + "\"," +
                      "\"dice\":\"" + inv.serializeDice() + "\"," +
                      "\"relics\":\"" + inv.serializeRelics() + "\"," +
                      "\"timestamp\":" + System.currentTimeMillis() + "}";

        com.badlogic.gdx.Net.HttpRequest req = new com.badlogic.gdx.Net.HttpRequest(com.badlogic.gdx.Net.HttpMethods.PUT);
        req.setUrl(url);
        req.setHeader("Content-Type", "application/json");
        req.setContent(json);
        com.badlogic.gdx.Gdx.net.sendHttpRequest(req, new com.badlogic.gdx.Net.HttpResponseListener() {
            @Override public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse response) {}
            @Override public void failed(Throwable t) {}
            @Override public void cancelled() {}
        });
    }

    private void deleteFromFirebase() {
        if (saveName == null || saveName.isEmpty()) return;
        String url = "https://lord-of-the-dices-default-rtdb.europe-west1.firebasedatabase.app/saves/" + saveName + ".json";
        com.badlogic.gdx.Net.HttpRequest req = new com.badlogic.gdx.Net.HttpRequest(com.badlogic.gdx.Net.HttpMethods.DELETE);
        req.setUrl(url);
        com.badlogic.gdx.Gdx.net.sendHttpRequest(req, new com.badlogic.gdx.Net.HttpResponseListener() {
            @Override public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse response) {}
            @Override public void failed(Throwable t) {}
            @Override public void cancelled() {}
        });
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            if (merchantX >= 0 && Math.abs(player.getX() - merchantX) < 50f) {
                screenManager.showShop(shop);
            }
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

    public float getPlayerHealth() { return player.getHealth(); }

    public float getPlayerX() { return player.getX(); }

    public float getPlayerY() { return player.getY(); }

    public Inventory getPlayerInventory() { return player.getInventory(); }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}