package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Lust extends Boss {

    private Animation<TextureRegion> standing;

    public Lust(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Lust", 
            55, 93, new int[]{4});
        setSize(55, 93);
        name = "Lust";
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
        standing = getFlippedAnimation(animations[0]);
        currentAnimation = standing;
    }
    
}
