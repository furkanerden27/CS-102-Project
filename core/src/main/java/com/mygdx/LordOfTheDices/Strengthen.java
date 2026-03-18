package com.mygdx.LordOfTheDices;

public class Strengthen extends Effect{

    public Strengthen(int duration, float baseValue) {
        super(duration, baseValue);
        effectName = "Strengthen";
    }

    @Override
    public void applyEffect(Entity e) {
        if(e instanceof Player){
            ((Player)e).setAttackModifier(baseValue * magnitude);
        }
        else if(e instanceof Mob){
            ((Mob)e).setEffectiveAttackDamage(((Mob)e).getBaseAttackDamage() + (baseValue * magnitude));
        }
        super.applyEffect(e);

    }
    
}
