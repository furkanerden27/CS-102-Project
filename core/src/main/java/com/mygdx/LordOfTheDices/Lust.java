package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Lust extends Boss {

    private Animation<TextureRegion> standing;
    private boolean showLureText = false;
    private double lureProb;

    public Lust(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Lust", 
            55, 93, new int[]{4});
        setSize(55, 93);
        name = "Lust";
        lureProb = 0.5;
        baseAttackDamage = 10; // may be changed later
    }

    @Override
    public void specialAttack(Player player) {
        isAttacking = true;
        attackStateTime = 0;
        float damage = baseAttackDamage;
        player.takeDamage(damage);
        if (Math.random() < lureProb) {
            player.addEffect(new Lure(1, damage)); // henüz çalışıyor mu emin değilim, duration ve baseValue değerleri değişebilir
            showLureText = true;
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
                if (showLureText) {
                    showFloatingText("Lured!", Color.BLUE);
                    showLureText = false;
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