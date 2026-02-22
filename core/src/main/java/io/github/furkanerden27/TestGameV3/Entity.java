package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Entity extends Sprite {
    /*Implementation of this class is incomplete */
    protected final double MAX_HEALTH;
    protected double health;
    protected boolean isAlive; // can be removed
    protected int gold;
    protected String direction;
    protected TextureRegion[][] entityImages;
    protected Animation<TextureRegion>[] animations;

    /* TODO a private arrylist of effects (buffs or debuffs) should be added when they are made */

    protected TextureRegion currentFrame;
    protected float stateTime = 0f;
    

    public Entity(double health, float posX, float posY) {
        MAX_HEALTH = health;
        direction = "";
        this.health = MAX_HEALTH;
        setX(posX);
        setY(posY);
        isAlive = true;
    }

    public void getDamage(int damage) {
        health = Math.max(0, health - damage);
        if(health == 0) {
            isAlive = false;
        }
    }

    public void heal(int heal) {
        health = Math.min(MAX_HEALTH, health + heal);
        
    }

    public abstract void move();

    public abstract void update(float deltaTime);
    
}