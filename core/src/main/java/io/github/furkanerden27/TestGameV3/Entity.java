package io.github.furkanerden27.TestGameV3;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Entity extends Sprite {
    /*Implementation of this class is incomplete */
    protected float maxHealth;
    protected float health;
    protected boolean isAlive; // can be removed
    protected int goldDropped;
    protected String direction;
    protected TextureRegion[][] entityImages;
    protected Animation<TextureRegion>[] animations;
    protected boolean isStunned;

    /* TODO a private arrylist of effects (buffs or debuffs) should be added when they are made */

    protected TextureRegion currentFrame;
    protected float stateTime = 0f;
    
    private ArrayList<Effect> effectsInFight;
    private ArrayList<Effect> effectsRemoveList;

    public Entity(float health, float posX, float posY) {
        maxHealth = health;
        direction = "";
        this.health = maxHealth;
        setX(posX);
        setY(posY);
        isAlive = true;
        isStunned = false;
        effectsInFight = new ArrayList<>();
        effectsRemoveList= new ArrayList<>();
    }

    public void takeDamage(float  damage) {
        health = Math.max(0, health - damage);
        if(health == 0) {
            isAlive = false;
        }
    }

    public void heal(float  heal) {
        health = Math.min(maxHealth, health + heal);
    }

    public void addMaxHealth(float  heal) {
        health += heal;
        maxHealth += heal;
    }

    public abstract void update(float deltaTime);

    public void setStun(boolean isStunned){
        this.isStunned = isStunned;
    }

    public void applyEffects(){
        for(Effect e : effectsInFight){
            e.applyEffect(this);
        }
        removeListedEffects();
    }

    public void addEffect(Effect e){
        effectsInFight.add(e);
    }

    public void removeQueue(Effect e){
        effectsRemoveList.add(e);
    }

    private void removeListedEffects(){
        for(Effect e : effectsRemoveList){
            effectsInFight.remove(e);
        }
    }
    
}