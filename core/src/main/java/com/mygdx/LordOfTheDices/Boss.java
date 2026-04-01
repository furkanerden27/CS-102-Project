package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.Texture;

public abstract class Boss extends Mob {
    
    public static int level = 1;
    protected Texture bossTexture;
    private final int MIN = 10;
    private final int MAX = 20; // can be changed

    public Boss(float posX, float posY) {
        super(posX, posY);
        goldDropped = MIN * level + (int)(Math.random() * (MAX - MIN) * level + 1);
        level++;
    }
    
}