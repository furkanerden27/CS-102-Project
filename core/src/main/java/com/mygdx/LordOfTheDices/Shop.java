package com.mygdx.LordOfTheDices;

import java.util.ArrayList;

import com.mygdx.LordOfTheDices.Card.Rank;
import com.mygdx.LordOfTheDices.Card.Suit;

public class Shop {

    private ArrayList<Card> cardsForSale;
    private ArrayList<Relic> relicsForSale;
    private Inventory inv;
    private int shopLevel;
    private Player player;

    public Shop(Inventory inv, Player player) {
        cardsForSale = new ArrayList<Card>();
        relicsForSale = new ArrayList<Relic>();

        for (int i = 2; i <= 14; i++) {
            cardsForSale.add(new Card(Suit.CLUBS, intToRank(i)));
            cardsForSale.add(new Card(Suit.DIAMONDS, intToRank(i)));
            cardsForSale.add(new Card(Suit.HEARTS, intToRank(i)));
            cardsForSale.add(new Card(Suit.SPADES, intToRank(i)));
        }
        for(int i = 11; i < 15; i++){
            cardsForSale.add(new SpecialCard(Suit.CLUBS, intToRank(i)));
            cardsForSale.add(new SpecialCard(Suit.DIAMONDS, intToRank(i)));
            cardsForSale.add(new SpecialCard(Suit.HEARTS, intToRank(i)));
            cardsForSale.add(new SpecialCard(Suit.SPADES, intToRank(i)));
        }
        relicsForSale.add(new Relic(RelicType.ARMOUR));
        relicsForSale.add(new Relic(RelicType.DAMAGE));
        relicsForSale.add(new Relic(RelicType.DEBUFF));
        relicsForSale.add(new Relic(RelicType.DISCOUNT));
        relicsForSale.add(new Relic(RelicType.REBIRTH));
        relicsForSale.add(new Relic(RelicType.BUFF));
        relicsForSale.add(new Relic(RelicType.GOLD));
        relicsForSale.add(new Relic(RelicType.HEALTH));
        relicsForSale.add(new Relic(RelicType.POTION));

        this.inv = inv;
        shopLevel = 1;
        this.player = player;
    }

    public Shop(Inventory inv, ArrayList<Card> cFS, ArrayList<Relic> rFS, int shopLevel, Player player) {
        this.inv = inv;
        this.shopLevel = shopLevel;
        cardsForSale = cFS;
        relicsForSale = rFS;
        this.player = player;
    }

    //Cards
    public String buyCard(Card card) {
        if (card.getBuyingValue() > inv.getGold()) {
            return "You don't have enough\nmoney!";
        }
        if (inv.hasCard(card)) {
            return "You already have this card!";
        }
        inv.addCard(card);
        inv.setGold(inv.getGold() - card.getBuyingValue());
        return "Thank you for your purchase!";
    }

    public String sellCard(Card card) {
        int suitCount = inv.getCardsBySuit(card.getSuit()).size();
        if (suitCount <= 1) {
            return "I can't take your last\n" + card.getSuit().name() + " card!";
        }
        boolean sold = inv.removeCard(card);
        if (sold) {
            inv.setGold(inv.getGold() + card.getSellingValue());
        }
        return sold ? "Alright, I'll take that!" : "Something went wrong...";
    }

    //Relics
    public String buyRelic(Relic relic){
        if(getFinalRelicBuyingValue(relic) <= inv.getGold()){
            if(inv.addRelic(relic)){
                inv.setGold(inv.getGold() - getFinalRelicBuyingValue(relic));
                return "Thank you for your purchase!";
            }
            else { return "You already have this relic!"; } //these outputs are for the "dialogue" system of the shop.
        }
        else{ return "You don't have enough\nmoney!"; }
    }

    //There's a discount relic, this exists to check for that
    public int getFinalCardBuyingValue(Card card){
        return (int)(((double)card.getBuyingValue()) * ((double)player.getRelicDiscountMultiplier()));
    }

    public int getFinalRelicBuyingValue(Relic relic){
        return (int)(((double)relic.getBuyingValue()) * ((double)player.getRelicDiscountMultiplier()));
    }
    
    public void shopUpgrade() {
        shopLevel++;
    }

    public Inventory getInv() {
        return inv;
    }

    public int getRelicAmount(){return relicsForSale.size();}

    public ArrayList<Card> getCardsForSale() {
        ArrayList<Card> available = new ArrayList<>();
        for (Card c : cardsForSale) {
            if (!inv.hasCard(c)) {
                available.add(c);
            }
        }
        return available;
    }

    public ArrayList<Relic> getRelicsForSale() {
        return relicsForSale;
    }

    private Rank intToRank(int i) {
        for (Rank r : Rank.values()) {
            if (r.getNumericValue() == i) return r;
        }
        return Rank.TWO;
    }
}
