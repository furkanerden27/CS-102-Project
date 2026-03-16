package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Envy extends Boss {
    
    private Animation<TextureRegion> standing;
    private Animation<TextureRegion> die;
    private Animation<TextureRegion> attack;


    public Envy(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Envy", 
            32, 32, new int[]{2, 2, 4, 8, 6, 8, 3, 8, 8});
        setSize(24, 24);
    }

    @Override
    public void specialAttack(Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specialAttack'");
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    @Override
    protected void setAnimations(int[] frameCounts) {
        super.setAnimations(frameCounts);
        standing = getFlippedAnimation(animations[2]);
        die = getFlippedAnimation(animations[7]);
        attack = getFlippedAnimation(animations[8]);
        currentAnimation = standing;
    }
}
