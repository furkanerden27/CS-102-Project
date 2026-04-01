package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class StoryEndScreen implements Screen {

    private static final float VIRTUAL_WIDTH  = 1366f;
    private static final float VIRTUAL_HEIGHT = 768f;

    private final Core game;
    private final ScreenManager screenManager;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;

    private int slideCount;
    private TextureRegion regionPrev, regionNext, regionEnd;
    private Sound hoverSound;

    private Rectangle btnPrev, btnNext;
    private int currentSlide = 0;
    private int hoveredBtn = -1;
    private int lastHoveredBtn = -1;

    public StoryEndScreen(Core game, ScreenManager screenManager) {
        this.game = game;
        this.screenManager = screenManager;
    }

    @Override
    public void show(){
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        Assets assets = game.getAssets();
        slideCount = assets.getStoryEndCount();

        TextureAtlas btnAtlas = assets.getAtlas(Assets.ATLAS_STORY_BUTTONS);
        regionNext = btnAtlas.findRegion("Story_Next_Button");
        regionEnd = btnAtlas.findRegion("Story_End_button");
        regionPrev = btnAtlas.findRegion("Story_Previous_Button");
        hoverSound = assets.getSound(Assets.SFX_HOVER);

        layoutButtons();
    }

    private void layoutButtons() {
        float scale = 0.45f;
        float btnW = regionPrev.getRegionWidth() * scale;
        float btnH = regionPrev.getRegionHeight() * scale;
        float margin = 40f;
        float btnY = margin;

        btnPrev = new Rectangle(margin, btnY, btnW, btnH);
        btnNext = new Rectangle(VIRTUAL_WIDTH - btnW - margin, btnY, btnW, btnH);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Texture slide = game.getAssets().getStoryEndSlide(currentSlide);
        batch.draw(slide, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        if(currentSlide > 0){
            drawButton(btnPrev, regionPrev, hoveredBtn == 0);
        }

        if(currentSlide < slideCount - 1) {
            drawButton(btnNext, regionNext, hoveredBtn == 1);
        }
        else{
            drawButton(btnNext, regionEnd, hoveredBtn == 1);
        }

        batch.end();
    }

    private void drawButton(Rectangle rect, TextureRegion region, boolean hovered){
        float scale = hovered ? 1.05f : 1f;
        float width = rect.width * scale;
        float height = rect.height * scale;
        float x = rect.x - (width - rect.width) / 2f;
        float y = rect.y - (height - rect.height) / 2f;
        batch.draw(region, x, y, width, height);
    }

    private void update(float delta) {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        hoveredBtn = -1;
        if (currentSlide > 0 && btnPrev.contains(mouse.x, mouse.y))
            hoveredBtn = 0;
        else if (btnNext.contains(mouse.x, mouse.y))
            hoveredBtn = 1;

        if (hoveredBtn != -1 && hoveredBtn != lastHoveredBtn) {
            game.getAudioManager().playSfx(hoverSound);
        }
        lastHoveredBtn = hoveredBtn;

        if (Gdx.input.justTouched()) {
            if (hoveredBtn == 0) {
                currentSlide--;
            }
            else if(hoveredBtn == 1){
                if(currentSlide < slideCount - 1){
                    currentSlide++;
                }
                else{
                    screenManager.showScreen(Screens.MAIN_MENU);
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (currentSlide < slideCount - 1) currentSlide++;
            else screenManager.showScreen(Screens.MAIN_MENU);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && currentSlide > 0) {
            currentSlide--;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenManager.showScreen(Screens.MAIN_MENU);
        }
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
    }
}
