package com.mygdx.LordOfTheDices;

import java.util.ArrayList;

public class FightManager {

    private Player player;
    private Mob mob;
    private BattleScreen screen1;
    private ScreenManager screenManager;

    private boolean isPlayerTurn;
    

    public FightManager(Player player, Mob mob, ScreenManager screenManager){
        this.player = player;
        this.mob = mob;
        isPlayerTurn = true;
        this.screenManager = screenManager;
        screen1 = screenManager.showBattleScreen(this);
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
        endFight();
    }

    private void playerTurn(){
        //TODO
        isPlayerTurn = true;
    }
    private void mobTurn(){
        //TODO
        isPlayerTurn = false;
    }

    public void updateFight(){
        //TODO
    }

    public void diceClicked(Dice clickedDice){
        //TODO Rolls the clicked dice
        System.out.println("Zar Atildi");
    }

    public void rollAllDice(){
        //TODO rolls all dice
    }
    

    public void actSelectedCard(Card selectedCard){
        //TODO secilen kartin etkilerini uygulayacak. 
        System.out.println(selectedCard.name);
        System.out.println(selectedCard.description);
    }
    public Card[] getHand(Card.Suit selectedSuit){
        //TODO player'in savasa soktugu kartlari cekecek, inventorden 
        Card[] cards = new Card[]{new Card(selectedSuit, Card.Rank.SIX), new Card(selectedSuit, Card.Rank.SEVEN), new Card(selectedSuit, Card.Rank.EIGHT), new Card(selectedSuit, Card.Rank.NINE), new Card(selectedSuit, Card.Rank.TEN)};
        return cards;
    }
    public Dice[] getDices(){
        //TODO player'in envanterinden dice alinacak
        //return player.getInventory().getDice();
        return new Dice[6];
    }

    public boolean isPlayerTurn(){
        return isPlayerTurn;
    }

    public ArrayList<Item> getSpritesOnScreen(){
        return null;//TODO
    }

    private void endFight(){
        mob.removeAllEffects();
        player.removeAllEffects();
        player.addGold(mob.getGold());
        //TODO
    }
    
}