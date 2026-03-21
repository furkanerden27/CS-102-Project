package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gluttony extends Boss {

    private Animation<TextureRegion> agressive;
    private Animation<TextureRegion> standing;
    private Animation<TextureRegion> attack;
    private double hitProb;
    private boolean isAttacking = false;
    private float attackStateTime = 0;
    private boolean showMissText = false;

    public Gluttony(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Gluttony", 
            144, 80, new int[]{6, 4, 8});
        setSize(144, 80);
        name = "Gluttony";
        hitProb = 0.3;
        baseAttackDamage = 10; // Base attack damage for Gluttony (can be changed)
    }

    @Override
    public void specialAttack(Player player) {
        // Start the attacking animation
        isAttacking = true;
        attackStateTime = 0;
        currentAnimation = attack;
        if (Math.random() < hitProb) {
            float damage = baseAttackDamage * 4;
            player.takeDamage(damage);
        }
        else {
            showMissText = true;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!isAlive) { return; }
        stateTime += deltaTime;
        if (isAttacking) {
            attackStateTime += deltaTime;
            if (attackStateTime >= attack.getAnimationDuration()) {
                isAttacking = false;
                currentAnimation = standing;
                if (showMissText) {
                    showFloatingText("Missed!", Color.RED);
                    showMissText = false;
                }
            }
        }
        updateDamageEffect(deltaTime);
        updateFloatingTexts(deltaTime);
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
