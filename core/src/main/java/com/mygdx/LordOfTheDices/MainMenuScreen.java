package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {

    private static final float VIRTUAL_WIDTH  = 1366f;
    private static final float VIRTUAL_HEIGHT = 768f;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;

    private Texture backgroundTexture;
    private Sound hoverSound;
    private TextureRegion btnNewNormal, btnLoadNormal, btnOptionsNormal, btnExitNormal;
    private TextureRegion btnNewHover, btnLoadHover, btnOptionsHover, btnExitHover;

    private int hoveredButton = -1;
    private int lastHoveredButton = -1;

    private Rectangle btnNewGame, btnLoadGame, btnOptions, btnExit;
    private Rectangle hitNewGame, hitLoadGame, hitOptions, hitExit;

    private Core game;
    private ScreenManager screenManager;

    public MainMenuScreen(Core game, ScreenManager screenManager){
        this.game = game;
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        batch = new SpriteBatch();

        Assets assets = game.getAssets();

        game.getAudioManager().playMenuMusic();

        TextureAtlas atlas      = assets.getAtlas(Assets.ATLAS_UI_NORMAL);
        TextureAtlas atlasHover = assets.getAtlas(Assets.ATLAS_UI_HOVER);

        // Hover State
        btnNewHover      = atlas.findRegion("Button_New_Game_Normal");
        btnLoadHover     = atlas.findRegion("button_2_normal-removebg-preview");
        btnOptionsHover  = atlas.findRegion("button_3_normal-removebg-preview");
        btnExitHover     = atlas.findRegion("button_4_normal-removebg-preview");

        // Normal State
        btnNewNormal     = atlasHover.findRegion("button_1_hover-removebg-preview");
        btnLoadNormal    = atlasHover.findRegion("button_2_hover-removebg-preview");
        btnOptionsNormal = atlasHover.findRegion("button_3_hover-removebg-preview");
        btnExitNormal    = atlasHover.findRegion("button_4_hover-removebg-preview");

        backgroundTexture = assets.getTexture(Assets.BG_MAIN_MENU);
        hoverSound        = assets.getSound(Assets.SFX_HOVER);

        layoutButtons();
    }

    private void layoutButtons() {
        float scale = 0.58f;
        float newW = btnNewNormal.getRegionWidth() * scale;
        float newH = btnNewNormal.getRegionHeight() * scale;
        float loadW = btnLoadNormal.getRegionWidth() * scale;
        float loadH = btnLoadNormal.getRegionHeight() * scale;
        float optW = btnOptionsNormal.getRegionWidth() * scale;
        float optH = btnOptionsNormal.getRegionHeight() * scale;
        float exitW = btnExitNormal.getRegionWidth() * scale;
        float exitH = btnExitNormal.getRegionHeight() * scale;

        float gap = -10f;
        float startY = 273f;

        btnExit = new Rectangle(VIRTUAL_WIDTH / 2f - exitW / 2f + gap,  startY, exitW, exitH);
        btnOptions = new Rectangle(VIRTUAL_WIDTH / 2f - optW / 2f + gap,   btnExit.y + exitH + gap, optW, optH);
        btnLoadGame = new Rectangle(VIRTUAL_WIDTH / 2f - loadW / 2f + gap,  btnOptions.y + optH + gap, loadW, loadH);
        btnNewGame = new Rectangle(VIRTUAL_WIDTH / 2f - newW / 2f + gap,   btnLoadGame.y + loadH + gap, newW, newH);

        hitExit = bannerHit(btnExit);
        hitOptions = bannerHit(btnOptions);
        hitLoadGame = bannerHit(btnLoadGame);
        hitNewGame = bannerHit(btnNewGame);
    }

    private Rectangle bannerHit(Rectangle draw) {
        float bannerRatio = 0.55f;
        return new Rectangle(draw.x, draw.y, draw.width, draw.height * bannerRatio);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (backgroundTexture != null){
            batch.draw(backgroundTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        drawButton(btnExit, btnExitNormal, btnExitHover, hoveredButton == 3);
        drawButton(btnOptions, btnOptionsNormal, btnOptionsHover, hoveredButton == 2);
        drawButton(btnLoadGame, btnLoadNormal, btnLoadHover, hoveredButton == 1);
        drawButton(btnNewGame, btnNewNormal, btnNewHover, hoveredButton == 0);

        batch.end();
    }

    private void drawButton(Rectangle rect, TextureRegion normal, TextureRegion hover, boolean isHovered) {
        if (isHovered && hover != null) {
            batch.draw(hover, rect.x, rect.y, rect.width, rect.height);
        } else {
            batch.draw(normal, rect.x, rect.y, rect.width, rect.height);
        }
    }

    private void update(float delta) {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);
        hoveredButton = -1;

        if(hitNewGame.contains(mouse.x, mouse.y))
            hoveredButton = 0;
        else if(hitLoadGame.contains(mouse.x, mouse.y))
            hoveredButton = 1;
        else if(hitOptions.contains(mouse.x, mouse.y))
            hoveredButton = 2;
        else if(hitExit.contains(mouse.x, mouse.y))
            hoveredButton = 3;

        if (hoveredButton != -1 && hoveredButton != lastHoveredButton){
            game.getAudioManager().playSfx(hoverSound);
        }

        lastHoveredButton = hoveredButton;

        if (Gdx.input.justTouched()) {
            switch (hoveredButton) {
                case 0: screenManager.showScreen(Screens.NEW_SAVE); break;
                case 1: screenManager.showScreen(Screens.LOAD_SAVE); break;
                case 2: screenManager.showScreen(Screens.OPTIONS); break;
                case 3: Gdx.app.exit(); break;
            }
        }
    }

    @Override public void resize(int w, int h){ viewport.update(w, h, true); }
    @Override public void pause(){}
    @Override public void resume() {}
    @Override public void hide(){}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        // Asset'ler AssetManager tarafından yönetilecek burada dispose etme!
    }
}
