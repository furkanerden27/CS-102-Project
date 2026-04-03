package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Pride extends Boss {
    
    private Animation<TextureRegion> standing;

    private float egoShield = 0.90f;
    private float missProb = 0f; 
    private boolean shieldBroken = false;
    private boolean showMissText = false;

    private float attackMultiplier = 1f;

    public Pride(float posX, float posY) {
        super(posX, posY);
        damageDuration = 0.5f; // Customize damage effect duration if needed
        initAnimationsFromAtlas("Pride", 
            160, 144, new int[]{6});
        setSize(160, 144);
        baseAttackDamage = 15; // can be changed
        health = maxHealth = 200; // can be changed
    }

    @Override
    public void takeDamage(float damage) {
        // Reducing the incoming damage by the ego shield
        float actualDamage = shieldBroken ? damage : damage * (1 - egoShield);
        super.takeDamage(actualDamage);
        updateShield();
    }

    private void updateShield() {
        float healthPercent = health / maxHealth;

        if (healthPercent <= 0.20f) {
            attackMultiplier = 4.0f;
            missProb = 0.5f; 
            if (!shieldBroken) {
                shieldBroken = true;
                egoShield = 0f;
                showFloatingText("SHIELD BROKEN!", Color.RED);
            }
        } 
        else if (healthPercent <= 0.40f) {
            egoShield = 0.20f;
            attackMultiplier = 3.0f;
            missProb = 0.3f;
            showFloatingText("Ego Crumbling...", Color.ORANGE);
        } 
        else if (healthPercent <= 0.60f) {
            egoShield = 0.45f;
            attackMultiplier = 2.0f;
            missProb = 0.2f;
            showFloatingText("Ego Weakening...", Color.YELLOW);
        } 
        else if (healthPercent <= 0.80f) {
            egoShield = 0.70f;
            attackMultiplier = 1.5f;
            missProb = 0.1f;
            showFloatingText("Meh...", Color.ORANGE);
        }
        else {
            showFloatingText("Pathetic...", Color.ORANGE);
        }
    }

    @Override
    public void specialAttack(Player player) {
        isAttacking = true;
        if(Math.random() < 1 - missProb) {
            attackStateTime = 0;
            float damage = baseAttackDamage * attackMultiplier;
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
            if(attackStateTime == 0) {
                translate(-20, 0);
            }
            attackStateTime += deltaTime;
            if (attackStateTime >= 2f) {
                isAttacking = false;
                if (showMissText) {
                    showFloatingText("Missed!", Color.RED);
                    showMissText = false;
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
        standing = animations[0];
        currentAnimation = standing;
    }
}
