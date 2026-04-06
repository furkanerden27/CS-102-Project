package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Texture;

public abstract class Boss extends Mob {
    
    public static int level = 1;
    protected Texture bossTexture;
    private final int MIN = 30;
    private final int MAX = 50;

    public Boss(float posX, float posY) {
        super(posX, posY);
        name = "Boss";
        health = maxHealth = 120;
        baseAttackDamage = 8;
        goldDropped = MIN * level + (int)(Math.random() * (MAX - MIN) * level + 1);
        level++;
    }
    
}
