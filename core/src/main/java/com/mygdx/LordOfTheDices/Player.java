package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Player extends Entity{
    private final float GRAVITY = 1f, FRICTION = 5f, ACC = 15, MAX_SPEED = 200, JUMP_SPEED = 70;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer WallMob;
    private TiledMapTileLayer WallBoss;
    private boolean isMobDefeated = false;
    private boolean isBossDefeated = false;
    private Inventory inventory;
    private boolean isLocked = false;

    private Animation<TextureRegion> standingRight;
    private Animation<TextureRegion> standingLeft;
    private Animation<TextureRegion> walkingRight;
    private Animation<TextureRegion> walkingLeft;
    private Animation<TextureRegion> jumpingRight;
    private Animation<TextureRegion> jumpingLeft;
    private Animation<TextureRegion> attackRight;
    private Animation<TextureRegion> attackLeft;
    private Animation<TextureRegion> dieRight;
    private Animation<TextureRegion> dieLeft;


    private float speedX, speedY;
    private boolean isOnGround;
    private float physicsAccumulator = 0f;
    private static final float FIXED_STEP = 1f / 60f;

    private float attackModifier;

    private boolean isDead = false;
    private float deathStateTime = 0;
    private boolean isBattleAttacking = false;
    private float battleAttackTimer = 0;
    private static final float BATTLE_ATTACK_DURATION = 0.6f;
    private static final float BATTLE_LUNGE = 50f;

    private float relicArmourMultiplier = 1f;
    private float relicDamageMultiplier = 1f;
    private float relicGoldMultiplier = 1f;
    private float relicPotionMultiplier = 1f;
    private float relicBuffIncrease = 0f;
    private float relicDebuffIncrease= 0f;
    private float relicDiscountMultiplier = 1f;
    private int relicRebirthCount = 0;

    public Player(float health, float posX, float posY, TiledMap map) {
        this(health, posX, posY, map, new Inventory());
    }

    public Player(float health, float posX, float posY, TiledMap map, Inventory inventory) {
        super(health, posX, posY);
        initAnimationsFromAtlas("maincharacter",
            32, 32, new int[]{2, 2, 4, 8, 6, 8, 3, 8, 8});
        setSize(24, 24);
        currentAnimation = standingRight;

        collisionLayer = (TiledMapTileLayer) map.getLayers().get("Ground");
        WallMob = (TiledMapTileLayer) map.getLayers().get("WallMob");
        WallBoss = (TiledMapTileLayer) map.getLayers().get("WallBoss");
        this.inventory = inventory;
        goldDropped = 0;
        speedX = 0;
        speedY = 0;

        isOnGround = false;
    }

    @Override
    protected void setAnimations(int[] frameCounts) {
        super.setAnimations(frameCounts);

        standingRight = animations[0];
        standingLeft = getFlippedAnimation(standingRight);
        walkingRight = animations[3];
        walkingLeft = getFlippedAnimation(walkingRight);
        jumpingRight = animations[5];
        jumpingLeft = getFlippedAnimation(jumpingRight);
        dieRight = animations[7];
        dieLeft = getFlippedAnimation(dieRight);
        attackRight = animations[8];
        attackLeft = getFlippedAnimation(attackRight);
    }

    public void moveLeft() {
        speedX -= ACC;
    }

    public void moveRight() {
        speedX += ACC;
    }

    public void jump() {
        if (isOnGround) {
            speedY = JUMP_SPEED;
            isOnGround = false;
        }
    }

    private void handleInput() {
        if(Math.abs(speedX) > MAX_SPEED) {
            speedX = MAX_SPEED * Math.signum(speedX);
        }
        if(Math.abs(speedY) > MAX_SPEED) {
            speedY = MAX_SPEED * Math.signum(speedY);
        }
        if(Math.abs(speedX) > FRICTION) {
            speedX -= FRICTION * Math.signum(speedX);
        }
        else {
            speedX = 0;
        }
        handleAnimation();
    }

    private void handleAnimation() {
        if(speedX > 0) {
            direction = "right";
        }
        else if(speedX < 0) {
            direction = "left";
        }

        if (!isOnGround) {
            if (direction.equals("right")) {
                currentAnimation = jumpingRight;
            }
            else {
                currentAnimation = jumpingLeft;
            }
        }
        else if (speedX == 0) {
            if (direction.equals("right")) {
                currentAnimation = standingRight;
            }
            else {
                currentAnimation = standingLeft;
            }
        }
        else if(direction.equals("right")) {
            currentAnimation = walkingRight;
        }
        else {
            currentAnimation = walkingLeft;
        }
    }

    private boolean isCollision(float x, float y, TiledMapTileLayer layer) {
        float playerWidth = getWidth();
        float playerHeight = getHeight();

        return checkTile(x, y, layer) || checkTile(x + playerWidth, y, layer) ||
            checkTile(x, y + playerHeight, layer) || checkTile(x + playerWidth, y + playerHeight, layer);
    }

    private boolean checkTile(float x, float y, TiledMapTileLayer layer) {
        int tileX = (int) (x / layer.getTileWidth());
        int tileY = (int) (y / layer.getTileHeight());
        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);
        return (cell != null);
    }

    @Override
    public void update(float deltaTime) {

        if (!isAlive && !isDead) {
            isDead = true;
            deathStateTime = 0;
            currentAnimation = direction.equals("right") ? dieRight : dieLeft;
        }
        if (isDead) {
            deathStateTime += deltaTime;
            if (deathStateTime >= currentAnimation.getAnimationDuration()) {
                currentFrame = currentAnimation.getKeyFrame(currentAnimation.getAnimationDuration(), false);
            } else {
                stateTime += deltaTime;
                currentFrame = currentAnimation.getKeyFrame(stateTime, false);
            }
        }
        else {
            if (isBattleAttacking) {
                battleAttackTimer += deltaTime;
                if (battleAttackTimer >= BATTLE_ATTACK_DURATION) {
                    isBattleAttacking = false;
                    translate(-BATTLE_LUNGE, 0);
                    currentAnimation = standingRight;
                }
            }
            else if (isLocked) {
                currentAnimation = standingRight;
            }

            if(!isLocked){
                physicsAccumulator += deltaTime;
                while (physicsAccumulator >= FIXED_STEP) {
                    handleInput();
                    speedY -= GRAVITY;

                    float nextX = getX() + speedX * FIXED_STEP;
                    float nextY = getY() + speedY * FIXED_STEP;

                    if (!isCollision(nextX, getY(), collisionLayer))
                    {
                        setX(nextX);
                    }
                    else {
                        speedX = 0;
                    }
                    if (!isCollision(getX(), nextY, collisionLayer))
                    {
                        setY(nextY);
                        isOnGround = false;
                    }
                    else {
                        if (speedY < 0) {
                            isOnGround = true;
                        }
                        speedY = 0;
                    }
                    physicsAccumulator -= FIXED_STEP;
                }
            }
            stateTime += deltaTime;
            currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        }
        updateDamageEffect(deltaTime);
    }

    public void addGold(int gold){
        inventory.addGold((int)(gold * relicGoldMultiplier));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setLocked(boolean t){
        isLocked = t;
    }

    public void playBattleAttack() {
        if (!isBattleAttacking) {
            isBattleAttacking = true;
            battleAttackTimer = 0;
            currentAnimation = attackRight;
            translate(BATTLE_LUNGE, 0);
        }
    }

    public float getAttackModifier(){
        return attackModifier;
    }

    public void setAttackModifier(float m){
        attackModifier = m;
    }

    @Override
    public void takeDamage(float damage) {
        damage *= relicArmourMultiplier;
        if(relicRebirthCount > 0) {
            health = Math.max(0, health - damage);
            if(health == 0) {
                relicRebirthCount--;
                health = maxHealth * 0.5f;
                isAlive = true;
            }
            if (health > 0) {
                isTakingDamage = true;
                isShaking = false;
                damageTime = 0;
                originalColor = getColor().cpy();
            }
        }
        else {
            super.takeDamage(damage);
        }
    }

    public void addRelicArmourMultiplier(float multiplier) {
        this.relicArmourMultiplier -= multiplier;
    }

    public void addRelicDamageMultiplier(float multiplier) {
        this.relicDamageMultiplier += multiplier;
    }

    public void addRelicGoldMultiplier(float multiplier) {
        this.relicGoldMultiplier += multiplier;
    }

    public void addRelicPotionMultiplier(float multiplier) {
        this.relicPotionMultiplier += multiplier;
    }

    public void addRelicBuffIncrease(float increase) {
        this.relicBuffIncrease += increase;
    }

    public void addRelicDebuffIncrease(float increase) {
        this.relicDebuffIncrease += increase;
    }

    public void addRelicDiscountMultiplier(float multiplier) {
        this.relicDiscountMultiplier -= multiplier;
    }

    public void addRebirthCount() {
        this.relicRebirthCount++;
    }

    public float getRelicBuffIncrease() {
        return relicBuffIncrease;
    }

    public float getRelicDebuffIncrease() {
        return relicDebuffIncrease;
    }

    public float getRelicDamageMultiplier() {
        return relicDamageMultiplier;
    }

    public float getRelicGoldMultiplier() {
        return relicGoldMultiplier;
    }

    public float getRelicPotionMultiplier() {
        return relicPotionMultiplier;
    }

    public float getRelicDiscountMultiplier() {
        return relicDiscountMultiplier;
    }

    public int getRebirthCount() {
        return relicRebirthCount;
    }
}
