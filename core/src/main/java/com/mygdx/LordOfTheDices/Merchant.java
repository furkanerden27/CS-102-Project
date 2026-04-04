package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Merchant extends Entity {

    private Animation<TextureRegion> standing;
    
    public Merchant(float posX, float posY) {
        super(1, posX, posY);
        initAnimationsFromAtlas("Merchant", 
        80, 80, new int[]{10, 6, 10, 8, 6});
        setSize(80, 80);
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
        standing = animations[0];
        currentAnimation = standing;
    }
}
