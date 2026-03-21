package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Pride extends Boss {
    
    private Animation<TextureRegion> standing;

    public Pride(float posX, float posY) {
        super(posX, posY);
        damageDuration = 0.5f; // Customize damage effect duration if needed
        initAnimationsFromAtlas("Pride", 
            160, 144, new int[]{6});
        setSize(160, 144);
        name = "Pride";
        // these 2 lines are for testing purposes, remove them later
        isTakingDamage = true; 
        damageDuration = 5f;
    }

    @Override
    public void specialAttack(Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specialAttack'");
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        setRegion(currentFrame);
        updateDamageEffect(deltaTime);
    }

    @Override
    protected void setAnimations(int[] frameCounts) {
        super.setAnimations(frameCounts);
        standing = animations[0];
        currentAnimation = standing;
    }
}
