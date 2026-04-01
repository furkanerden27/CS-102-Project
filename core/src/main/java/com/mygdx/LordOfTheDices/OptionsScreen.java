package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class OptionsScreen implements Screen {

    // ── Sabitler ──────────────────────────────────────────────────────────────
    private static final float VIRTUAL_WIDTH  = 1366f;
    private static final float VIRTUAL_HEIGHT = 768f;

    private static final String PREFS_NAME        = "game_settings";
    private static final String PREF_SFX          = "sfx_level";
    private static final String PREF_MUSIC        = "music_level";
    private static final String PREF_FULLSCREEN   = "fullscreen";

    // ── Bağımlılıklar ─────────────────────────────────────────────────────────
    private final Core          game;
    private final ScreenManager screenManager;

    // ── Scene2D & LibGDX Nesneleri ────────────────────────────────────────────
    private Stage        stage;
    private Texture      backgroundTexture;
    private TextureAtlas atlas;

    // Bellek sızıntısını önlemek için oluşturduğumuz geçici texture'ları tutuyoruz
    private final Array<Texture> generatedTextures = new Array<>();

    // ── Pixel-perfect hit detection için return butonu pixmap'i ───────────────
    // show() içinde bir kez hazırlanır, dispose() içinde temizlenir.
    private Pixmap returnBtnPixmap;

    // ── Fullscreen checkbox referansı (F tuşu için) ──────────────────────────
    private ImageButton fullscreenBtn;

    // ── Durum Değişkenleri ────────────────────────────────────────────────────
    private float   sfxLevel;
    private float   musicLevel;
    private boolean fullscreen;

    public OptionsScreen(Core game, ScreenManager screenManager) {
        this.game          = game;
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage); // Inputları Stage'e yönlendiriyoruz

        loadSettings();
        applyAudioLevels();
        
        Assets assets      = game.getAssets();
        backgroundTexture = assets.getTexture(Assets.BG_MAIN_MENU);
        atlas             = assets.getAtlas(Assets.ATLAS_OPTIONS);

        TextureRegion panelRegion             = atlas.findRegion("Options_bg");
        TextureRegion checkboxEmptyRegion     = atlas.findRegion("checkbox_empty");
        TextureRegion checkboxFilledRegion    = atlas.findRegion("checkbox_filled");
        TextureRegion returnButtonRegion      = atlas.findRegion("returnToMenuButton");
        TextureRegion returnButtonHoverRegion = atlas.findRegion("returnToMenuButtonHover");

        // ── Return butonu için pixmap'i bir kez hazırla ───────────────────────
        // consumePixmap() atlas texture verisini tüketir; bu yüzden bunu
        // show() içinde tek seferlik yapıp sınıf alanında saklıyoruz.
        Texture retTex = returnButtonRegion.getTexture();
        if (!retTex.getTextureData().isPrepared()) {
            retTex.getTextureData().prepare();
        }
        returnBtnPixmap = retTex.getTextureData().consumePixmap();
        // ─────────────────────────────────────────────────────────────────────

        // 2. Arka Planı Ekle
        Image bgImage = new Image(backgroundTexture);
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        // 3. Ana Tablo (Paneli ekranın ortasında tutmak için)
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // 4. Panel Tablosu (İçerisine elemanları dizeceğimiz tablo)
        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(panelRegion));

        float panelW = panelRegion.getRegionWidth()  * 1f;
        float panelH = panelRegion.getRegionHeight() * 1f;

        // 5. Stilleri Oluştur (Manuel koordinatlar yerine stiller tanımlanır)

        // Checkbox Stili (ImageButton olarak kullanıyoruz)
        ImageButton.ImageButtonStyle cbStyle = new ImageButton.ImageButtonStyle();
        cbStyle.imageUp      = new TextureRegionDrawable(checkboxEmptyRegion);
        cbStyle.imageChecked = new TextureRegionDrawable(checkboxFilledRegion);

        // Return Butonu Stili (Hover efekti otomatik çalışacak)
        ImageButton.ImageButtonStyle retStyle = new ImageButton.ImageButtonStyle();
        retStyle.up   = new TextureRegionDrawable(returnButtonRegion);
        retStyle.over = new TextureRegionDrawable(returnButtonHoverRegion);

        // Slider Stili (ShapeRenderer ile çizdiğiniz renkleri Scene2D Drawable'a çeviriyoruz)
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = createColorDrawable(new Color(0.25f, 0.15f, 0.08f, 1f), 18); // Arka plan (Koyu kahve)
        sliderStyle.knobBefore = createColorDrawable(new Color(0.85f, 0.65f, 0.1f,  1f), 18); // Dolu kısım (Altın sarısı)
        sliderStyle.knob       = createCircleDrawable((int)(18f * 1.4f), new Color(1f, 0.8f, 0.2f, 1f)); // Topuz

        // 6. UI Elemanlarını Oluştur ve Dinleyicileri (Listeners) Ekle

        fullscreenBtn = new ImageButton(cbStyle);
        fullscreenBtn.setChecked(fullscreen);
        // Listener, setChecked'den SONRA ekleniyor ki ilk set tetiklemesin
        fullscreenBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fullscreen = fullscreenBtn.isChecked();
                if (fullscreen) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode((int) VIRTUAL_WIDTH, (int) VIRTUAL_HEIGHT);
                }
                saveSettings();
            }
        });

        final Slider sfxSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle);
        sfxSlider.setValue(sfxLevel);
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sfxLevel = sfxSlider.getValue();
                applyAudioLevels();
            }
        });

        final Slider musicSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle);
        musicSlider.setValue(musicLevel);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicLevel = musicSlider.getValue();
                applyAudioLevels();
            }
        });

        // ── Pixel-perfect hit detection ile Return Butonu ─────────────────────
        // returnButtonRegion final olarak saklanıyor ki anonymous sınıf içinden
        // erişebilelim.
        final TextureRegion finalReturnRegion = returnButtonRegion;

        ImageButton returnBtn = new ImageButton(retStyle) {
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                // Önce normal sınır (bounding-box) kontrolü; alan dışındaysa zaten null
                if (super.hit(x, y, touchable) == null) return null;

                // Butondaki (x, y) koordinatını texture üzerindeki piksel
                // koordinatına dönüştürüyoruz.
                // Scene2D'nin Y ekseni aşağıdan yukarıya, Pixmap'inki yukarıdan
                // aşağıya olduğu için Y'yi ters çeviriyoruz.
                int texX = (int)(x / getWidth()  * finalReturnRegion.getRegionWidth());
                int texY = (int)((1f - y / getHeight()) * finalReturnRegion.getRegionHeight());

                // Sınır güvenliği (kenar piksellerinde taşmayı önler)
                texX = Math.max(0, Math.min(texX, finalReturnRegion.getRegionWidth()  - 1));
                texY = Math.max(0, Math.min(texY, finalReturnRegion.getRegionHeight() - 1));

                // Atlas içindeki bölgenin başlangıç ofsetini ekliyoruz
                int pixelX = finalReturnRegion.getRegionX() + texX;
                int pixelY = finalReturnRegion.getRegionY() + texY;

                // Pixmap'ten RGBA değerini al; en düşük byte alpha'dır
                int pixel = returnBtnPixmap.getPixel(pixelX, pixelY);
                int alpha  = pixel & 0xFF;

                // Alpha eşiği: 10'un altı şeffaf sayılır → tıklanamaz
                return alpha < 10 ? null : this;
            }
        };

        returnBtn.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) { // -1 = mouse move, not drag
                    game.getAudioManager().playSfx(game.getAssets().getSound(Assets.SFX_HOVER));
                }
            }
        });

        returnBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBack();
            }
        });
        // ─────────────────────────────────────────────────────────────────────

        // 7. Elemanları Panel Tablosuna Yerleştir
        // Not: Eğer butonlar panel arkaplanındaki yazılara tam oturmuyorsa,
        // padTop(), padBottom() değerlerini değiştirerek kolayca hizalayabilirsin.

        float trackW = panelW * 0.75f;
        float retW   = panelW * 0.80f;

        // Oranı manuel yazmak YERİNE, doğrudan Atlas'taki orijinal boyutlardan hesaplatıyoruz!
        float realRatio = (float) returnButtonRegion.getRegionHeight() / returnButtonRegion.getRegionWidth();
        float retH = retW * realRatio;

        // Yukarıdan aşağıya doğru elemanları ekliyoruz
        panel.add(fullscreenBtn).size(55f).left().padLeft(panelW * 0.04f).padTop(panelH * 0.40f).row();
        panel.add(sfxSlider).width(trackW).padTop(45).row();
        panel.add(musicSlider).width(trackW).padTop(45).row();
        panel.add(returnBtn).size(retW, retH).padTop(-65).padBottom(20).row();

        // Paneli kök tabloya ekle ve boyutunu ayarla
        rootTable.add(panel).size(panelW, panelH);
    }

    // =========================================================================
    // Yardımcı Grafik Metotları (ShapeRenderer yerine Pixmap kullanımı)
    // =========================================================================

    private Drawable createColorDrawable(Color color, int height) {
        Pixmap pixmap = new Pixmap(1, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        generatedTextures.add(texture); // Dispose edebilmek için listeye ekliyoruz
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private Drawable createCircleDrawable(int radius, Color color) {
        Pixmap pixmap = new Pixmap(radius * 2, radius * 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(radius, radius, radius);
        Texture texture = new Texture(pixmap);
        generatedTextures.add(texture);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    // =========================================================================
    // Ayarlar & Mantık
    // =========================================================================

    private void loadSettings() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        sfxLevel   = prefs.getFloat(PREF_SFX,   0.6f);
        musicLevel = prefs.getFloat(PREF_MUSIC,  0.75f);
        fullscreen = Gdx.graphics.isFullscreen();
    }

    private void saveSettings() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putFloat(PREF_SFX,   sfxLevel);
        prefs.putFloat(PREF_MUSIC, musicLevel);
        prefs.putBoolean(PREF_FULLSCREEN, fullscreen);
        prefs.flush();
    }

    private void applyAudioLevels() {
        game.getAudioManager().setMusicVolume(musicLevel);
        game.getAudioManager().setSfxVolume(sfxLevel);
    }

    private void goBack() {
        saveSettings();
        screenManager.showScreen(Screens.MAIN_MENU);
    }

    // =========================================================================
    // Render & Döngü
    // =========================================================================

    @Override
    public void render(float delta) {
        // Klavye kısayollarını kontrol et
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            goBack();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            fullscreenBtn.setChecked(!fullscreenBtn.isChecked());
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tüm çizim ve güncelleme işlemini Stage halleder!
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause(){
        saveSettings();
    }

    @Override
    public void resume(){}
    @Override
    public void hide() {
        saveSettings();
    }

    @Override
    public void dispose() {
        stage.dispose();

        // Pixel-perfect hit detection için oluşturulan pixmap'i temizle
        if (returnBtnPixmap != null) returnBtnPixmap.dispose();

        // Ürettiğimiz slider grafiklerini temizliyoruz (prosedürel, AssetManager dışı)
        for (Texture tex : generatedTextures) {
            tex.dispose();
        }
        // backgroundTexture ve atlas AssetManager tarafından yönetilir
    }
}