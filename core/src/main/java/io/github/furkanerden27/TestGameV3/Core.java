package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Core extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // Setting the first screen
        this.setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        // Super.render() must always stay, it calls the render of the active screen
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
