package com.mygdx.LordOfTheDices;

public class Weaken extends Effect{

    public Weaken(int duration, float baseValue) {
        super(duration, baseValue);
        effectName = "Weaken";
    }

    @Override
    public void applyEffect(Entity e) {
        if(e instanceof Player){
            ((Player)e).addAttackModifier(-(baseValue * magnitude));
        }
        else if(e instanceof Mob){
            ((Mob)e).setEffectiveAttackDamage(((Mob)e).getEffectiveAttackDamage() * (1f - baseValue * magnitude));
        }
        super.applyEffect(e);
    }
    
}
