package io.github.furkanerden27.TestGameV3;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    protected Animation<TextureRegion> currentAnimation;
    protected boolean isStunned;
    protected String name;

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

    protected void initAnimationsFromAtlas(String regionName, int tileWidth, int tileHeight, int[] frameCounts) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("entities/Entities.atlas"));
        TextureRegion region = atlas.findRegion(regionName);
        initAnimationsFromRegion(region, tileWidth, tileHeight, frameCounts);
    }

    // Initialize animations from a single TextureRegion
    private void initAnimationsFromRegion(TextureRegion region, int tileWidth, int tileHeight, int[] frameCounts) {
        entityImages = splitRegion(region, tileWidth, tileHeight);
        animations = new Animation[entityImages.length];
        setAnimations(frameCounts);
    }

    
    private static TextureRegion[][] splitRegion(TextureRegion region, int tileWidth, int tileHeight) {
        int cols = region.getRegionWidth() / tileWidth;
        int rows = region.getRegionHeight() / tileHeight;
        TextureRegion[][] tiles = new TextureRegion[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tiles[i][j] = new TextureRegion(
                    region.getTexture(),
                    region.getRegionX() + j * tileWidth,
                    region.getRegionY() + i * tileHeight,
                    tileWidth, tileHeight
                );
            }
        }
        return tiles;
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

    public Animation<TextureRegion> getFlippedAnimation(Animation<TextureRegion> animation) {
        Object[] originalFrames = animation.getKeyFrames();
        TextureRegion[] flippedFrames = new TextureRegion[originalFrames.length];

        for (int i = 0; i < originalFrames.length; i++) {
            flippedFrames[i] = new TextureRegion((TextureRegion) originalFrames[i]);
            flippedFrames[i].flip(true, false);
        }
        return new Animation<>(animation.getFrameDuration(), flippedFrames);
    }

    protected void setAnimations(int[] frameCounts) {
        for (int i = 0; i < entityImages.length; i++) {
            TextureRegion[] frames = new TextureRegion[frameCounts[i]];
            for (int j = 0; j < frameCounts[i]; j++) {
                frames[j] = entityImages[i][j];
            }
            animations[i] = new Animation<>(1f / frames.length, frames);
        }
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(currentAnimation.getKeyFrame(stateTime, true), getX(), getY(), getWidth(), getHeight());
    }

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
    public int getGold(){
        return goldDropped;
    }
    public void removeAllEffects(){
        effectsInFight.clear();
    }
}