package com.mygdx.LordOfTheDices;

public class Weaken extends Effect{

    public Weaken(int duration, float baseValue) {
        super(duration, baseValue);
        effectName = "Weaken";
    }

    @Override
    public void applyEffect(Entity e) {
        if(e instanceof Player){
            ((Player)e).addAttackModifier(-baseValue);
        }
        else if(e instanceof Mob){
            float current = ((Mob)e).getEffectiveAttackDamage();
            ((Mob)e).setEffectiveAttackDamage(current * (1f - baseValue));
        }
        super.applyEffect(e);
    }

}
