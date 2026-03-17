package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Pride extends Boss {
    
    private Animation<TextureRegion> standing;

    public Pride(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Pride", 
            160, 144, new int[]{6});
        setSize(160, 144);
        name = "Pride";
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
        standing = animations[0];
        currentAnimation = standing;
    }
}
