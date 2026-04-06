package com.mygdx.LordOfTheDices;

import java.util.ArrayList;

import com.mygdx.LordOfTheDices.Card.Rank;
import com.mygdx.LordOfTheDices.Card.Suit;

public class Shop {

    private ArrayList<Card> cardsForSale;
    private ArrayList<Relic> relicsForSale;
    private Inventory inv;
    private int shopLevel;

    public Shop(Inventory inv) {
        cardsForSale = new ArrayList<Card>();
        relicsForSale = new ArrayList<Relic>();

        for (int i = 2; i <= 14; i++) {
            cardsForSale.add(new Card(Suit.CLUBS, intToRank(i)));
            cardsForSale.add(new Card(Suit.DIAMONDS, intToRank(i)));
            cardsForSale.add(new Card(Suit.HEARTS, intToRank(i)));
            cardsForSale.add(new Card(Suit.SPADES, intToRank(i)));
        }

        this.inv = inv;
        shopLevel = 1;
    }

    public Shop(Inventory inv, ArrayList<Card> cFS, ArrayList<Relic> rFS, int shopLevel) {
        this.inv = inv;
        this.shopLevel = shopLevel;
        cardsForSale = cFS;
        relicsForSale = rFS;
    }

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

    public void shopUpgrade() {
        shopLevel++;
    }

    public Inventory getInv() {
        return inv;
    }

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
