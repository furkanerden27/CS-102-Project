package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Preferences;

public class AudioManager {

    private static final String NAME  = "game_settings";
    private static final String MUSIC  = "music_level";
    private static final String SFX    = "sfx_level";

    private Music menuMusic;
    private float musicVolume;
    private float sfxVolume;

    public AudioManager() {
        Preferences prefs = Gdx.app.getPreferences(NAME);
        musicVolume = prefs.getFloat(MUSIC, 0.75f);
        sfxVolume   = prefs.getFloat(SFX,   0.6f);
    }

    // ── Müzik ────────────────────────────────────────────────────────────────

    public void playMenuMusic() {
        if (menuMusic == null) {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/menu_music.mp3"));
            menuMusic.setLooping(true);
            menuMusic.setVolume(musicVolume);
        }
        if (!menuMusic.isPlaying()) {
            menuMusic.play();
        }
    }

    public void stopMenuMusic() {
        if (menuMusic != null && menuMusic.isPlaying()){
            menuMusic.stop();
        }
    }

    public void setMusicVolume(float volume){
        this.musicVolume = volume;
        if (menuMusic != null){
            menuMusic.setVolume(volume);
        }
    }

    public float getMusicVolume(){
        return musicVolume;
    }

    public Music getMenuMusic(){
        return menuMusic;
    }

    // ── SFX ──────────────────────────────────────────────────────────────────

    public void playSfx(Sound sound) {
        if (sound != null && sfxVolume > 0f) {
            sound.play(sfxVolume);
        }
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = volume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void dispose(){
        if (menuMusic != null) {
            menuMusic.dispose();
            menuMusic = null;
        }
    }
}
