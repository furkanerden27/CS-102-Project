package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/* base class for all items, that are cards, dice and relics */
public abstract class Item extends Sprite {

    // reused across all tooltip draws — zero allocation per frame
    private static final GlyphLayout LAYOUT = new GlyphLayout();
    private static final Color TEMP_COLOR = new Color();

    protected String name;
    protected String description;

    public Item(String name) {
        super();
        this.name = name;
        this.description = "";
    }

    public abstract String getDescription();

    protected abstract void loadTexture();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void drawAt(SpriteBatch batch, float xPos, float yPos) {
        setPosition(xPos, yPos);
        draw(batch);
    }

    public void drawAt(SpriteBatch batch, float xPos, float yPos, float width, float height) {
        setPosition(xPos, yPos);
        setSize(width, height);
        draw(batch);
    }

    public boolean isClicked(float mouseX, float mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth()
            && mouseY >= getY() && mouseY <= getY() + getHeight();
    }

    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth()
            && mouseY >= getY() && mouseY <= getY() + getHeight();
    }

    /**
     * Draws a tooltip with this item's description near the mouse position.
     *
     * Usage: call between batch.end() and the next batch.begin(), or
     * manage the batch/sr lifecycle outside. The caller owns the ShapeRenderer.
     *
     * @param batch the SpriteBatch (must NOT be drawing when called)
     * @param sr    a reusable ShapeRenderer (caller creates once, disposes on screen dispose)
     * @param font  a BitmapFont for the text
     * @param mouseX world-space x
     * @param mouseY world-space y
     */
    public void drawTooltip(SpriteBatch batch, ShapeRenderer sr, BitmapFont font, float mouseX, float mouseY) {
        String desc = getDescription();
        if (desc == null || desc.isEmpty()) return;

        LAYOUT.setText(font, desc);
        float padX = 8f;
        float padY = 6f;
        float tooltipX = mouseX + 12f;
        float tooltipY = mouseY + 4f;
        float boxW = LAYOUT.width + padX * 2;
        float boxH = LAYOUT.height + padY * 2;

        // Background box
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.8f);
        sr.rect(tooltipX - padX, tooltipY, boxW, boxH);
        sr.end();

        // Text
        batch.begin();
        TEMP_COLOR.set(font.getColor());
        font.setColor(Color.WHITE);
        font.draw(batch, desc, tooltipX, tooltipY + boxH - padY);
        font.setColor(TEMP_COLOR);
        batch.end();
    }
}
