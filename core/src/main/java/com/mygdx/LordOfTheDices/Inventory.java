package com.mygdx.LordOfTheDices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.mygdx.LordOfTheDices.Card.Rank;
import com.mygdx.LordOfTheDices.Card.Suit;

public class Inventory implements Comparator<Card>{

    private ArrayList<Dice> dice;
    private ArrayList<Card> cards;
    private ArrayList<Relic> relics;
    private int gold;

    /** For loading a saved game. */
    public Inventory(ArrayList<Dice> dice, ArrayList<Card> cards, ArrayList<Relic> relics, int gold) {
        this.dice = dice;
        this.cards = cards;
        this.relics = relics;
        this.gold = gold;
    }

    // Creates a default new-game inventory. 
    public Inventory() {
        dice = new ArrayList<>();
        cards = new ArrayList<>();
        relics = new ArrayList<>();

        cards.add(new Card(Suit.CLUBS, Rank.TWO));
        cards.add(new Card(Suit.SPADES, Rank.TWO));
        cards.add(new Card(Suit.DIAMONDS, Rank.TWO));
        cards.add(new Card(Suit.HEARTS, Rank.TWO));
        Collections.sort(cards, this);

        dice.add(new Dice("31"));
        gold = 100;
    }

    //Dice ------------------------------------------------------------------------------------

    public void addDice() {
        dice.add(new Dice("32"));
    }

    // public ArrayList<Dice> getDice() { return dice; }

    // public Dice getDice(int i) { return dice.get(i); }

    // public int getDiceCount() { return dice.size(); }

    //Cards --------------------------------------------------------------------------------------

    // Adds a card. Noral cards (rank < 11) can't be stacked. Returns false if duplicate. 
    public boolean addCard(Card card) {
        if (card.getRank().getNumericValue() < 11 && hasCard(card)) {
            return false;
        }
        cards.add(card);
        Collections.sort(cards, this);
        return true;
    }

    //Removes a card. Rank TWO cards (starter cards) can't be removed.
    public boolean removeCard(Card card) {
        if (card.getRank().getNumericValue() == 2) {
            return false;
        }
        return cards.remove(card);
    }

    //Checks if a card already exists in the inventory.
    public boolean hasCard(Card card) {
        for (Card c : cards) {
            if (c.getSuit() == card.getSuit() && c.getRank() == card.getRank()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Card> getCards() { return cards; }

    public Card getCard(int i) { return cards.get(i); }

    public int getCardCount() { return cards.size(); }

    //Returns all cards of a given suit.
    public ArrayList<Card> getCardsBySuit(Suit suit) {
        ArrayList<Card> result = new ArrayList<>();
        for (Card c : cards) {
            if (c.getSuit() == suit) {
                result.add(c);
            }
        }
        return result;
    }

    //Relics-------------------------------------------------

    /** Adds a relic. Relics can't be stacked. */
    public boolean addRelic(Relic relic) {
        if (hasRelic(relic)) {
            return false;
        }
        relics.add(relic);
        return true;
    }

    //checks if a relic already exists in the inventory
    public boolean hasRelic(Relic relic) {
        for (Relic r : relics) {
            if (r.getRelicType() == relic.getRelicType()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Relic> getRelics() { return relics; }

    public Relic getRelic(int i) { return relics.get(i); }

    public int getRelicCount() { return relics.size(); }

    //Gold------------------------------------------------------
    public int getGold() { return gold; }

    public void setGold(int gold) { this.gold = gold; }

    public void addGold(int amount) { this.gold += amount; }


    public boolean canAfford(int cost) { return gold >= cost; }

    // Spends gold. Returns false if insufficient funds.
    public boolean spendGold(int amount) {
        if (gold < amount) return false;
        gold -= amount;
        return true;
    }

    //The sort method uses this. This is needed to properly sort cards so that
    //They can be shown in a specific way in the inventory screen.
    public int compare(Card card1, Card card2) {
        if(card1.getSuit() != card2.getSuit()){
            return card1.getSuit().getNumericValue() - card2.getSuit().getNumericValue();
        }
        else{
            return card1.getRank().getNumericValue() - card2.getRank().getNumericValue();
        }
    }
}
