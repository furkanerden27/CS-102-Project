package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;

public class LoadGameScreen implements Screen {

    private static final float VIRTUAL_WIDTH  = 1366f;
    private static final float VIRTUAL_HEIGHT = 768f;

    private static final String FIREBASE_URL =
        "https://lord-of-the-dices-default-rtdb.europe-west1.firebasedatabase.app/saves.json";

    private static final Color COL_PANEL_BG   = new Color(0.08f, 0.06f, 0.12f, 0.85f);
    private static final Color COL_ROW_EVEN   = new Color(0.14f, 0.11f, 0.20f, 0.90f);
    private static final Color COL_ROW_ODD    = new Color(0.10f, 0.08f, 0.16f, 0.90f);
    private static final Color COL_ROW_HOVER  = new Color(0.85f, 0.38f, 0.05f, 0.45f);
    private static final Color COL_ROW_SEL    = new Color(0.85f, 0.38f, 0.05f, 0.70f);
    private static final Color COL_HEADER     = new Color(0.98f, 0.90f, 0.50f, 1f);
    private static final Color COL_TEXT       = new Color(0.90f, 0.88f, 0.82f, 1f);
    private static final Color COL_BUTTON_TOP = new Color(0.85f, 0.38f, 0.05f, 1f);
    private static final Color COL_BUTTON_BOT = new Color(0.50f, 0.16f, 0.02f, 1f);
    private static final Color COL_BTN_CAN_TOP = new Color(0.60f, 0.60f, 0.63f, 1f);
    private static final Color COL_BTN_CAN_BOT = new Color(0.28f, 0.28f, 0.30f, 1f);
    private static final Color COL_TEXT_BTN   = new Color(0.98f, 0.90f, 0.50f, 1f);


    //LAYOUT FİNAL VALUES   
    private static final float PANEL_W = 900f;
    private static final float PANEL_H = 560f;
    private static final float ROW_H   = 55f;
    private static final float HEADER_H = 45f;

    //Bases for screen
    private final Core game;
    private final ScreenManager screenManager;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer sr;
    private Texture bgTexture;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    private GlyphLayout layout;
    private Sound hoverSound;

    private float panelX, panelY;
    private float tableX, tableY, tableW, tableH;

    private Rectangle btnLoad, btnDelete, btnBack;
    private int hoveredBtn = -1;
    private int lastHoveredBtn = -1;

    private List<PlayerData> saves = new ArrayList<>();
    private boolean loading = true;
    private String errorMsg = null;

    private float scrollOffset = 0f;
    private float pendingScroll = 0f;
    private int selectedIndex = -1;
    private int hoveredRow = -1;

    public LoadGameScreen(Core game, ScreenManager screenManager) {
        this.game = game;
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        //initials
        camera   = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        batch    = new SpriteBatch();
        sr       = new ShapeRenderer();
        layout   = new GlyphLayout();

        Assets assets = game.getAssets();
        bgTexture  = assets.getTexture(Assets.BG_MAIN_MENU);
        hoverSound = assets.getSound(Assets.SFX_HOVER);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        titleFont.setColor(COL_HEADER);

        bodyFont = new BitmapFont();
        bodyFont.getData().setScale(1.6f);

        panelX = (VIRTUAL_WIDTH - PANEL_W) / 2f;
        panelY = (VIRTUAL_HEIGHT - PANEL_H) / 2f;

        tableX = panelX + 20f;
        tableY = panelY + 70f;
        tableW = PANEL_W - 40f;
        tableH = PANEL_H - 150f;

        float btnW = 150f, btnH = 42f;
        float btnY = panelY + 15f;
        float cx = VIRTUAL_WIDTH / 2f;
        btnLoad   = new Rectangle(cx - btnW * 1.5f - 16f, btnY, btnW, btnH);
        btnDelete = new Rectangle(cx - btnW / 2f,         btnY, btnW, btnH);
        btnBack   = new Rectangle(cx + btnW * 0.5f + 16f, btnY, btnW, btnH);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                pendingScroll += amountY;
                return true;
            }
        });

        fetchSaves();
    }
    //get the saves
    private void fetchSaves() {
        loading = true;
        errorMsg = null;

        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(FIREBASE_URL);

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse response) {
                int status = response.getStatus().getStatusCode();
                String body = response.getResultAsString();

                if (status == 200 && body != null && !body.equals("null")) {
                    final ArrayList<PlayerData> parsed = PlayerData.fromJsonAll(body);
                    Gdx.app.postRunnable(() -> {
                        saves = parsed;
                        loading = false;
                    });
                } else {
                    Gdx.app.postRunnable(() -> {
                        saves.clear();
                        loading = false;
                        if (status != 200) errorMsg = "Error: HTTP " + status;
                    });
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    loading = false;
                    errorMsg = "Error: No connection";
                });
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> loading = false);
            }
        });
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bgTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(COL_PANEL_BG);
        sr.rect(panelX, panelY, PANEL_W, PANEL_H);
        sr.end();

        drawTableRows();

        drawShapeButton(btnLoad,   hoveredBtn == 0, COL_BUTTON_TOP, COL_BUTTON_BOT);
        drawShapeButton(btnDelete, hoveredBtn == 1, COL_BUTTON_TOP, COL_BUTTON_BOT);
        drawShapeButton(btnBack,   hoveredBtn == 2, COL_BTN_CAN_TOP, COL_BTN_CAN_BOT);

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawTexts();
        batch.end();
    }
    //handle rows
    private void drawTableRows() {
        Rectangle scissors = new Rectangle();
        Rectangle clip = new Rectangle(tableX, tableY, tableW, tableH);
        com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.calculateScissors(
            camera, viewport.getScreenX(), viewport.getScreenY(),
            viewport.getScreenWidth(), viewport.getScreenHeight(),
            batch.getTransformMatrix(), clip, scissors);

        if (com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.pushScissors(scissors)) {
            sr.setProjectionMatrix(camera.combined);

            float headerY = tableY + tableH - HEADER_H;
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(0.20f, 0.15f, 0.28f, 0.95f);
            sr.rect(tableX, headerY, tableW, HEADER_H);
            sr.end();

            float dataTop = headerY;
            int visibleCount = saves.size();
            for (int i = 0; i < visibleCount; i++) {
                float rowY = dataTop - (i + 1) * ROW_H + scrollOffset;

                if (rowY + ROW_H < tableY || rowY > dataTop) continue;

                sr.begin(ShapeRenderer.ShapeType.Filled);
                if (i == selectedIndex) {
                    sr.setColor(COL_ROW_SEL);
                } else if (i == hoveredRow) {
                    sr.setColor(COL_ROW_HOVER);
                } else {
                    sr.setColor(i % 2 == 0 ? COL_ROW_EVEN : COL_ROW_ODD);
                }
                sr.rect(tableX, rowY, tableW, ROW_H);
                sr.end();
            }

            com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.popScissors();
        }
    }
    //it handles the load screens texts
    private void drawTexts() {
        float cx = VIRTUAL_WIDTH / 2f;
        titleFont.setColor(COL_HEADER);
        layout.setText(titleFont, "Load Game");
        titleFont.draw(batch, "Load Game", cx - layout.width / 2f, panelY + PANEL_H - 15f);

        float hY = tableY + tableH - HEADER_H;
        float columnName = tableX + 20f;
        float columnLevel = tableX + tableW * 0.45f;
        float columnHP = tableX + tableW * 0.60f;
        float columnGold = tableX + tableW * 0.75f;

        bodyFont.setColor(COL_HEADER);
        bodyFont.draw(batch, "Save Name", columnName,hY + HEADER_H * 0.65f);
        bodyFont.draw(batch, "Level", columnLevel, hY + HEADER_H * 0.65f);
        bodyFont.draw(batch, "HP", columnHP,hY + HEADER_H * 0.65f);
        bodyFont.draw(batch, "Gold", columnGold, hY + HEADER_H * 0.65f);

        if (loading) {
            bodyFont.setColor(COL_TEXT);
            layout.setText(bodyFont, "Loading...");
            bodyFont.draw(batch, "Loading...", cx - layout.width / 2f, tableY + tableH / 2f);
        }
        else if (errorMsg != null){
            bodyFont.setColor(Color.RED);
            layout.setText(bodyFont, errorMsg);
            bodyFont.draw(batch, errorMsg, cx - layout.width / 2f, tableY + tableH / 2f);
        }
        else if (saves.isEmpty()){
            bodyFont.setColor(COL_TEXT);
            String msg = "No saved games found.";
            layout.setText(bodyFont, msg);
            bodyFont.draw(batch, msg, cx - layout.width / 2f, tableY + tableH / 2f);
        }
        else {
            float dataTopFir = hY;
            Rectangle scissors = new Rectangle();
            Rectangle clip = new Rectangle(tableX, tableY, tableW, tableH - HEADER_H);
            com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.calculateScissors(
                camera, viewport.getScreenX(), viewport.getScreenY(),
                viewport.getScreenWidth(), viewport.getScreenHeight(),
                batch.getTransformMatrix(), clip, scissors);

            batch.flush();
            if (com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.pushScissors(scissors)) {
                for (int i = 0; i < saves.size(); i++) {
                    float rowY = dataTopFir - (i + 1) * ROW_H + scrollOffset;
                    if (rowY + ROW_H < tableY || rowY > dataTopFir) continue;

                    PlayerData e = saves.get(i);
                    float textY = rowY + ROW_H * 0.62f;

                    bodyFont.setColor(i == selectedIndex ? COL_HEADER : COL_TEXT);
                    bodyFont.draw(batch, e.saveName,columnName,textY);
                    bodyFont.draw(batch, String.valueOf(e.currentLevel), columnLevel, textY);
                    bodyFont.draw(batch, String.valueOf((int) e.currentHealth), columnHP, textY);
                    bodyFont.draw(batch, String.valueOf(e.currentMoney),columnGold,textY);
                }
                batch.flush();
                com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.popScissors();
            }
        }

        bodyFont.setColor(COL_TEXT_BTN);
        drawCentered("Load", btnLoad);
        drawCentered("Delete", btnDelete);
        drawCentered("Back", btnBack);

        if (!saves.isEmpty()) {
            float totalContentH = saves.size() * ROW_H;
            float vH = tableH - HEADER_H;
            if (totalContentH > vH) {
                bodyFont.setColor(new Color(1f, 1f, 1f, 0.4f));
                String scrollHint = (int)(scrollOffset / (totalContentH - vH) * 100) + "%";
                layout.setText(bodyFont, scrollHint);
                bodyFont.draw(batch, scrollHint,
                    tableX + tableW - layout.width - 5f,
                    tableY + 18f);
            }
        }
    }

    private void drawCentered(String text, Rectangle rectangle) {
        layout.setText(bodyFont, text);
        bodyFont.draw(batch, text,
            rectangle.x + (rectangle.width - layout.width) / 2f,
            rectangle.y + (rectangle.height + layout.height) / 2f + 2f);
    }

    private void drawShapeButton(Rectangle r, boolean hov, Color topC, Color botC) {
        Color t = hov ? topC.cpy().lerp(Color.WHITE, 0.18f) : topC;
        Color b = hov ? botC.cpy().lerp(Color.WHITE, 0.14f) : botC;

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(r.x, r.y, r.width, r.height, b, b, t, t);
        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(1f, 1f, 1f, hov ? 0.20f : 0.10f);
        sr.rect(r.x + 2, r.y + r.height * 0.55f, r.width - 4, r.height * 0.38f);
        sr.end();
    }

    private void update(float delta) {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        hoveredBtn = -1;
        if (btnLoad.contains(mouse.x, mouse.y)){
            hoveredBtn = 0;
        }
        else if(btnDelete.contains(mouse.x, mouse.y)){
            hoveredBtn = 1;
        }
        else if(btnBack.contains(mouse.x, mouse.y)){
            hoveredBtn = 2;
        }

        if (hoveredBtn != -1 && hoveredBtn != lastHoveredBtn) {
            game.getAudioManager().playSfx(hoverSound);
        }
        lastHoveredBtn = hoveredBtn;

        hoveredRow = -1;
        float headerY = tableY + tableH - HEADER_H;
        float dataTop = headerY;
        if (mouse.x >= tableX && mouse.x <= tableX + tableW
            && mouse.y >= tableY && mouse.y < dataTop) {
            for (int i = 0; i < saves.size(); i++) {
                float rowY = dataTop - (i + 1) * ROW_H + scrollOffset;
                if (mouse.y >= rowY && mouse.y < rowY + ROW_H
                    && rowY + ROW_H > tableY && rowY < dataTop) {
                    hoveredRow = i;
                    break;
                }
            }
        }

        handleScroll();

        if (Gdx.input.justTouched()) {
            if (hoveredRow != -1) {
                selectedIndex = hoveredRow;
            }
            if (hoveredBtn == 0){
                onLoad();
            }
            if (hoveredBtn == 1) onDelete();
            if (hoveredBtn == 2) onBack();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) onBack();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))  onLoad();
        if (Gdx.input.isKeyJustPressed(Input.Keys.DEL) || Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL))
            onDelete();

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && selectedIndex > 0) {
            selectedIndex--;
            ensureVisible(selectedIndex);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && selectedIndex < saves.size() - 1) {
            selectedIndex++;
            ensureVisible(selectedIndex);
        }
    }

    //for scrolling
    private void handleScroll() {
        if (pendingScroll != 0) {
            scrollOffset += pendingScroll * 40f;
            pendingScroll = 0f;
            clampScroll();
        }
    }

    private void clampScroll() {
        float visibleH = tableH - HEADER_H;
        float totalContentH = saves.size() * ROW_H;
        float maxScroll = Math.max(0, totalContentH - visibleH);
        scrollOffset = MathUtils.clamp(scrollOffset, 0, maxScroll);
    }

    private void ensureVisible(int i) {
        float headerY = tableY + tableH - HEADER_H;
        float visibleH = tableH - HEADER_H;
        float rowTop = (i) * ROW_H;
        float rowBot = (i + 1) * ROW_H;
        if(rowBot - scrollOffset > visibleH) {
            scrollOffset = rowBot - visibleH;
        }
        if (rowTop < scrollOffset) {
            scrollOffset = rowTop;
        }
        clampScroll();
    }

    //it loads the save
    private void onLoad() {
        if (selectedIndex < 0 || selectedIndex >= saves.size()) return;
        PlayerData entry = saves.get(selectedIndex);
        screenManager.showScreen(Screens.PLAY, entry);
    }

    //deletes the save
    private void onDelete() {
        if (selectedIndex < 0 || selectedIndex >= saves.size()) return;
        PlayerData entry = saves.get(selectedIndex);
        String url = "https://lord-of-the-dices-default-rtdb.europe-west1.firebasedatabase.app/saves/"
    + entry.saveName + ".json";

        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.DELETE);
        req.setUrl(url);

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse response) {
                Gdx.app.postRunnable(() -> {
                    saves.remove(selectedIndex);
                    if (selectedIndex >= saves.size()) selectedIndex = saves.size() - 1;
                    clampScroll();
                });
            }
            @Override public void failed(Throwable t) {}
            @Override public void cancelled() {}
        });
    }

    private void onBack() {
        screenManager.showScreen(Screens.MAIN_MENU);
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch     != null) batch.dispose();
        if (sr        != null) sr.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bodyFont  != null) bodyFont.dispose();
    }

}
