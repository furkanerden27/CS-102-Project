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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private Texture pauseBtnTexture;
    private Shop shop;
    private float merchantX = -1;

    private String saveName;
    private Level level;
    private PlayerData playerData;
    private ShapeRenderer shapeRenderer;
    private BitmapFont hudFont;

    public PlayScreen(Core game) {
        this(game, PlayerData.newSave("", 1, 200, 100));
    }

    public PlayScreen(Core game, PlayerData playerData) {
        this.game = game;
        this.assets = game.getAssets();
        this.screenManager = game.screen;
        this.playerData = playerData;
        this.saveName = playerData.saveName;
        this.level = Level.fromNumber(playerData.currentLevel);
        camera = new OrthographicCamera();
        viewport = new FitViewport(405, 225, camera);
        map = assets.getMap(level.getMapFile());
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        pauseBtnTexture = assets.getTexture(Assets.TEX_PAUSE_BTN);
        shapeRenderer = new ShapeRenderer();
        hudFont = new BitmapFont();
        hudFont.getData().setScale(0.5f);
        hudFont.setUseIntegerPositions(false);
        initialiseEntities();
        shop = new Shop(player.getInventory(), player);
    }

    private void initialiseEntities() {
        BasicMob.level = 0;
        entities = new ArrayList<>();
        float px = (playerData.playerX == 0 && playerData.playerY == 0) ? 300 : playerData.playerX;
        float py = (playerData.playerX == 0 && playerData.playerY == 0) ? 350 : playerData.playerY;

        if (!playerData.cards.isEmpty()) {
            Inventory loadedInv = playerData.toInventory();
            player = new Player(playerData.currentHealth, px, py, map, loadedInv);
        } else {
            player = new Player(playerData.currentHealth, px, py, map);
            player.getInventory().setGold(playerData.currentMoney);
        }
        int targetDice = Math.min(level.getNumber(), 6);
        for (int i = 1; i < level.getNumber() && player.getInventory().getDiceCount() < targetDice; i++) {
            Level prev = Level.fromNumber(i);
            String diceName = "Dice Of " + prev.getBossName();
            player.getInventory().addDice(diceName);
        }

        for (Relic r : player.getInventory().getRelics()) {
            r.reapply(player);
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

        float halfW = viewport.getWorldWidth() / 2f;
        float halfH = viewport.getWorldHeight() / 2f;
        float hudLeft = camera.position.x - halfW + 5;
        float hudTop = camera.position.y + halfH - 5;
        float hudRight = camera.position.x + halfW - 5;

        float hpBarW = 60f, hpBarH = 5f;
        float hpBarX = hudLeft;
        float hpBarY = hudTop - hpBarH;
        float healthRatio = player.getHealth() / player.getMaxHealth();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.setColor(0.3f, 0.05f, 0.05f, 0.9f);
        shapeRenderer.rect(hpBarX, hpBarY, hpBarW, hpBarH);
        shapeRenderer.setColor(1f - healthRatio, healthRatio * 0.85f, 0.05f, 1f);
        shapeRenderer.rect(hpBarX, hpBarY, hpBarW * healthRatio, hpBarH);
        shapeRenderer.end();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (Entity e : entities) {
            e.draw(game.batch);
        }

        hudFont.setColor(Color.WHITE);
        hudFont.draw(game.batch, (int) player.getHealth() + "/" + (int) player.getMaxHealth(),
            hpBarX, hpBarY - 2);

        hudFont.setColor(Color.GOLD);
        hudFont.draw(game.batch, "Gold: " + player.getInventory().getGold(),
            hpBarX, hpBarY - 14);

        float btnScale = 0.1f;
        float btnW = pauseBtnTexture.getWidth() * btnScale;
        float btnH = pauseBtnTexture.getHeight() * btnScale;
        float btnX = hudRight - btnW;
        float btnY = hudTop - btnH;
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
            PlayerData data = PlayerData.fromPlayer(saveName, next.getNumber(), player);
            data.currentHealth = 200;
            data.playerX = 300;
            data.playerY = 350;
            saveToFirebase(data);
            screenManager.showScreen(Screens.PLAY, data);
        }
    }

    private void saveToFirebase(PlayerData data) {
        if (data.saveName == null || data.saveName.isEmpty()) return;
        String url = "https://lord-of-the-dices-default-rtdb.europe-west1.firebasedatabase.app/saves/" + data.saveName + ".json";

        com.badlogic.gdx.Net.HttpRequest req = new com.badlogic.gdx.Net.HttpRequest(com.badlogic.gdx.Net.HttpMethods.PUT);
        req.setUrl(url);
        req.setHeader("Content-Type", "application/json");
        req.setContent(data.toJson());
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
