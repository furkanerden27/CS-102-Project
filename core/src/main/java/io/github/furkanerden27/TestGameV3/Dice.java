package io.github.furkanerden27.TestGameV3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Dice {
    private final TextureRegion[] dices;
    private final Animation<TextureRegion> diceAnimation;
    private TextureRegion currentFrame;
    private float stateTime = 0;
    private boolean isRolling = true; 
    private final SpriteBatch batch;
    private final float posx;
    private final float posy;


    public Dice(SpriteBatch batch, float posx, float posy) {
        dices = TextureRegion.split(new Texture("dice.png"), 32, 32)[0];
        diceAnimation = new Animation<>(0.05f, dices);
        diceAnimation.setPlayMode(Animation.PlayMode.LOOP);
        this.batch = batch;
        this.posx = posx;
        this.posy = posy;
    }

    public void draw(float worldX, float worldY) {
        update(worldX, worldY);
        currentFrame = diceAnimation.getKeyFrame(stateTime);
        batch.draw(currentFrame, posx, posy); 
    }

    private void update(float worldX, float worldY) {
        handleInput(worldX, worldY);
        if (isRolling) {
            stateTime += Gdx.graphics.getDeltaTime();
        }
        currentFrame = diceAnimation.getKeyFrame(stateTime);
    }


    private void handleInput(float worldX, float worldY) {
        if (Gdx.input.justTouched() && 
            (worldX >= posx && worldX <= posx + currentFrame.getRegionWidth()) && 
            (worldY >= posy && worldY <= posy + currentFrame.getRegionHeight())) {
            isRolling = !isRolling;
            
            if (isRolling) {
                stateTime = (float) Math.random() * 10;
            }
        }
    }
}
