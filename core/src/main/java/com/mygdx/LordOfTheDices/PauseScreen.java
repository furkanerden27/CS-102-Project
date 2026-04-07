package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PauseScreen implements Screen {

    private static final float VIRTUAL_WIDTH  = 1366f;
    private static final float VIRTUAL_HEIGHT = 768f;

    private final Core game;
    private final ScreenManager screenManager;
    private final Screen playScreen;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private GlyphLayout layout;

    private Texture overlayTexture;

    private TextureRegion resumeRegion;
    private TextureRegion optionsRegion;
    private TextureRegion returnRegion;

    private static final float BTN_SCALE = 0.35f;
    private static final float BTN_GAP   = 15f;

    private Rectangle[] buttonRects;
    private int hoveredButton = -1;
    private int lastHoveredButton = -1;

    public PauseScreen(Core game, ScreenManager screenManager, Screen playScreen) {
        this.game = game;
        this.screenManager = screenManager;
        this.playScreen = playScreen;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        batch = new SpriteBatch();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        titleFont.setColor(Color.GOLD);

        layout = new GlyphLayout();

        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(0, 0, 0, 0.7f);
        overlayPixmap.fill();
        overlayTexture = new Texture(overlayPixmap);
        overlayPixmap.dispose();

        TextureAtlas atlas = game.getAssets().getAtlas(Assets.ATLAS_PAUSE_BUTTONS);
        resumeRegion  = atlas.findRegion("resume_btn");
        optionsRegion = atlas.findRegion("pause_options_btn");
        returnRegion  = atlas.findRegion("return_to_main_menu_btn");

        TextureRegion[] regions = {resumeRegion, optionsRegion, returnRegion};
        buttonRects = new Rectangle[3];

        float totalHeight = 0;
        for (TextureRegion r : regions) {
            totalHeight += r.getRegionHeight() * BTN_SCALE;
        }
        totalHeight += (regions.length - 1) * BTN_GAP;

        float startY = (VIRTUAL_HEIGHT + totalHeight) / 2f;

        for (int i = 0; i < regions.length; i++) {
            float btnW = regions[i].getRegionWidth() * BTN_SCALE;
            float btnH = regions[i].getRegionHeight() * BTN_SCALE;
            float x = (VIRTUAL_WIDTH - btnW) / 2f;
            startY -= btnH;
            buttonRects[i] = new Rectangle(x, startY, btnW, btnH);
            startY -= BTN_GAP;
        }
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(overlayTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        layout.setText(titleFont, "PAUSED");
        float titleX = (VIRTUAL_WIDTH - layout.width) / 2f;
        float titleY = buttonRects[0].y + buttonRects[0].height + 80f;
        titleFont.draw(batch, layout, titleX, titleY);

        TextureRegion[] regions = {resumeRegion, optionsRegion, returnRegion};
        for (int i = 0; i < regions.length; i++) {
            Rectangle r = buttonRects[i];
            if (hoveredButton == i) {
                batch.setColor(1f, 1f, 1f, 1f);
            } else {
                batch.setColor(0.8f, 0.8f, 0.8f, 1f);
            }
            batch.draw(regions[i], r.x, r.y, r.width, r.height);
        }
        batch.setColor(1f, 1f, 1f, 1f);

        batch.end();
    }

    private void handleInput() {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);
        hoveredButton = -1;

        for (int i = 0; i < buttonRects.length; i++) {
            if (buttonRects[i].contains(mouse.x, mouse.y)) {
                hoveredButton = i;
                break;
            }
        }

        if (hoveredButton != -1 && hoveredButton != lastHoveredButton) {
            game.getAudioManager().playSfx(game.getAssets().getSound(Assets.SFX_HOVER));
        }
        lastHoveredButton = hoveredButton;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            resumeGame();
            return;
        }

        if (Gdx.input.justTouched() && hoveredButton != -1) {
            switch (hoveredButton) {
                case 0:
                    resumeGame();
                    break;
                case 1:
                    screenManager.showScreen(Screens.OPTIONS);
                    break;
                case 2:
                    saveGame();
                    playScreen.dispose();
                    screenManager.showScreen(Screens.MAIN_MENU);
                    break;
            }
        }
    }

    private void resumeGame() {
        game.setScreen(playScreen);
    }

    private void saveGame() {
        if (!(playScreen instanceof PlayScreen)) return;
        PlayScreen ps = (PlayScreen) playScreen;
        String saveName = ps.getSaveName();
        if (saveName == null || saveName.isEmpty()) return;

        PlayerData data = PlayerData.fromPlayScreen(saveName, ps.getLevel().getNumber(),
            ps.getPlayerHealth(), ps.getPlayerX(), ps.getPlayerY(), ps.getPlayerInventory());
        System.out.println("[SAVE] Gold: " + data.currentMoney + " HP: " + data.currentHealth + " JSON: " + data.toJson());
        String url = "https://lord-of-the-dices-default-rtdb.europe-west1.firebasedatabase.app/saves/" + saveName + ".json";

        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.PUT);
        req.setUrl(url);
        req.setHeader("Content-Type", "application/json");
        req.setContent(data.toJson());

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse response) {}
            @Override
            public void failed(Throwable t) {}
            @Override
            public void cancelled() {}
        });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (overlayTexture != null) overlayTexture.dispose();
    }
}
