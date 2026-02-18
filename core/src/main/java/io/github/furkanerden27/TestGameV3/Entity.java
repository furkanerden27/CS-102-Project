package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Entity extends Sprite {
    /*Implementation od this class is incomplete */
    protected final double MAX_HEALTH;
    protected double health;
    protected boolean isAlive; // can be removed
    protected int gold;
    protected TextureRegion[] EntityImages;
    protected final Animation<TextureRegion> ENTITY_ANIMATION;

    /* TODO a private arrylist of effects (buffs or debuffs) should be added when they are made */

    protected TextureRegion currentFrame;
    protected float stateTime = 0f;
    

    public Entity(TextureRegion[] EntityImages, double health, float posX, float posY) {
        this.EntityImages = EntityImages;
        ENTITY_ANIMATION = new Animation<>(1f, EntityImages); /* can be changed later 
        just a demo for now */
        MAX_HEALTH = health;
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

    public abstract void draw(SpriteBatch batch);

    public abstract void update(float deltaTime);

    
}