package com.mygdx.LordOfTheDices;

public class Bleeding extends Effect{
    public Bleeding(int duration, float baseValue) {
        super(duration, baseValue);
        effectName = "Bleeding";
    }

    @Override
    public void applyEffect(Entity e) {
        e.takeDamage(baseValue * magnitude);
        super.applyEffect(e);
    }
}
