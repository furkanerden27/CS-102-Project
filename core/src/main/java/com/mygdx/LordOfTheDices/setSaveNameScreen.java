package com.mygdx.LordOfTheDices;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class setSaveNameScreen implements Screen {

    private static final float VIRTUAL_WIDTH  = 1366f;
    private static final float VIRTUAL_HEIGHT = 768f;
    private static final int   MAX = 20;

    private static final Color COL_BTN_CNF_TOP = new Color(0.85f, 0.38f, 0.05f, 1f);
    private static final Color COL_BTN_CNF_BOT = new Color(0.50f, 0.16f, 0.02f, 1f);
    private static final Color COL_BTN_CAN_TOP = new Color(0.60f, 0.60f, 0.63f, 1f);
    private static final Color COL_BTN_CAN_BOT = new Color(0.28f, 0.28f, 0.30f, 1f);
    private static final Color COL_TEXT_BTN    = new Color(0.98f, 0.90f, 0.50f, 1f);

    private Texture       bgTexture;
    private TextureRegion regionPanel;
    private TextureRegion regionInput;
    private Sound         hoverSound;

    private BitmapFont  bodyFont;
    private GlyphLayout layout = new GlyphLayout();

    private float PANEL_W, PANEL_H;
    private float panelX,  panelY;

    private Rectangle inputBox;
    private Rectangle btnConfirm;
    private Rectangle btnCancel;

    private StringBuilder saveName       = new StringBuilder();
    private boolean       inputFocused   = true;
    private float         cursorTimer    = 0f;
    private boolean       showCursor     = true;
    private int           hoveredBtn     = -1;
    private int           lastHoveredBtn = -1;
    private String        statusMsg      = "";
    private float         statusTimer    = 0f;

    private int currentLevel  = 1;
    private int currentHealth = 100;
    private int currentMoney  = 10;

    private Core          game;
    private ScreenManager screenManager;
    private OrthographicCamera camera;
    private FitViewport        viewport;
    private SpriteBatch        batch;
    private ShapeRenderer      sr;

    public setSaveNameScreen(Core game, ScreenManager screenManager) {
        this.game          = game;
        this.screenManager = screenManager;
    }

    public void setPlayerData(int level, int health, int money) {
        this.currentLevel  = level;
        this.currentHealth = health;
        this.currentMoney  = money;
    }

    @Override
    public void show() {
        camera   = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        batch    = new SpriteBatch();
        sr       = new ShapeRenderer();

        Assets assets = game.getAssets();

        bgTexture = assets.getTexture(Assets.BG_MAIN_MENU);

        TextureAtlas saveNameAtlas = assets.getAtlas(Assets.ATLAS_SAVE_PANEL);
        regionPanel = saveNameAtlas.findRegion("NameYourSavePanelBg");
        regionInput = saveNameAtlas.findRegion("yaziAlaniSon");

        hoverSound = assets.getSound(Assets.SFX_HOVER);

        bodyFont = new BitmapFont();
        bodyFont.getData().setScale(1.9f);

        PANEL_W = regionPanel.getRegionWidth();
        PANEL_H = regionPanel.getRegionHeight();

        layoutUI();
    }

    private void layoutUI() {
        panelX = (VIRTUAL_WIDTH  - PANEL_W) / 2f;
        panelY = (VIRTUAL_HEIGHT - PANEL_H) / 2f;

        float ibW = PANEL_W * 0.80f;
        float ibH = PANEL_H * 0.20f;
        float ibX = panelX + (PANEL_W - ibW) / 2f;
        float ibY = panelY + PANEL_H * 0.28f;

        inputBox = new Rectangle(ibX, ibY + 80f, ibW, ibH);

        float btnW = 160f, btnH = 45f;
        float btnY = panelY + PANEL_H * 0.32f;
        float cx   = VIRTUAL_WIDTH / 2f;
        btnConfirm = new Rectangle(cx - btnW - 12f, btnY, btnW, btnH);
        btnCancel  = new Rectangle(cx + 12f,        btnY, btnW, btnH);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (bgTexture   != null) batch.draw(bgTexture,   0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        if (regionPanel != null) batch.draw(regionPanel, panelX, panelY, PANEL_W, PANEL_H);
        if (regionInput != null) batch.draw(regionInput, inputBox.x, inputBox.y, inputBox.width, inputBox.height);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        drawButton(btnConfirm, hoveredBtn == 0, COL_BTN_CNF_TOP, COL_BTN_CNF_BOT);
        drawButton(btnCancel,  hoveredBtn == 1, COL_BTN_CAN_TOP, COL_BTN_CAN_BOT);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawText();
        batch.end();
    }

    private void drawButton(Rectangle r, boolean hov, Color topC, Color botC) {
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

    private void drawText() {
        float cx = VIRTUAL_WIDTH / 2f;

        String display;
        if (saveName.length() == 0 && !inputFocused) {
            bodyFont.setColor(0.50f, 0.40f, 0.30f, 1f);
            display = "Enter save name...";
        } else {
            bodyFont.setColor(Color.WHITE);
            display = saveName.toString() + (showCursor && inputFocused ? "|" : "");
        }
        layout.setText(bodyFont, display);
        bodyFont.draw(batch, display,
                inputBox.x + 16f,
                inputBox.y + (inputBox.height + layout.height) / 2f + 2f);
        bodyFont.setColor(Color.WHITE);

        bodyFont.setColor(COL_TEXT_BTN);
        drawCentered("Confirm", btnConfirm);
        drawCentered("Cancel",  btnCancel);
        bodyFont.setColor(Color.WHITE);

        if (statusTimer > 0f) {
            bodyFont.setColor(statusMsg.startsWith("Error")
                    ? Color.RED : new Color(0.5f, 1f, 0.5f, 1f));
            layout.setText(bodyFont, statusMsg);
            bodyFont.draw(batch, statusMsg, cx - layout.width / 2f, panelY - 30f);
        }
    }

    private void drawCentered(String text, Rectangle r) {
        layout.setText(bodyFont, text);
        bodyFont.draw(batch, text,
                r.x + (r.width  - layout.width)  / 2f,
                r.y + (r.height + layout.height) / 2f + 2f);
    }

    private void update(float delta) {
        cursorTimer += delta;
        if (cursorTimer >= 0.5f) { cursorTimer = 0; showCursor = !showCursor; }
        if (statusTimer > 0f) statusTimer -= delta;

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        hoveredBtn = -1;
        if      (btnConfirm.contains(mouse.x, mouse.y)) hoveredBtn = 0;
        else if (btnCancel .contains(mouse.x, mouse.y)) hoveredBtn = 1;

        if (hoveredBtn != -1 && hoveredBtn != lastHoveredBtn && hoverSound != null)
            game.getAudioManager().playSfx(hoverSound);
        lastHoveredBtn = hoveredBtn;

        handleKeyboard();

        if (Gdx.input.justTouched()) {
            inputFocused = inputBox.contains(mouse.x, mouse.y);
            if (hoveredBtn == 0) onConfirm();
            if (hoveredBtn == 1) onCancel();
        }
    }

    private void handleKeyboard() {
        if (!inputFocused) return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && saveName.length() > 0)
            saveName.deleteCharAt(saveName.length() - 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))  { onConfirm(); return; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { onCancel();  return; }

        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                     || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        for (int k = Input.Keys.A; k <= Input.Keys.Z; k++) {
            if (Gdx.input.isKeyJustPressed(k) && saveName.length() < MAX) {
                char c = (char) ('a' + (k - Input.Keys.A));
                saveName.append(shift ? Character.toUpperCase(c) : c);
            }
        }
        for (int k = Input.Keys.NUM_0; k <= Input.Keys.NUM_9; k++)
            if (Gdx.input.isKeyJustPressed(k) && saveName.length() < MAX)
                saveName.append((char) ('0' + (k - Input.Keys.NUM_0)));
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS) && saveName.length() < MAX)
            saveName.append(shift ? '_' : '-');
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && saveName.length() < MAX)
            saveName.append(' ');
    }

    private void onConfirm() {
        String name = saveName.toString().trim();
        if (name.isEmpty()) { showStatus("Error: Name cannot be empty!", 2.5f); return; }
        saveToFirebase(name);
    }

    private void onCancel() {
        screenManager.showScreen(Screens.MAIN_MENU);
    }

    private void saveToFirebase(String name) {
        String url = "https://lord-of-the-dices-default-rtdb.europe-west1.firebasedatabase.app/saves/" + name + ".json";

        String json = "{\"saveName\":\"" + name + "\"," +
                      "\"currentLevel\":"  + currentLevel  + "," +
                      "\"currentHealth\":" + currentHealth + "," +
                      "\"currentMoney\":"  + currentMoney  + "," +
                      "\"timestamp\":"     + System.currentTimeMillis() + "}";

        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.PUT);
        req.setUrl(url);
        req.setHeader("Content-Type", "application/json");
        req.setContent(json);

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse r) {
                int status = r.getStatus().getStatusCode();
                if (status == 200) {
                    showStatus("Saved: " + name, 2f);
                    Gdx.app.postRunnable(() ->
                        screenManager.showScreen(Screens.STORY_BEGIN));
                } else {
                    showStatus("Error: " + status, 3f);
                }
            }
            @Override public void failed(Throwable t) { showStatus("Error: No connection", 3f); }
            @Override public void cancelled()          {}
        });
    }

    private void showStatus(String msg, float dur) { statusMsg = msg; statusTimer = dur; }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        if (batch    != null) batch.dispose();
        if (sr       != null) sr.dispose();
        if (bodyFont != null) bodyFont.dispose();
    }

    public static class SaveData {
        public String saveName;
        public int    currentLevel;
        public int    currentHealth;
        public int    currentMoney;
        public long   timestamp;
        public SaveData() {}
        public SaveData(String n, int level, int health, int money) {
            this.saveName      = n;
            this.currentLevel  = level;
            this.currentHealth = health;
            this.currentMoney  = money;
            this.timestamp     = System.currentTimeMillis();
        }
    }
}
