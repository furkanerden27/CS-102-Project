package io.github.furkanerden27.TestGameV3;

public abstract class Mob extends Entity {
    
    protected float baseAttackDamage;
    protected float effectiveAttackDamage; // efektler eklendikten sonra karşı tarafa aktarilacak hasar bu. 

    public Mob(float posX, float posY) {
        super(0, posX, posY);
    }
    
    public void setEffectiveAttackDamage(float d){
        effectiveAttackDamage = d;
    }
    public float getBaseAttackDamage(){
        return baseAttackDamage;
    }
}
