package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends Game {
    public SpriteBatch batch;
    ScreenManager screen;
    AudioManager audioManager;
    Assets assets;

    @Override
    public void create(){
        batch = new SpriteBatch();
        assets = new Assets();
        assets.load();
        assets.finishLoading();

        Card.init(assets.getAtlas(Assets.ATLAS_CARDS));
        Dice.init(assets.getTexture(Assets.TEXTURE_DICE));
        Relic.init(assets.getAtlas(Assets.ATLAS_RELICS));
        Effect.effects = assets.getAtlas(Assets.ATLAS_EFFECTS);

        screen = new ScreenManager(this);
        audioManager = new AudioManager();

        // Kaydedilmiş fullscreen ayarını uygula
        Preferences prefs = Gdx.app.getPreferences("game_settings");
        if (prefs.getBoolean("fullscreen", false)) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }

        screen.showScreen(Screens.MAIN_MENU);
    }

    public AudioManager getAudioManager(){
        return audioManager;
    }

    public Assets getAssets() {
        return assets;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        audioManager.dispose();
        assets.dispose();
    }
}
