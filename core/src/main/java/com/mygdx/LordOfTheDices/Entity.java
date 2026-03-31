package com.mygdx.LordOfTheDices;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

public abstract class Entity extends Sprite {
    /*Implementation of this class is incomplete */
    protected float maxHealth;
    protected float health;
    protected boolean isAlive;
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
    
    // Floating text fields
    private ArrayList<FloatingText> floatingTexts;
    
    // Damage effect fields
    protected boolean isTakingDamage = false;
    protected boolean isShaking = false;
    protected boolean isAttacking = false;
    protected float attackStateTime = 0;
    protected float damageTime = 0;
    protected float waitTime = 1f;
    protected float damageDuration = 0.5f;
    protected Color originalColor = Color.WHITE;

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
        floatingTexts = new ArrayList<>();
    }

    protected void initAnimationsFromAtlas(String regionName, int tileWidth, int tileHeight, int[] frameCounts) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("Atlas/Entities.atlas"));
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
        // Activate damage effect
        if (health > 0) {
            isTakingDamage = true;
            isShaking = false;
            damageTime = 0;
            originalColor = getColor().cpy();
        }
    }

    public void heal(float  heal) {
        health = Math.min(maxHealth, health + heal);
    }

    public void addMaxHealth(float  heal) {
        health += heal;
        maxHealth += heal;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void showFloatingText(String text, Color color) {
        float textX = getX() + getWidth() / 2;
        float textY = getY() + getHeight();
        floatingTexts.add(new FloatingText(text, textX, textY, color));
    }

    public abstract void update(float deltaTime);
    
    // Update damage effect - call this in your subclass update method
    protected void updateDamageEffect(float deltaTime) {
        if (isTakingDamage) {
            damageTime += deltaTime;
            if (damageTime >= waitTime) {
                isShaking = true;
                setColor(1f, 0.3f, 0.3f, 1);
                if (damageTime >= waitTime + damageDuration) {
                    isTakingDamage = false;
                    isShaking = false;
                    setColor(originalColor);
                }
            }
        }
    }

    // Update floating texts - call this in your subclass update method
    protected void updateFloatingTexts(float deltaTime) {
        floatingTexts.removeIf(text -> !text.isAlive());
        for (FloatingText text : floatingTexts) {
            text.update(deltaTime);
        }
    }

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
        TextureRegion frameToDraw = (currentFrame != null) ? currentFrame : currentAnimation.getKeyFrame(stateTime, true);
        if (isShaking) {
            // Apply shake effect when taking damage
            float shakeX = (float) (Math.random() * 8 - 4);
            float shakeY = (float) (Math.random() * 8 - 4);
            
            float origX = getX();
            float origY = getY();
            
            setX(origX + shakeX);
            setY(origY + shakeY);
            batch.draw(frameToDraw, getX(), getY(), getWidth(), getHeight());
            setX(origX);
            setY(origY);
        } 
        else {
            batch.draw(frameToDraw, getX(), getY(), getWidth(), getHeight());
        }

        // Draw floating texts
        for (FloatingText text : floatingTexts) {
            text.render(batch);
        }
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

    public void setEntity(TiledMap map) {
        MapLayer objectLayer = map.getLayers().get("Entities");
        MapObject playerObject = objectLayer.getObjects().get(name);
        if (playerObject instanceof TextureMapObject) {
            TextureMapObject texObj = (TextureMapObject) playerObject;
            this.setPosition(texObj.getX(), texObj.getY());
        }
    }
}
