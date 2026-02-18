package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Entity {
    private int health;
    private boolean isAlive;
    private final TextureRegion[] ENTITY_IMAGES;
    private final Animation<TextureRegion> ENTITY_ANIMATION;
    private TextureRegion currentFrame;
    private SpriteBatch batch;
    private float posX;
    private float posY;

    public Entity(TextureRegion[] ENTITY_IMAGES, SpriteBatch batch, int health, float posX, float posY) {
        this.ENTITY_IMAGES = ENTITY_IMAGES;
        ENTITY_ANIMATION = new Animation<>(1f, ENTITY_IMAGES); /* can be changed later 
        just a demo for now */
        this.batch = batch;
        this.health = health;
        this.posX = posX;
        this.posY = posY;
        isAlive = true;
    }

    public void getDamage(int damage) {
        health -= damage;
    }

    public void heal(int heal) {
        health += heal;
    }

    



    
}