package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class FloatingText {
    private static BitmapFont defaultFont = new BitmapFont();

    private String text;
    private float x, y;
    private float velocityY;
    private float duration;
    private float timeElapsed;
    private Color color;
    private BitmapFont font;
    private boolean isAlive;

    public FloatingText(String text, float x, float y, Color color) {
        this(text, x, y, color, defaultFont);
        defaultFont.setUseIntegerPositions(false);
        defaultFont.getData().setScale(0.7f);
        defaultFont.getRegion().getTexture().setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );
    }

    private FloatingText(String text, float x, float y, Color color, BitmapFont font) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        this.font = font;
        this.velocityY = 50f; // pixels per second upwards
        this.duration = 2f; // seconds
        this.timeElapsed = 0;
        this.isAlive = true;
    }

    public void update(float deltaTime) {
        timeElapsed += deltaTime;
        y += velocityY * deltaTime;
        // To contol the fading
        float alpha = 1 - (timeElapsed / duration);
        if (alpha < 0) {
            alpha = 0;
        }
        color.a = alpha;

        if (timeElapsed >= duration) {
            isAlive = false;
        }
    }

    public void setImmovable() {
        this.velocityY = 0;
    }

    public void setDurationIndefinite() {
        this.duration = Float.MAX_VALUE;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void render(Batch batch) {
        if (isAlive) {
            font.setColor(color);
            GlyphLayout layout = new GlyphLayout(font, text);
            font.draw(batch, text, x - layout.width / 2, y);
        }
    }

    public boolean isAlive() {
        return isAlive;
    }
}