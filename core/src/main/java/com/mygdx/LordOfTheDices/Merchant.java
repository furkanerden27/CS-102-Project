package com.mygdx.LordOfTheDices;

public class Merchant extends Entity {

    public Merchant(float posX, float posY) {
        super(1, posX, posY);
        initAnimationsFromAtlas("Merchant", 800, 400, new int[]{1});
        setSize(64, 32);
        name = "Merchant";
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);
    }

    @Override
    protected void setAnimations(int[] frameCounts) {
        super.setAnimations(frameCounts);
        currentAnimation = animations[0];
    }
}
