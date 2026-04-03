package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Dice extends Item {

    private static TextureRegion[] frames;
    private static Animation<TextureRegion> diceAnimation;

    // Call once in AssetManager
    public static void init(Texture diceAsset) {
        frames = TextureRegion.split(diceAsset, 32, 32)[0];
        diceAnimation = new Animation<>(0.05f, frames);
        diceAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    private int value;
    private float stateTime;
    private boolean isRolling; 
    private boolean isLocked;

    public Dice(String name) {
        super(name);
        description = "A six-sided dice. Current value: " + value;
        value = 1;
        isLocked = false;
        isRolling = false;
        this.stateTime = 0f;
    }

    //returns a random num from 1 to 6, call it to stop rolling animation
    public int roll() {
        if(isLocked) {
            return value;
        }
        value = (int) (Math.random() * 6) + 1;
        stateTime = (value - 1) * diceAnimation.getFrameDuration();
        isRolling = false;
        return value;
    }

    //call after roll all button is clicked
    public void startRolling() {
        if (isLocked) return;
        isRolling = true;
        stateTime = (float) Math.random() * 10;
    }

    //use when rendering
    public void update(float deltaTime) {
        if(isRolling) {
            stateTime += deltaTime;
        }
    }

    //use when rendering to display dice
    public TextureRegion getCurrentFrame() {
        return diceAnimation.getKeyFrame(stateTime);
    }

    public int getValue() { 
        return value; 
    }

    public boolean isLocked() { 
        return isLocked; 
    }

    public void setLocked(boolean l) { 
        this.isLocked = l; 
    }

    public boolean isRolling() { 
        return isRolling; 
    }

    @Override
    protected void loadTexture() {}
}
