package io.github.furkanerden27.TestGameV3;

public class BasicMob extends Mob {

    public static int level = 0;
    private final float CONSTANT = 20f;
    private final int MIN = 3;
    private final int MAX = 6; // can be changed

    public BasicMob(float posX, float posY) {
        super(posX, posY);
        goldDropped = MIN * level + (int)(Math.random() * (MAX - MIN) * level + 1);
        level++;
        addMaxHealth(level * CONSTANT);
    }

    @Override
    public void update(float deltaTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
    
}
