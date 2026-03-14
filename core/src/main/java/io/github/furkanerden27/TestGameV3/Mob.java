package io.github.furkanerden27.TestGameV3;

public abstract class Mob extends Entity {
    
    protected float baseAttachDamage;

    public Mob(float posX, float posY) {
        super(0, posX, posY);
    }
    
}
