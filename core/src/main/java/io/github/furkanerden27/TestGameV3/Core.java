package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Core extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // İlk açılacak ekranı set ediyoruz
        this.setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        // Super.render() mutlaka kalmalı, aktif ekranın render'ını çağırır
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
