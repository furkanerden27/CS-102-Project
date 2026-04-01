package io.github.furkanerden27.TestGameV3;

public class Lure extends Effect{

    public Lure(int duration, float baseValue) {
        super(duration, baseValue);
        effectName = "Lure";
    }

    @Override
    public void applyEffect(Entity e) {
        e.setStun(true);
        super.applyEffect(e);
    }

}