package com.mygdx.LordOfTheDices;

public class Strengthen extends Effect{

    public Strengthen(int duration, float baseValue) {
        super(duration, baseValue);
        effectName = "Strengthen";
    }

    @Override
    public void applyEffect(Entity e) {
        if(e instanceof Player){
            ((Player)e).addAttackModifier(baseValue);
        }
        else if(e instanceof Mob){
            float current = ((Mob)e).getEffectiveAttackDamage();
            ((Mob)e).setEffectiveAttackDamage(current * (1f + baseValue));
        }
        super.applyEffect(e);
    }

}
