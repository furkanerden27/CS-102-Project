package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Player extends Entity {
    /*Implementation of this class is incomplete */
    private final float GRAVITY = 1, FRICTION = 2.5f, ACC = 25, MAX_SPEED = 200, JUMP_SPEED = 100;
    
    
    private Texture playerTexture;
    private TiledMapTileLayer collisionLayer;

    private Animation<TextureRegion> standing;
    
    private float speedX, speedY;
    private boolean isOnGround;

    public Player(double health, float posX, float posY, TiledMap map) {
        super(health, posX, posY);
        /* TODO Animations of the player will be initalized after they are decided */
        
        playerTexture = new Texture("maincharacter.png");

        entityImages = TextureRegion.split(playerTexture, 32, 32);
        animations = new Animation[entityImages.length];

        setRegion(entityImages[0][0]); // to test the code for now
        setSize(32, 32);

        collisionLayer = (TiledMapTileLayer) map.getLayers().get("Ground");
        setAnimations();
        gold = 0;
        speedX = 0;
        speedY = 0;
        isOnGround = false;
        
    }

    private void setAnimations() {
        for (int i = 0; i < entityImages.length; i++) {
            animations[i] = new Animation<>(0.1f, entityImages[i]); // frame duration can be changed
        }
        standing = animations[0];
    }


    private void handleInput() {

        /* getting the input to update the speeds (S is not implemented yet)*/
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            speedX -= ACC;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            speedX += ACC;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) && isOnGround) {
            speedY = JUMP_SPEED;
            isOnGround = false;
            
        }
        //--------------------------------------------------------------------------
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
        //---------------------------------------------------------------------------------------
        /* deciding on the direction of the player */
        if(speedX == 0) {
            direction = "still";
        }
        else if(speedX > 0) {
            direction = "right";
        }
        else {
            direction = "left";
        }
        //-----------------------------------------------------------------------------------------
        
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
    public void move() {
        // TODO Auto-generated method stub
    }

    @Override
    public void update(float deltaTime) {
        handleInput();
        speedY -= GRAVITY; 
        

        float nextX = getX() + speedX * deltaTime;
        float nextY = getY() + speedY * deltaTime;

        /* updating the coorinates of the player 
        two controlls are necessary to keeping the other movement when hitting a wall*/
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
    
}
