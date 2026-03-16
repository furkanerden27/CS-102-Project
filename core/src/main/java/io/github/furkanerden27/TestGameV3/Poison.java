package io.github.furkanerden27.TestGameV3;

public class Poison extends Effect{
    public Poison(int duration, float baseValue) {
        super(duration, baseValue);
        effectName = "Poison";
    }

    @Override
    public void applyEffect(Entity e) {
        e.takeDamage(baseValue * magnitude);
        super.applyEffect(e);
    }
}
