package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gluttony extends Boss {

    private Animation<TextureRegion> agressive;
    private Animation<TextureRegion> standing;
    private Animation<TextureRegion> attack;

    public Gluttony(float posX, float posY) {
        super(posX, posY);
        bossTexture = new Texture("Entities/Gluttony.png");
        
        entityImages = TextureRegion.split(bossTexture, 144, 80);
        animations = new Animation[entityImages.length];
        int[] frameCounts = {6, 4, 8};
        setAnimations(frameCounts);
        // Set size to match frame dimensions (adjust if scaling is needed)
        setSize(144, 80);
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