package io.github.furkanerden27.TestGameV3;

public class Weaken extends Effect{

    public Weaken(int duration, float baseValue) {
        super(duration, baseValue);
        effectName = "Weaken";
    }

    @Override
    public void applyEffect(Entity e) {
        if(e instanceof Player){
            ((Player)e).setAttackModifier(baseValue * magnitude * (-1));
        }
        else if(e instanceof Mob){
            ((Mob)e).setEffectiveAttackDamage(((Mob)e).getBaseAttackDamage() - (baseValue * magnitude));
        }
        super.applyEffect(e);

    }
    
}
