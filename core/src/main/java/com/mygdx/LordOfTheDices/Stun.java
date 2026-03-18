package com.mygdx.LordOfTheDices;

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
