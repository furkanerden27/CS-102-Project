package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Sloth extends Boss {
    
    private Animation<TextureRegion> standing;
    private double stunProb = 0.4f; 
    private boolean showstunText = false;

    public Sloth(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Sloth", 
            64, 64, new int[]{16});
        setSize(32,32);
        name = "Sloth";
        baseAttackDamage = 15; // can be changed
        health = maxHealth = 100; // can be changed
    }

    @Override
    public void specialAttack(Player player) {
        attackStateTime = 0;
        float damage = baseAttackDamage;
        if (Math.random() < stunProb) {
            isAttacking = true;
            player.takeDamage(damage);
            player.addEffect(new Lure(1, damage)); // henüz çalışıyor mu emin değilim, duration ve baseValue değerleri değişebilir
            showstunText = true;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!isAlive) { return; }
        stateTime += deltaTime;
        if (isAttacking) {
            if(attackStateTime == 0) {
                translate(-20, 0);
            }
            attackStateTime += deltaTime;
            if (attackStateTime >= 2f) {
                isAttacking = false;
                if (showstunText) {
                    showFloatingText("Stunned!", Color.BLUE);
                    showstunText = false;
                }
                translate(20, 0);
            }
        }
        updateDamageEffect(deltaTime);
        updateFloatingTexts(deltaTime);
    }

    @Override
    protected void setAnimations(int[] frameCounts) {
        super.setAnimations(frameCounts);
        standing = getFlippedAnimation(animations[0]);
        currentAnimation = standing;
    }
    
}
