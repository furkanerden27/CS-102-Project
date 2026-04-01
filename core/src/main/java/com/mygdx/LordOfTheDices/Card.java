package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Card extends Item implements Comparable<Card> {

     @Override
     public int compareTo(Card other) {
         if (this.rank.getNumericValue() != other.rank.getNumericValue()) {
             return Integer.compare(this.rank.getNumericValue(), other.rank.getNumericValue());
         } 
         else {
             return this.suit.compareTo(other.suit);
         }
     }

    //
    // ATLAS — set once via Card.init(atlas) from Core.create()
    //

    private static TextureAtlas atlas;

    /** Call once after AssetManager finishes loading. */
    public static void init(TextureAtlas cardsAtlas) {
        atlas = cardsAtlas;
    }

    private static TextureRegion getAtlasRegion(Suit suit, Rank rank) {
        if (atlas == null)
            throw new IllegalStateException("Call Card.init() in Core.create() first.");
        return atlas.findRegion("card-" + suit.name().toLowerCase() + "-" + rank.getNumericValue());
    }

    //
    // ENUMS
    //

    public enum Suit {
        SPADES(1), 
        HEARTS(2), 
        CLUBS(3), 
        DIAMONDS(4); 

        private final int value;
        Suit(int v) { 
            this.value = v; 
        }

        /* Returns the numeric value of this rank (1–13). */
        public int getNumericValue() { 
            return value; 
        }
    }

    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6),
        SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(11), QUEEN(12), KING(13), ACE(14);

        private final int value;
        Rank(int v) { this.value = v; }
        public int getNumericValue() { return value; }
    }

    //
    // PROPERTIES
    //

    private final Suit suit;
    private final Rank rank;
    private final int power;
    private final int diceRequirement;
    private final boolean expendable;
    private final int buyingValue;
    private final int sellingValue;
    private boolean selected;

    // cached — set once in loadTexture(), returned by getTextureRegion()
    private TextureRegion cachedRegion;

    //
    // Constructor
    //

    public Card(Suit suit, Rank rank) {
        super(suit.name() + " " + rank.name());
        this.suit            = suit;
        this.rank            = rank;
        this.power           = calculateStartingPower();
        this.diceRequirement = rank.getNumericValue();
        this.expendable      = false;
        this.selected        = false;
        this.buyingValue     = rank.getNumericValue() * 10;
        this.sellingValue    = buyingValue / 2;
        this.description     = buildDescription();
        loadTexture();
    }

    /** Protected constructor for SpecialCard to set expendable = true. */
    protected Card(Suit suit, Rank rank, boolean expendable) {
        super(suit.name() + " " + rank.name());
        this.suit            = suit;
        this.rank            = rank;
        this.power           = calculateStartingPower();
        this.diceRequirement = rank.getNumericValue();
        this.expendable      = expendable;
        this.selected        = false;
        this.buyingValue     = rank.getNumericValue() * 10;
        this.sellingValue    = buyingValue / 2;
        this.description     = buildDescription();
        loadTexture();
    }

    //
    // ITEM OVERRIDES
    //

    @Override
    public void loadTexture() {
        TextureRegion region = getAtlasRegion(suit, rank);
        if (region != null) {
            setRegion(region);
            setSize(region.getRegionWidth(), region.getRegionHeight());
            cachedRegion = region;
        }
    }

    @Override
    public String getDescription() {
        return description;
    }


    public TextureRegion getTextureRegion() {
        return cachedRegion;
    }

    //
    // PRIVATE HELPERS
    //

    private int calculateStartingPower() {
        switch (suit) {
            case SPADES:   return rank.getNumericValue() * 2;
            case HEARTS:   return rank.getNumericValue();
            case CLUBS:    return rank.getNumericValue();
            case DIAMONDS: return rank.getNumericValue();
            default:       return 0;
        }
    }

    private String buildDescription() {
        String effectDesc;
        switch (suit) {
            case SPADES:   effectDesc = "Deals "             + power + " damage";     break;
            case HEARTS:   effectDesc = "Gives "             + power + " shield";     break;
            case CLUBS:    effectDesc = "Debuffs enemy for " + power + " turns";      break;
            case DIAMONDS: effectDesc = "Buffs player for "  + power + " turns";      break;
            default:       effectDesc = "";
        }
        return effectDesc + "\nRequires roll: " + diceRequirement + (expendable ? "\n[Discarded when used]" : "");
    }

    //
    // GAMEPLAY
    //

    public boolean checkPlay(int diceTotal) {
        return diceTotal >= diceRequirement;
    }

    /**
     * Applies the card's effect based on its suit.
     * SPADES/CLUBS affect the mob, HEARTS/DIAMONDS affect the player.
     */
    public void apply(Player player, Mob mob) {
        switch (suit) {
            case SPADES:
                mob.takeDamage(power);
                break;
            case HEARTS:
                player.heal(power);
                break;
            case CLUBS:
                mob.addEffect(new Weaken(power, 1));
                break;
            case DIAMONDS:
                player.addEffect(new Strengthen(power, 1));
                break;
        }
    }

    @Override
    public int compareTo(Card o) {
        if(suit.ordinal() == o.getSuit().ordinal()){
            return Integer.compare(rank.getNumericValue(), o.getRank().getNumericValue());
        }
        return Integer.compare(suit.ordinal(), o.getSuit().ordinal());
    }
    
    // will implement later to balance the gameplay
    //public void increasePower() {}
    //public void increaseDiceRequirement() {}
    //public void increaseBuyingValue() {}
    //public void increaseSellingValue() {}

    //
    // GETTERS
    //

    public Suit    getSuit()            { return suit; }
    public Rank    getRank()            { return rank; }
    public int     getPower()           { return power; }
    public int     getDiceRequirement() { return diceRequirement; }
    public boolean isSelected()         { return selected; }
    public boolean isExpendable()       { return expendable; }
    public int     getBuyingValue()     { return buyingValue; }
    public int     getSellingValue()    { return sellingValue; }

    // Sorts by suit first, then by rank ascending.
    @Override
    public int compareTo(Card other) {
        int suitCmp = this.suit.ordinal() - other.suit.ordinal();
        if (suitCmp != 0) return suitCmp;
        return this.rank.ordinal() - other.rank.ordinal();
    }
}
