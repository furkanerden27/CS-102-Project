package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Envy extends Boss {
    
    private Animation<TextureRegion> standing;
    private Animation<TextureRegion> die;
    private Animation<TextureRegion> attack;
    private Card lastPlayedCard = null;
    private boolean showMimicText = false;
    private float mimicDamageMultiplier = 0.75f; // can be changed
    private final float MIMIC_PROB = 0.8f; 


    public Envy(float posX, float posY) {
        super(posX, posY);
        initAnimationsFromAtlas("Envy", 
            32, 32, new int[]{2, 2, 4, 8, 6, 8, 3, 8, 8});
        setSize(24, 24);
        baseAttackDamage = 8;
        health = maxHealth = 100;
    }

    public void mimic(Player player) {
        // Mimic the player's last played card
        switch (lastPlayedCard.getSuit()) {
            case SPADES:
                player.takeDamage(lastPlayedCard.getPower() * mimicDamageMultiplier);
                break;
            case HEARTS:
                this.heal(lastPlayedCard.getPower() * mimicDamageMultiplier);
                break;
            // TODO I m not sure
            case DIAMONDS:
                player.addEffect(new Weaken(2, lastPlayedCard.getPower() * mimicDamageMultiplier));
                break;
            case CLUBS:
                this.addEffect(new Strengthen(2, lastPlayedCard.getPower() * mimicDamageMultiplier));
                break;
            default:
                break;
        }
        showMimicText = true;
    }

    public void setLastPlayedCard(Card card) {
        this.lastPlayedCard = card;
    }

    @Override
    public void specialAttack(Player player) {
        isAttacking = true;
        attackStateTime = 0;
        currentAnimation = attack;
        if (lastPlayedCard != null && Math.random() < MIMIC_PROB) {
            mimic(player);
        } 
        else {
            player.takeDamage(baseAttackDamage);
        }
        lastPlayedCard = null;
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
            if (attackStateTime >= attack.getAnimationDuration()) {
                currentAnimation = standing;
                if (showMimicText) {
                    showFloatingText("Mimicked!", Color.PURPLE);
                    showMimicText = false;
                }
            }
            if(attackStateTime >= 2f) {
                translate(20, 0);
                isAttacking = false;
            }
        }
        updateDamageEffect(deltaTime);
        updateFloatingTexts(deltaTime);
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
