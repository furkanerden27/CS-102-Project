package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/* base class for all items, that are cards, dice and relics */
public abstract class Item extends Sprite{
    
    protected String name;
    protected String description;

    private static final Color TEMP_COLOR = new Color();
    private static final GlyphLayout LAYOUT = new GlyphLayout();

    public Item(String name) {
        super();
        this.name = name;
        this.description = "";
    }

    /* returns the descripion of the item as a string to show while hovering */
    public abstract String getDescription();

    /* loads the correct sprite region for this item */
    protected abstract void loadTexture();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /* draws the item at the screen in the given position */
    public void drawAt(SpriteBatch batch, float xPos, float yPos) {
        setPosition(xPos, yPos);
        draw(batch);
    }

    /* draws the item at the screen in the given position with the specified size*/
    public void drawAt(SpriteBatch batch, float xPos, float yPos, float width, float height) {
        setPosition(xPos, yPos);
        setSize(width, height);
        draw(batch);
    }

    public boolean isClicked(float mouseX, float mouseY) {
        float deltaX = getX() - mouseX;
        float deltaY = getY() - mouseY;
        return ((deltaX >= 0 && deltaX <= getWidth()) && (deltaY >= 0 && deltaY <= getHeight()));
    }

    /* Returns true if the mouse is hovering over this item's bounds. */
    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth()
            && mouseY >= getY() && mouseY <= getY() + getHeight();
    }

    /* Draws a tooltip with the description near the mouse position, call when isHovered() returns true.*/
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

        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.8f);
        sr.rect(tooltipX - padX, tooltipY, boxW, boxH);
        sr.end();

        batch.begin();
        TEMP_COLOR.set(font.getColor());
        font.setColor(Color.WHITE);
        font.draw(batch, desc, tooltipX, tooltipY + boxH - padY);
        font.setColor(TEMP_COLOR);
        batch.end();
    }
}
