package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BasicMob extends Mob {

    public static int level = 0;
    private final float CONSTANT = 30f;
    private final int MIN = 20;
    private final int MAX = 35;
    private Animation<TextureRegion> standing;

    public BasicMob(float posX, float posY) {
        super(posX, posY);
        level++;
        goldDropped = MIN * level + (int)(Math.random() * (MAX - MIN) * level + 1);
        addMaxHealth(level * CONSTANT);
        initAnimationsFromAtlas("BasicMob", 
            64, 64, new int[]{6});
        setSize(64, 64);
        name = "BasicMob";
        baseAttackDamage = 6 + 2 * level;
    }

    @Override
    public void specialAttack(Player player) {
        isAttacking = true;
        attackStateTime = 0;
        float damage = effectiveAttackDamage;
        player.takeDamage(damage);
    }

    @Override
    public void update(float deltaTime) {
        if (!isAlive) { return; }
        stateTime += deltaTime;
        if (isAttacking) {
            if(attackStateTime == 0) {
                translate(-60, 0);
            }
            attackStateTime += deltaTime;
            if (attackStateTime >= 2f) {
                isAttacking = false;
                translate(60, 0);
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
