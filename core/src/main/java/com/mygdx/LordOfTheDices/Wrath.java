package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Wrath extends Boss {
    
    private Animation<TextureRegion> standing;

    public Wrath(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Wrath", 
            121, 110, new int[]{4});
        setSize(121,110);
        name = "Wrath";
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
        standing = animations[0];
        currentAnimation = standing;
    }
}