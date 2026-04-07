package com.mygdx.LordOfTheDices;

import java.util.Locale;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Card extends Item {

    private static TextureAtlas atlas;

    public static void init(TextureAtlas cardAtlas) {
        atlas = cardAtlas;
    }

    private static TextureRegion getAtlasRegion(Suit suit, Rank rank) {
        if (atlas == null)
            throw new IllegalStateException("Call Card.init() first.");
        String suitName = suit.name().toLowerCase(Locale.ENGLISH);
        int atlasValue = (rank == Rank.ACE) ? 1 : rank.getNumericValue();
        return atlas.findRegion("card-" + suitName + "-" + atlasValue);
    }

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

        public int getNumericValue() {
            return value;
        }
    }

    protected final Suit suit;
    protected final Rank rank;
    private TextureRegion cachedRegion;

    protected float power;
    private int diceRequirement;
    private boolean selected;
    protected final boolean expendable;
    private int buyingValue;
    private int sellingValue;

    public Card(Suit suit, Rank rank) {
        super(suit.name() + " " + rank.name());
        this.suit            = suit;
        this.rank            = rank;
        this.power           = calculateStartingPower();
        this.diceRequirement = calculateDiceRequirement();
        this.expendable      = false;
        this.selected        = false;
        this.buyingValue     = computeBuyPrice();
        this.sellingValue    = computeSellPrice();
        this.description     = buildDescription();
        loadTexture();
    }

    protected Card(Suit suit, Rank rank, boolean expendable) {
        super(suit.name() + " " + rank.name());
        this.suit            = suit;
        this.rank            = rank;
        this.power           = calculateStartingPower();
        this.diceRequirement = calculateDiceRequirement();
        this.expendable      = expendable;
        this.selected        = false;
        this.buyingValue     = computeBuyPrice();
        this.sellingValue    = computeSellPrice();
        this.description     = buildDescription();
        loadTexture();
    }

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

    private float calculateStartingPower() {
        int r = rank.getNumericValue();
        switch (suit) {
            case SPADES:   return 4 + r * 2f;
            case HEARTS:   return 3 + r * 1.5f;
            case CLUBS:    return 0.05f + r * 0.02f;
            case DIAMONDS: return 0.05f + r * 0.02f;
            default:       return 0;
        }
    }

    private int calculateDiceRequirement() {
        return Math.max(2, rank.getNumericValue() - 1);
    }

    private int getEffectDuration() {
        return 3;
    }

    public void apply(Player player, Mob mob) {
        switch (suit) {
            case SPADES:
                float damage = power * (1f + player.getAttackModifier()) * player.getRelicDamageMultiplier();
                mob.takeDamage(Math.max(0, damage));
                break;
            case HEARTS:
                player.heal(power * player.getRelicPotionMultiplier());
                break;
            case CLUBS:
                mob.addEffect(new Weaken(getEffectDuration(), power + player.getRelicDebuffIncrease()));
                break;
            case DIAMONDS:
                player.addEffect(new Strengthen(getEffectDuration(), power + player.getRelicBuffIncrease()));
                break;
        }
    }

    // TWO=10, FIVE=25, TEN=50, ACE=70
    private int computeBuyPrice() {
        return rank.getNumericValue() * 5;
    }

    private int computeSellPrice() {
        return computeBuyPrice() / 2;
    }

    private String buildDescription() {
        String effectDesc;
        switch (suit) {
            case SPADES:
                effectDesc = "Deals " + (int) power + " damage";
                break;
            case HEARTS:
                effectDesc = "Heals " + (int) power + " HP";
                break;
            case CLUBS:
                effectDesc = "Weakens enemy " + (int)(power * 100) + "% for " + (getEffectDuration() - 1) + " turns";
                break;
            case DIAMONDS:
                effectDesc = "Strengthens " + (int)(power * 100) + "% for " + (getEffectDuration() - 1) + " turns";
                break;
            default:
                effectDesc = "";
        }
        return effectDesc + "\nDice needed: " + diceRequirement
            + (expendable ? "\n[Single use]" : "");
    }

    public boolean checkPlay(int diceTotal) {
        return diceTotal >= diceRequirement;
    }

    public void select()   { selected = true; }
    public void unselect() { selected = false; }

    public Suit    getSuit()            { return suit; }
    public Rank    getRank()            { return rank; }
    public float   getPower()           { return power; }
    public int     getDiceRequirement() { return diceRequirement; }
    public boolean isSelected()         { return selected; }
    public boolean isExpendable()       { return expendable; }
    public int     getBuyingValue()     { return buyingValue; }
    public int     getSellingValue()    { return sellingValue; }
}
