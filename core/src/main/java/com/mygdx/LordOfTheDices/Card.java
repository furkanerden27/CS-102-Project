package com.mygdx.LordOfTheDices;

import java.util.Locale;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Card extends Item {

    // 
    // ATLAS PROPERTIES
    //

    private static TextureAtlas atlas;

    /* Call once in AssetManager */
    public static void init(TextureAtlas cardAtlas) {
        atlas = cardAtlas;
    }

    /* Returns the 96×144 region for a suit+rank from cards.atlas. */
    private static TextureRegion getAtlasRegion(Suit suit, Rank rank) {
        if (atlas == null)
            throw new IllegalStateException("Call Card.init() first.");
        String suitName = suit.name().toLowerCase(Locale.ENGLISH);
        return atlas.findRegion("card-" + suitName + "-" + rank.getNumericValue());
    }

    // 
    // ENUMS
    // 

    public enum Suit { 
        SPADES, 
        HEARTS, 
        CLUBS, 
        DIAMONDS 
    }

    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6),
        SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(11), QUEEN(12), KING(13), ACE(14);

        private final int value;
        Rank(int v) { 
            this.value = v; 
        }

        /* Returns the numeric value of this rank (1–13). */
        public int getNumericValue() { 
            return value; 
        }
    }

    // 
    // PROPERTIES
    // 

    private final Suit suit;
    private final Rank rank;
    private TextureRegion cachedRegion;

    /* Effect scale: damage dealt / HP healed / buff-debuff magnitude. */
    private int power;

    /* Minimum total dice roll needed to play this card. */
    private int diceRequirement;

    /* True once the player clicks this card during their turn. */
    private boolean selected;

    /* True for SpecialCard face-cards that are consumed on use. */
    private final boolean expendable;

    /* Gold cost when buying from a merchant. */
    private int buyingValue;

    /* Gold received when selling to a merchant. */
    private int sellingValue;

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
        this.buyingValue     = computeBuyPrice();
        this.sellingValue    = computeSellPrice();
        this.description     = buildDescription();
        loadTexture();
    }

    /* Protected constructor for SpecialCard to set expendable = true. */
    protected Card(Suit suit, Rank rank, boolean expendable) {
        super(suit.name() + " " + rank.name());
        this.suit            = suit;
        this.rank            = rank;
        this.power           = calculateStartingPower();
        this.diceRequirement = rank.getNumericValue();
        this.expendable      = expendable;
        this.selected        = false;
        this.buyingValue     = computeBuyPrice();
        this.sellingValue    = computeSellPrice();
        this.description     = buildDescription();
        loadTexture();
    }

    // 
    // PUBLIC DISPLAY METHODS
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
            case SPADES:   effectDesc = "Deals "          + power + " damage";     break;
            case HEARTS:   effectDesc = "Gives "          + power + " shield";     break;
            case CLUBS:    effectDesc = "Debuffs enemy for " + power + " turns";   break;
            case DIAMONDS: effectDesc = "Buffs player for "  + power + " turns";   break;
            default:       effectDesc = "";
        }
        return effectDesc + "\nRequires roll: " + diceRequirement + (expendable ? "\n[Discarded when used]" : "");
    }

    private int computeBuyPrice()  { 
        return rank.getNumericValue() * 10; 
    }

    private int computeSellPrice() { 
        return computeBuyPrice() / 2; 
    }

    //
    // GAMEPLAY
    //

    public boolean checkPlay(int diceTotal) {
        return diceTotal >= diceRequirement;
    }

    /* Applies the card's effect based on its suit. */
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

    // will implement later to balance the gameplay
    //public void increasePower() {}
    //public void increaseDiceRequirement() {}
    //public void increaseBuyingValue() {}
    //public void increaseSellingValue() {}

    public void select() { 
        selected = true; 
    }
    public void unselect() { 
        selected = false; 
    }

    // 
    // GETTERS AND HELPERS
    // 

    public Suit    getSuit()              { return suit; }
    public Rank    getRank()              { return rank; }
    public int     getPower()             { return power; }
    public int     getDiceRequirement()   { return diceRequirement; }
    public boolean isSelected()           { return selected; }
    public boolean isExpendable()         { return expendable; }
    public int     getBuyingValue()       { return buyingValue; }
    public int     getSellingValue()      { return sellingValue; }
}

