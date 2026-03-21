package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Sloth extends Boss {
    
    private Animation<TextureRegion> standing;

    public Sloth(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Sloth", 
            64, 64, new int[]{16});
        setSize(32,32);
        name = "Sloth";
    }

    @Override
    public void specialAttack(Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specialAttack'");
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        updateDamageEffect(deltaTime);
    }

    @Override
    protected void setAnimations(int[] frameCounts) {
        super.setAnimations(frameCounts);
        standing = getFlippedAnimation(animations[0]);
        currentAnimation = standing;
    }
    
}
