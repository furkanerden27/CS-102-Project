package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Wrath extends Boss {
    
    private Animation<TextureRegion> standing;
    private boolean showRageText = false;
    private double rageProb;

    private float rage; // Rage level, can be used to increase attack damage

    public Wrath(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Wrath", 
            121, 110, new int[]{4});
        setSize(121,110);
        rageProb = 0.4; // Probability of rage attack, can be adjusted
    }

    @Override
    public void specialAttack(Player player) {
        isAttacking = true;
        attackStateTime = 0;
        if (Math.random() < rageProb) {
            player.takeDamage(effectiveAttackDamage);
            showRageText = true;
        }
        else {
            player.takeDamage(baseAttackDamage);
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
                if (showRageText) {
                    showFloatingText("MEGA HIT!", Color.BLACK);
                    showRageText = false;
                }
                translate(20, 0);
            }
        }
        updateRage();
        updateDamageEffect(deltaTime);
        updateFloatingTexts(deltaTime);
    }

    private void updateRage() {
        if(!isAlive) return;
        rage = (float) Math.sqrt(maxHealth / health); // Rage increases as health decreases, can be adjusted to fit desired difficulty curve
        effectiveAttackDamage = baseAttackDamage * rage; // Update effective attack damage based on rage
    }

    @Override
    protected void setAnimations(int[] frameCounts) {
        super.setAnimations(frameCounts);
        standing = animations[0];
        currentAnimation = standing;
    }
}
