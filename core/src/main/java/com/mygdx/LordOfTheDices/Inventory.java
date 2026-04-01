package com.mygdx.LordOfTheDices;

import java.util.ArrayList;
import java.util.Collections;

import com.mygdx.LordOfTheDices.Card.Rank;
import com.mygdx.LordOfTheDices.Card.Suit;

public class Inventory {

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

    /** Creates a default new-game inventory. */
    public Inventory() {
        dice = new ArrayList<>();
        cards = new ArrayList<>();
        relics = new ArrayList<>();

        cards.add(new Card(Suit.CLUBS, Rank.TWO));
        cards.add(new Card(Suit.SPADES, Rank.TWO));
        cards.add(new Card(Suit.DIAMONDS, Rank.TWO));
        cards.add(new Card(Suit.HEARTS, Rank.TWO));
        Collections.sort(cards);

        dice.add(new Dice("31"));
        gold = 100;
    }

    // ── Dice ─────────────────────────────────────────────────────────────

    public void addDice() {
        dice.add(new Dice("32"));
    }

    public ArrayList<Dice> getDice() { return dice; }

    public Dice getDice(int i) { return dice.get(i); }

    public int getDiceCount() { return dice.size(); }

    // ── Cards ────────────────────────────────────────────────────────────

    /** Adds a card. Face cards (rank > 10) cannot be duplicated. Returns false if duplicate. */
    public boolean addCard(Card card) {
        if (card.getRank().getNumericValue() > 10 && hasCard(card)) {
            return false;
        }
        cards.add(card);
        Collections.sort(cards);
        return true;
    }

    /** Removes a card. Rank TWO cards (starter cards) cannot be removed. */
    public boolean removeCard(Card card) {
        if (card.getRank().getNumericValue() == 2) {
            return false;
        }
        return cards.remove(card);
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

    /** Returns all cards of a given suit. */
    public ArrayList<Card> getCardsBySuit(Suit suit) {
        ArrayList<Card> result = new ArrayList<>();
        for (Card c : cards) {
            if (c.getSuit() == suit) {
                result.add(c);
            }
        }
        return result;
    }

    // ── Relics ───────────────────────────────────────────────────────────

    /** Adds a relic. Duplicate relics are not allowed. */
    public boolean addRelic(Relic relic) {
        if (hasRelic(relic)) {
            return false;
        }
        relics.add(relic);
        return true;
    }

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

    // ── Gold ─────────────────────────────────────────────────────────────

    public int getGold() { return gold; }

    public void setGold(int gold) { this.gold = gold; }

    public void addGold(int amount) { this.gold += amount; }

    /** Returns true if the player can afford the cost. */
    public boolean canAfford(int cost) { return gold >= cost; }

    /** Spends gold. Returns false if insufficient funds. */
    public boolean spendGold(int amount) {
        if (gold < amount) return false;
        gold -= amount;
        return true;
    }
}
