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

    // Renk paleti
    private static final Color COL_PANEL_BG   = new Color(0.08f, 0.06f, 0.12f, 0.85f);
    private static final Color COL_ROW_EVEN   = new Color(0.14f, 0.11f, 0.20f, 0.90f);
    private static final Color COL_ROW_ODD    = new Color(0.10f, 0.08f, 0.16f, 0.90f);
    private static final Color COL_ROW_HOVER  = new Color(0.85f, 0.38f, 0.05f, 0.45f);
    private static final Color COL_ROW_SEL    = new Color(0.85f, 0.38f, 0.05f, 0.70f);
    private static final Color COL_HEADER     = new Color(0.98f, 0.90f, 0.50f, 1f);
    private static final Color COL_TEXT       = new Color(0.90f, 0.88f, 0.82f, 1f);
    private static final Color COL_BTN_TOP    = new Color(0.85f, 0.38f, 0.05f, 1f);
    private static final Color COL_BTN_BOT    = new Color(0.50f, 0.16f, 0.02f, 1f);
    private static final Color COL_BTN_CAN_TOP = new Color(0.60f, 0.60f, 0.63f, 1f);
    private static final Color COL_BTN_CAN_BOT = new Color(0.28f, 0.28f, 0.30f, 1f);
    private static final Color COL_TEXT_BTN   = new Color(0.98f, 0.90f, 0.50f, 1f);

    // Panel boyutları
    private static final float PANEL_W = 900f;
    private static final float PANEL_H = 560f;
    private static final float ROW_H   = 55f;
    private static final float HEADER_H = 45f;

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

    // Panel pozisyonu
    private float panelX, panelY;
    private float tableX, tableY, tableW, tableH;

    // Butonlar
    private Rectangle btnLoad, btnDelete, btnBack;
    private int hoveredBtn = -1;
    private int lastHoveredBtn = -1;

    // Veri
    private List<SaveEntry> saves = new ArrayList<>();
    private boolean loading = true;
    private String errorMsg = null;

    // Scroll & seçim
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

        // Tablo alanı (panel içinde, başlık ve butonlar hariç)
        tableX = panelX + 20f;
        tableY = panelY + 70f;  // butonların üstü
        tableW = PANEL_W - 40f;
        tableH = PANEL_H - 150f; // başlık + buton alanı çıkartılmış

        // Butonlar
        float btnW = 150f, btnH = 42f;
        float btnY = panelY + 15f;
        float cx = VIRTUAL_WIDTH / 2f;
        btnLoad   = new Rectangle(cx - btnW * 1.5f - 16f, btnY, btnW, btnH);
        btnDelete = new Rectangle(cx - btnW / 2f,         btnY, btnW, btnH);
        btnBack   = new Rectangle(cx + btnW * 0.5f + 16f, btnY, btnW, btnH);

        // Scroll input
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                pendingScroll += amountY;
                return true;
            }
        });

        fetchSaves();
    }

    // ── Firebase'den save'leri çek ───────────────────────────────────────────
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
                    final List<SaveEntry> parsed = parseJson(body);
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

    // ── Basit JSON parser (Firebase response) ────────────────────────────────
    // Format: {"key1":{...},"key2":{...}}
    private List<SaveEntry> parseJson(String json) {
        List<SaveEntry> list = new ArrayList<>();
        if (json == null || json.length() < 2) return list;

        // Dış süslü parantezleri kaldır
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        // Her bir save objesini bul
        int i = 0;
        while (i < json.length()) {
            // Key'i bul: "saveName":
            int keyStart = json.indexOf('"', i);
            if (keyStart == -1) break;
            int keyEnd = json.indexOf('"', keyStart + 1);
            if (keyEnd == -1) break;

            // Value objesini bul: {...}
            int objStart = json.indexOf('{', keyEnd);
            if (objStart == -1) break;
            int objEnd = json.indexOf('}', objStart);
            if (objEnd == -1) break;

            String obj = json.substring(objStart, objEnd + 1);
            SaveEntry entry = parseSaveEntry(obj);
            if (entry != null) list.add(entry);

            i = objEnd + 1;
        }

        return list;
    }

    private SaveEntry parseSaveEntry(String obj) {
        SaveEntry e = new SaveEntry();
        e.saveName      = extractString(obj, "saveName");
        e.currentLevel  = extractInt(obj, "currentLevel");
        e.currentHealth = extractInt(obj, "currentHealth");
        e.currentMoney  = extractInt(obj, "currentMoney");
        e.timestamp     = extractLong(obj, "timestamp");
        if (e.saveName == null || e.saveName.isEmpty()) return null;
        return e;
    }

    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf('"', start);
        return end == -1 ? "" : json.substring(start, end);
    }

    private int extractInt(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-'))
            end++;
        try { return Integer.parseInt(json.substring(start, end)); }
        catch (NumberFormatException ex) { return 0; }
    }

    private long extractLong(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-'))
            end++;
        try { return Long.parseLong(json.substring(start, end)); }
        catch (NumberFormatException ex) { return 0; }
    }

    // ── render ───────────────────────────────────────────────────────────────
    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        // 1. Arka plan
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bgTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.end();

        // 2. Panel arka planı
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(COL_PANEL_BG);
        sr.rect(panelX, panelY, PANEL_W, PANEL_H);
        sr.end();

        // 3. Tablo satırları (scissor ile kırpılmış)
        drawTableRows();

        // 4. Butonlar
        drawShapeButton(btnLoad,   hoveredBtn == 0, COL_BTN_TOP, COL_BTN_BOT);
        drawShapeButton(btnDelete, hoveredBtn == 1, COL_BTN_TOP, COL_BTN_BOT);
        drawShapeButton(btnBack,   hoveredBtn == 2, COL_BTN_CAN_TOP, COL_BTN_CAN_BOT);

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 5. Metin
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawTexts();
        batch.end();
    }

    // ── Tablo satırlarını çiz ────────────────────────────────────────────────
    private void drawTableRows() {
        // Scissor: sadece tablo alanında çiz
        Rectangle scissors = new Rectangle();
        Rectangle clip = new Rectangle(tableX, tableY, tableW, tableH);

        // Viewport-based scissor hesapla
        com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.calculateScissors(
            camera, viewport.getScreenX(), viewport.getScreenY(),
            viewport.getScreenWidth(), viewport.getScreenHeight(),
            batch.getTransformMatrix(), clip, scissors);

        if (com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.pushScissors(scissors)) {
            sr.setProjectionMatrix(camera.combined);

            // Header satırı
            float headerY = tableY + tableH - HEADER_H;
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(0.20f, 0.15f, 0.28f, 0.95f);
            sr.rect(tableX, headerY, tableW, HEADER_H);
            sr.end();

            // Veri satırları
            float dataTop = headerY;
            int visibleCount = saves.size();
            for (int i = 0; i < visibleCount; i++) {
                float rowY = dataTop - (i + 1) * ROW_H + scrollOffset;

                // Görünür mü?
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

    // ── Metin çizimleri ──────────────────────────────────────────────────────
    private void drawTexts() {
        float cx = VIRTUAL_WIDTH / 2f;

        // Başlık
        titleFont.setColor(COL_HEADER);
        layout.setText(titleFont, "Load Game");
        titleFont.draw(batch, "Load Game", cx - layout.width / 2f, panelY + PANEL_H - 15f);

        // Tablo header metinleri
        float headerY = tableY + tableH - HEADER_H;
        float colName  = tableX + 20f;
        float colLevel = tableX + tableW * 0.45f;
        float colHP    = tableX + tableW * 0.60f;
        float colGold  = tableX + tableW * 0.75f;

        bodyFont.setColor(COL_HEADER);
        bodyFont.draw(batch, "Save Name",  colName,  headerY + HEADER_H * 0.65f);
        bodyFont.draw(batch, "Level",      colLevel, headerY + HEADER_H * 0.65f);
        bodyFont.draw(batch, "HP",         colHP,    headerY + HEADER_H * 0.65f);
        bodyFont.draw(batch, "Gold",       colGold,  headerY + HEADER_H * 0.65f);

        // Loading / Error / Empty mesajları
        if (loading) {
            bodyFont.setColor(COL_TEXT);
            layout.setText(bodyFont, "Loading...");
            bodyFont.draw(batch, "Loading...", cx - layout.width / 2f, tableY + tableH / 2f);
        } else if (errorMsg != null) {
            bodyFont.setColor(Color.RED);
            layout.setText(bodyFont, errorMsg);
            bodyFont.draw(batch, errorMsg, cx - layout.width / 2f, tableY + tableH / 2f);
        } else if (saves.isEmpty()) {
            bodyFont.setColor(COL_TEXT);
            String msg = "No saved games found.";
            layout.setText(bodyFont, msg);
            bodyFont.draw(batch, msg, cx - layout.width / 2f, tableY + tableH / 2f);
        } else {
            // Veri satır metinleri (scissor bölgesinde)
            float dataTop = headerY;
            // Scissor tekrar uygula (metin için)
            Rectangle scissors = new Rectangle();
            Rectangle clip = new Rectangle(tableX, tableY, tableW, tableH - HEADER_H);
            com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.calculateScissors(
                camera, viewport.getScreenX(), viewport.getScreenY(),
                viewport.getScreenWidth(), viewport.getScreenHeight(),
                batch.getTransformMatrix(), clip, scissors);

            batch.flush();
            if (com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.pushScissors(scissors)) {
                for (int i = 0; i < saves.size(); i++) {
                    float rowY = dataTop - (i + 1) * ROW_H + scrollOffset;
                    if (rowY + ROW_H < tableY || rowY > dataTop) continue;

                    SaveEntry e = saves.get(i);
                    float textY = rowY + ROW_H * 0.62f;

                    bodyFont.setColor(i == selectedIndex ? COL_HEADER : COL_TEXT);
                    bodyFont.draw(batch, e.saveName,                    colName,  textY);
                    bodyFont.draw(batch, String.valueOf(e.currentLevel),  colLevel, textY);
                    bodyFont.draw(batch, String.valueOf(e.currentHealth), colHP,    textY);
                    bodyFont.draw(batch, String.valueOf(e.currentMoney),  colGold,  textY);
                }
                batch.flush();
                com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.popScissors();
            }
        }

        // Buton metinleri
        bodyFont.setColor(COL_TEXT_BTN);
        drawCentered("Load",   btnLoad);
        drawCentered("Delete", btnDelete);
        drawCentered("Back",   btnBack);

        // Scrollbar göstergesi
        if (!saves.isEmpty()) {
            float totalContentH = saves.size() * ROW_H;
            float visibleH = tableH - HEADER_H;
            if (totalContentH > visibleH) {
                bodyFont.setColor(new Color(1f, 1f, 1f, 0.4f));
                String scrollHint = (int)(scrollOffset / (totalContentH - visibleH) * 100) + "%";
                layout.setText(bodyFont, scrollHint);
                bodyFont.draw(batch, scrollHint,
                    tableX + tableW - layout.width - 5f,
                    tableY + 18f);
            }
        }
    }

    private void drawCentered(String text, Rectangle r) {
        layout.setText(bodyFont, text);
        bodyFont.draw(batch, text,
            r.x + (r.width - layout.width) / 2f,
            r.y + (r.height + layout.height) / 2f + 2f);
    }

    // ── Buton çizimi ─────────────────────────────────────────────────────────
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

    // ── update ───────────────────────────────────────────────────────────────
    private void update(float delta) {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        // Buton hover
        hoveredBtn = -1;
        if      (btnLoad.contains(mouse.x, mouse.y))   hoveredBtn = 0;
        else if (btnDelete.contains(mouse.x, mouse.y)) hoveredBtn = 1;
        else if (btnBack.contains(mouse.x, mouse.y))   hoveredBtn = 2;

        if (hoveredBtn != -1 && hoveredBtn != lastHoveredBtn) {
            game.getAudioManager().playSfx(hoverSound);
        }
        lastHoveredBtn = hoveredBtn;

        // Satır hover
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

        // Scroll (mouse wheel)
        handleScroll();

        // Tıklama
        if (Gdx.input.justTouched()) {
            if (hoveredRow != -1) {
                selectedIndex = hoveredRow;
            }
            if (hoveredBtn == 0) onLoad();
            if (hoveredBtn == 1) onDelete();
            if (hoveredBtn == 2) onBack();
        }

        // Klavye
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

    private void ensureVisible(int index) {
        float headerY = tableY + tableH - HEADER_H;
        float visibleH = tableH - HEADER_H;
        float rowTop = (index) * ROW_H;
        float rowBot = (index + 1) * ROW_H;

        if (rowBot - scrollOffset > visibleH) {
            scrollOffset = rowBot - visibleH;
        }
        if (rowTop < scrollOffset) {
            scrollOffset = rowTop;
        }
        clampScroll();
    }

    // ── Aksiyonlar ───────────────────────────────────────────────────────────
    private void onLoad() {
        if (selectedIndex < 0 || selectedIndex >= saves.size()) return;
        SaveEntry entry = saves.get(selectedIndex);
        // TODO: Oyunu yükle — şimdilik story begin'e gönder
        screenManager.showScreen(Screens.STORY_BEGIN);
    }

    private void onDelete() {
        if (selectedIndex < 0 || selectedIndex >= saves.size()) return;
        SaveEntry entry = saves.get(selectedIndex);
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

    // ── Screen lifecycle ─────────────────────────────────────────────────────
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
        // bgTexture ve hoverSound AssetManager tarafından yönetilir
    }

    // ── Veri modeli ──────────────────────────────────────────────────────────
    private static class SaveEntry {
        String saveName = "";
        int currentLevel;
        int currentHealth;
        int currentMoney;
        long timestamp;
    }
}
