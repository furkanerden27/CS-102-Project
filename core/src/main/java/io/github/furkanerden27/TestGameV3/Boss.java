package io.github.furkanerden27.TestGameV3;

public abstract class Boss extends Mob {

    public Boss(float posX, float posY) {
        super(posX, posY);
    }

    public abstract void specialAttack(Player player);
    
}
