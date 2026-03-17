package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gluttony extends Boss {

    private Animation<TextureRegion> agressive;
    private Animation<TextureRegion> standing;
    private Animation<TextureRegion> attack;

    public Gluttony(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Gluttony", 
            144, 80, new int[]{6, 4, 8});
        setSize(144, 80);
        name = "Gluttony";
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
        agressive = getFlippedAnimation(animations[0]);
        standing = getFlippedAnimation(animations[1]);
        attack = getFlippedAnimation(animations[2]);
        currentAnimation = standing;
    }
}