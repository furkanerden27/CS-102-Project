package com.mygdx.LordOfTheDices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.mygdx.LordOfTheDices.Card.Rank;
import com.mygdx.LordOfTheDices.Card.Suit;

public class Inventory implements Comparator<Card> {

    private ArrayList<Dice> dice;
    private ArrayList<Card> cards;
    private ArrayList<Relic> relics;
    private int gold;

    // For loading a saved game. 
    public Inventory(ArrayList<Dice> dice, ArrayList<Card> cards, ArrayList<Relic> relics, int gold) {
        this.dice = dice;
        this.cards = cards;
        this.relics = relics;
        this.gold = gold;
    }

    // Creates a default new game inventory. 
    public Inventory() {
        dice = new ArrayList<Dice>(6);
        cards = new ArrayList<Card>();
        relics = new ArrayList<Relic>();

        cards.add(new Card(Suit.CLUBS, Rank.TWO));
        cards.add(new Card(Suit.SPADES, Rank.TWO));
        cards.add(new Card(Suit.DIAMONDS, Rank.TWO));
        cards.add(new Card(Suit.HEARTS, Rank.TWO));
        Collections.sort(cards, this); //look at the bottom for explanation
        dice.add(new Dice("Dice of Greed"));
        gold = 100; //can be changed
    }

    // Dice

    public void addDice(String str) {
        dice.add(new Dice(str));
    }

    public ArrayList<Dice> getDice() { return dice; }

    public Dice getDice(int i) { return dice.get(i); }

    public int getDiceCount() { return dice.size(); }

    // Cards

    // Adds a card. Cards that are not special cards can't be stacked. Returns false if duplicate. 
    public boolean addCard(Card card) {
        if (card.getRank().getNumericValue() < 11 && hasCard(card)) {
            return false;
        }
        cards.add(card);
        Collections.sort(cards, this);
        return true;
    }

    public boolean removeCard(Card card) {
        int suitCount = getCardsBySuit(card.getSuit()).size();
        if (suitCount <= 1) {
            return false;
        }

        for (Card c : cards) {
            if (c.getSuit() == card.getSuit() && c.getRank() == card.getRank()) {
                cards.remove(c);
                return true;
            }
        }

        return false;
    }

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

    //Relics

    //Adds a relic. Relics can't be stacked.
    // public boolean addRelic(Relic relic) {
    //     if (hasRelic(relic)) {
    //         return false;
    //     }
    //     relics.add(relic);
    //     return true;
    // }

    // public boolean hasRelic(Relic relic) {
    //     for (Relic r : relics) {
    //         if (r.getType() == relic.getType()) {
    //             return true;
    //         }
    //     }
    //     return false;
    // }

    public ArrayList<Relic> getRelics() { return relics; }

    public Relic getRelic(int i) { return relics.get(i); }

    public int getRelicCount() { return relics.size(); }

    //Gold

    public int getGold() { return gold; }

    public void setGold(int gold) { this.gold = gold; }

    public void addGold(int amount) { this.gold += amount; }

    /** Returns true if the player can afford the cost. */
    public boolean canAfford(int cost) { return gold >= cost; }

    /** Spends gold. Returns false if insufficient funds. */
    public boolean spendGold(int amount) {
        if (gold < amount) {return false;}
        gold -= amount;
        return true;
    }


    //Other

    //The sort method uses this. This is needed to properly sort cards so that
    //They can be shown in a specific way in the inventory screen.
    public int compare(Card card1, Card card2) {
        if(card1.getRank() != card2.getRank()){
            return card1.getRank().getNumericValue() - card2.getRank().getNumericValue();      
        }
        else{
            Suit[] suitArr = new Suit[4];
            suitArr[0] = Card.Suit.SPADES;
            suitArr[1] = Card.Suit.HEARTS;
            suitArr[2] = Card.Suit.CLUBS;
            suitArr[3] = Card.Suit.DIAMONDS;

            int cardVal1 = 0;
            int cardVal2 = 0;

            for(int i = 0; i < 4; i++){
                if(suitArr[i] == card1.getSuit()){
                    cardVal1 = i;
                }
                if(suitArr[i] == card2.getSuit()){
                    cardVal2 = i;
                }
            }

            return cardVal1 - cardVal2;
            
        }
    }
    //Returns five random cards of a suit
    //Requested for the battle system
    public ArrayList<Card> getFiveCards(Suit suit) {
        ArrayList<Card> list = getCardsBySuit(suit);
        Collections.shuffle(list);
        ArrayList<Card> fiveList = new ArrayList<Card>();
        for(int i = 0; i < list.size(); i++){
            if(i < 5){
                fiveList.add(list.get(i));
            }
        }
        return fiveList;
    }

}

