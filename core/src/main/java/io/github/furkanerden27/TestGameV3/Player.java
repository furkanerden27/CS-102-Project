package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Player extends Entity{
    /*Implementation of this class is incomplete */
    private final float GRAVITY = 1, FRICTION = 2.5f, ACC = 15, MAX_SPEED = 200, JUMP_SPEED = 70;
    
    private Texture playerTexture;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer distanceLayer;
    
    private Animation<TextureRegion> standingRight;
    private Animation<TextureRegion> standingLeft;
    private Animation<TextureRegion> walkingRight;
    private Animation<TextureRegion> walkingLeft;
    private Animation<TextureRegion> jumpingRight;
    private Animation<TextureRegion> jumpingLeft;
    
    private float speedX, speedY;
    private boolean isOnGround;

    private float attackModifier; // TODO bu hasar veren kartların hassarına eklenecek. Strengthen ve Weaken Efektleri bunu değiştirecek. 

    public Player(float health, float posX, float posY, TiledMap map) {
        super(health, posX, posY);
        /* TODO Animations of the player will be initalized after they are decided */
        
        playerTexture = new Texture("Entities/maincharacter.png");

        entityImages = TextureRegion.split(playerTexture, 32, 32);
        animations = new Animation[entityImages.length];

        setSize(24, 24);

        collisionLayer = (TiledMapTileLayer) map.getLayers().get("Ground");
        //distanceLayer = (TiledMapTileLayer) map.getLayers().get("Enemy");
        int[] frameCounts = {2, 2, 4, 8, 6, 8, 3, 8, 8};
        setAnimations(frameCounts);
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
    }

    

    // to handle input in PlayScreen
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
        /* limiting the speeds to a maximum value */
        
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
        /* deciding on the direction and the animation of the player */
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
    
    private boolean isCollision(float x, float y) {
        float playerWidth = getWidth();
        float playerHeight = getHeight();

        return checkTile(x, y) || checkTile(x + playerWidth, y) || // left and right down
            checkTile(x, y + playerHeight) || checkTile(x + playerWidth, y + playerHeight); // left and right up
    }

    private boolean checkTile(float x, float y) {
        int tileX = (int) (x / collisionLayer.getTileWidth());
        int tileY = (int) (y / collisionLayer.getTileHeight());
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);
        return (cell != null);
    }

    @Override
    public void update(float deltaTime) {
        handleInput();
        speedY -= GRAVITY; 
        

        float nextX = getX() + speedX * deltaTime;
        float nextY = getY() + speedY * deltaTime;

        /* updating the coorinates of the player 
        two controlls are necessary to keeping the other movement when hitting a wall */
        // x coordinates
        if (!isCollision(nextX, getY())) {
            setX(nextX);
        } 
        else {
            speedX = 0;
        }
        // y coordinates
        if (!isCollision(getX(), nextY)) {
            setY(nextY);
            isOnGround = false; 
        } 
        else {
            if (speedY < 0) {
                isOnGround = true;
            }
            speedY = 0;
        }

        stateTime += deltaTime;
    }
    
    public void setAttackModifier(float m){
        attackModifier = m;
    }
}
