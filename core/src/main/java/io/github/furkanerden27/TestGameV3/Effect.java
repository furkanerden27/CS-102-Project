package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public abstract class Effect {
    
    public static final TextureAtlas effects = new TextureAtlas(Gdx.files.local("effects/Effects.atlas"));
    protected int durationLeft;
    protected float magnitude;
    protected float baseValue;
    protected String effectName;
    
    protected FightManager currentFight;

    public Effect(int duration, float baseValue){
        this.durationLeft = duration;
        this.baseValue = baseValue;
    }

   

    public void applyEffect(Entity e){
        durationLeft--;
        if(didExpire()){
            e.removeQueue(this);
        }
    }

    public void setMagnitude(float magnitude){
        this.magnitude = magnitude;
    }

    public boolean didExpire(){
        return durationLeft <= 0;
    }

    public String getName(){
        return effectName;
    }
}
