package io.github.furkanerden27.TestGameV3;

import java.util.ArrayList;

public class FightManager {

    private Player player;
    private Mob mob;
    private BattleScreen1 screen1;
    

    public FightManager(Player player, Mob mob){
        this.player = player;
        this.mob = mob;
        
        screen1 = new BattleScreen1();
        //TODO SCREENMANAGER'E BATTLLESCREEN1'e GEÇİRTÇEN
    }

    private void fightLoop(){
        // TODO
        while(player.isAlive && mob.isAlive){
            player.applyEffects();
            mob.applyEffects();
            if(player.isAlive)
                playerTurn();
            if(mob.isAlive)
                mobTurn();
            mob.setEffectiveAttackDamage(mob.getBaseAttackDamage());
            player.setAttackModifier(0);
        }
    }

    private void playerTurn(){
        //TODO
    }
    private void mobTurn(){
        //TODO
    }
    
}
