package io.github.furkanerden27.TestGameV3;

public class Stun extends Effect{

    public Stun(int duration, float baseValue) {
        super(duration, baseValue);
        effectName = "Stun";
    }

    @Override
    public void applyEffect(Entity e) {
        e.setStun(true);
        super.applyEffect(e);
    }
}
