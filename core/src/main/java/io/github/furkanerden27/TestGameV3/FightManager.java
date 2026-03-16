package io.github.furkanerden27.TestGameV3;

import java.util.ArrayList;

public class FightManager {

    private Player player;
    private Mob mob;
    private BattleScreen1 screen1;
    private ArrayList<Effect> effectsInFight;
    private ArrayList<Effect> effectsRemoveList;

    public FightManager(Player player, Mob mob){
        this.player = player;
        this.mob = mob;
        effectsInFight = new ArrayList<>();
        effectsRemoveList= new ArrayList<>();
        screen1 = new BattleScreen1();
        //TODO SCREENMANAGER'E BATTLLESCREEN1'e GEÇİRTÇEN
    }

    private void fightLoop(){
        // TODO
        if(player.isAlive && mob.isAlive){
            playTurn();
            removeListedEffects();
        }
    }
    private void playTurn(){
        //TODO
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
}
