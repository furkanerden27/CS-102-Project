package com.mygdx.LordOfTheDices;

import java.util.ArrayList;

public class FightManager {

    private Player player;
    private Mob mob;
    private BattleScreen screen1;
    private ScreenManager screenManager;

    private boolean isPlayerTurn;
    private boolean canRollAll;

    private ArrayList<Card> spadesCards;
    private ArrayList<Card> clubsCards;  
    private ArrayList<Card> diamondsCards;
    private ArrayList<Card> heartsCards;

    public ArrayList<Dice> dices;

    public FightManager(Player player, Mob mob, ScreenManager screenManager){
        this.player = player;
        this.mob = mob;
        isPlayerTurn = true;
        canRollAll = true;
        dices = player.getInventory().getDice();
        this.screenManager = screenManager;
        screen1 = screenManager.showBattleScreen(this);
        
        determineHands();
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

    private void determineHands(){
        
        spadesCards = player.getInventory().getCardsBySuit(Card.Suit.SPADES);
        clubsCards = player.getInventory().getCardsBySuit(Card.Suit.CLUBS);
        diamondsCards = player.getInventory().getCardsBySuit(Card.Suit.DIAMONDS);
        heartsCards = player.getInventory().getCardsBySuit(Card.Suit.HEARTS);

        if(spadesCards.size() > 5){
            spadesCards = getRandomCards(spadesCards);
        }
        if(clubsCards.size() > 5){
            clubsCards = getRandomCards(clubsCards);
        }
        if(diamondsCards.size() > 5){
            diamondsCards = getRandomCards(diamondsCards);
        }
        if(heartsCards.size() > 5){
            heartsCards = getRandomCards(heartsCards);
        }
    }

    private ArrayList<Card> getRandomCards(ArrayList<Card> list){
        int[] choosedIndexes = new int[5];
        int random;
        ArrayList<Card> result = new ArrayList<>(5);
        boolean canContinue;
        for(int i = 0; i < 5; i++){
            random = (int)(Math.random() * list.size());
            canContinue = true;
            for(int m = 0; m < i; m++){
                if(random == choosedIndexes[m]){
                    canContinue = false;
                }
            }
            if(canContinue){
                choosedIndexes[i] = random;
                result.set(i, list.get(random));
            }
            else{
                i--;
            }
        }

        return result;
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
        if(allDiceRolled()){
            canRollAll = false;
        }
    }

    public boolean allDiceRolled(){
        for(int i = 0; i < dices.size(); i++){
            if(dices.get(i).canRoll){
                return false;
            }
        }
        return true;
    }
    public void rollAllDice(){
        //TODO rolls all dice
        if(canRollAll){}
        canRollAll = false;
    }
    

    public void actSelectedCard(Card selectedCard){
        //TODO secilen kartin etkilerini uygulayacak. 
        System.out.println(selectedCard.name);
        System.out.println(selectedCard.description);
    }

    public ArrayList<Card> getHand(Card.Suit selectedSuit){
        switch (selectedSuit) {
            case SPADES:
                return spadesCards;
            case CLUBS:
                return clubsCards;
            case DIAMONDS:
                return diamondsCards;
            case HEARTS:
                return heartsCards;
            default:
                throw new AssertionError();
        }
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