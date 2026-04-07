package com.mygdx.LordOfTheDices;

public class Strengthen extends Effect{

    public Strengthen(int duration, float baseValue) {
        super(duration, baseValue);
        effectName = "Strengthen";
    }

    @Override
    public void applyEffect(Entity e) {
        if(e instanceof Player){
            ((Player)e).addAttackModifier(baseValue * magnitude);
        }
        else if(e instanceof Mob){
            ((Mob)e).setEffectiveAttackDamage(((Mob)e).getEffectiveAttackDamage() + (baseValue * magnitude));
        }
        super.applyEffect(e);
    }
    
}
