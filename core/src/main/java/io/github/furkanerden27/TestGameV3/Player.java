package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Entity {
    /*Implementation od this class is incomplete */
    private final float GRAVITY = 10, FRICTION = 10, ACC = 10, MAX_SPEED = 200, FLIGTH_TIME = 5;
    private float speedX, speedY;

    public Player(TextureRegion[] EntityImages, double health, float posX, float posY) {
        super(EntityImages, health, posX, posY);
        /* TODO Images and the animations of the player will be initalized after they are decided */
        gold = 0;
        speedX = 0;
        speedY = 0;
    }

    private void handleInput() {
        /* getting the input to update the speeds (S is not implemented yet)*/
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            speedX -= ACC;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            speedX += ACC;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            speedY += GRAVITY * FLIGTH_TIME / 2;
        }
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
        if(Math.abs(speedY) > GRAVITY) {
            speedY -= GRAVITY * Math.signum(speedY);
        } 
        else {
            speedY = 0;
        }
    }

    private boolean isCollision(float nextX, float nextY) {
        /*TODO will be done when tiled map is made */
        return false;
    }

    @Override
    public void draw(SpriteBatch batch) {
        // TODO Auto-generated method stub
        
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

        if (!isCollision(nextX, nextY)) {
            setPosition(nextX, nextY);
        } 
        else {
            speedY = 0;
        }

        stateTime += deltaTime;
    }
    
}
