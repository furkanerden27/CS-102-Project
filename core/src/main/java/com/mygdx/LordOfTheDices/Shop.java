package com.mygdx.LordOfTheDices;

import java.util.ArrayList;

import com.mygdx.LordOfTheDices.Card.Rank;
import com.mygdx.LordOfTheDices.Card.Suit;

public class Shop{

    private ArrayList<Card> cardsForSale;
    private ArrayList<Relic> relicsForSale;
    private Inventory inv;
    private int shopLevel;



    //doesn't need the arraylist inputs if a new game is started, can just pass null into it.
    public Shop(Inventory inv, ArrayList<Card> cFS, ArrayList<Relic> rFS, int shopLevel, boolean newGame){
        if(newGame){
            newGameShop();
            this.inv = inv;
        }
        else{
            this.inv = inv;
            this.shopLevel = shopLevel;
            cardsForSale = cFS;
            relicsForSale = rFS;
        }
    }

    //the default shop at the start of a game.
    private void newGameShop(){
        cardsForSale = new ArrayList<Card>();
        relicsForSale = new ArrayList<Relic>();

        for(int i = 2; i < 5; i++){
            cardsForSale.add(new Card(Suit.CLUBS, intToRank(i)));
            cardsForSale.add(new Card(Suit.DIAMONDS, intToRank(i)));
            cardsForSale.add(new Card(Suit.HEARTS, intToRank(i)));
            cardsForSale.add(new Card(Suit.SPADES, intToRank(i)));
        }
        for(int i = 11; i < 15; i++){
            cardsForSale.add(new Card(Suit.CLUBS, intToRank(i)));
            cardsForSale.add(new Card(Suit.DIAMONDS, intToRank(i)));
            cardsForSale.add(new Card(Suit.HEARTS, intToRank(i)));
            cardsForSale.add(new Card(Suit.SPADES, intToRank(i)));
        }

        shopLevel = 1;
    }

    public String buyCard(Card card){
        if(card.getBuyingValue() <= inv.getGold()){
            if(inv.addCard(card)){
                inv.setGold(inv.getGold() - card.getBuyingValue());
                return "Card purchased!";
            }
            else { return "You already have this card!"; } //these outputs are for the "dialogue" system of the shop.
        }
        else{ return "You don't have enough money!"; }
    }

    public String sellCard(Card card){
        inv.setGold(inv.getGold() + card.getSellingValue());
        inv.removeCard(card);
        return "Card sold!";
    }

    //Once the 2nd and the 4th bosses are defeated, the shop is upgraded.
    //each upgrade unlocks new cards for sale.
    public void shopUpgrade(){
        shopLevel++;
        for(int i = (shopLevel*3 - 1); i < (shopLevel*3 + 2); i++ ){
            cardsForSale.add(new Card(Suit.CLUBS, intToRank(i)));
            cardsForSale.add(new Card(Suit.DIAMONDS, intToRank(i)));
            cardsForSale.add(new Card(Suit.HEARTS, intToRank(i)));
            cardsForSale.add(new Card(Suit.SPADES, intToRank(i)));
        }
    }

    //a helper method that makes adding cards to an arraylist with for loops possible.
    //(might seem lengthy, but the alternative is worse.)
    private Rank intToRank(int i){
        Rank result = Rank.TWO;

        switch(i){
            case 2:
                result = Rank.TWO;
                break;
            case 3:
                result = Rank.THREE;
                break;
            case 4:
                result = Rank.FOUR;
                break;
            case 5:
                result = Rank.FIVE;
                break;
            case 6:
                result = Rank.SIX;
                break;
            case 7:
                result = Rank.SEVEN;
                break;
            case 8:
                result = Rank.EIGHT;
                break;
            case 9:
                result = Rank.NINE;
                break;
            case 10:
                result = Rank.TEN;
                break;
            case 11:
                result = Rank.JACK;
                break;
            case 12:
                result = Rank.QUEEN;
                break;
            case 13:
                result = Rank.KING;
                break;
            case 14:
                result = Rank.ACE;
                break;
            
        }
        return result;
    }
}