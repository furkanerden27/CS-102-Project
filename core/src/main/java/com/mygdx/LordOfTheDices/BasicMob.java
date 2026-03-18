package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BasicMob extends Mob {

    public static int level = 0;
    private final float CONSTANT = 20f;
    private final int MIN = 3;
    private final int MAX = 6; // can be changed
    private Animation<TextureRegion> standing;

    public BasicMob(float posX, float posY) {
        super(posX, posY);
        goldDropped = MIN * level + (int)(Math.random() * (MAX - MIN) * level + 1);
        level++;
        addMaxHealth(level * CONSTANT);
        initAnimationsFromAtlas("BasicMob", 
            64, 64, new int[]{6});
        setSize(64, 64);
        name = "BasicMob";
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    @Override
    protected void setAnimations(int[] frameCounts) {
        super.setAnimations(frameCounts);
        standing = getFlippedAnimation(animations[0]);
        currentAnimation = standing;
    }
    
}
