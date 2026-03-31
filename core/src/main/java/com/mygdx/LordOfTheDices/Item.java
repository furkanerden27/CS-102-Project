package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/* base class for all items, that are cards, dice and relics */
public abstract class Item extends Sprite{
    
    protected String name;
    protected String description;

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

    public boolean isClicked(float mouseX, float mouseY){
        //returns if the given coordinates are in between the boundaries of the sprite 
        float deltaX = getX() - mouseX;
        float deltaY = getY() - mouseY;
        return ((deltaX >= 0 && deltaX <= getWidth()) && (deltaY >= 0 && deltaY <= getHeight()));
    }
}
