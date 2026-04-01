package com.mygdx.LordOfTheDices;

public class Relic extends Item {

    public enum RelicType {
        DISCOUNT,
        MAXHEALTH,
        GOLD_BOOST,
        DAMAGE_BOOST
    }

    private final RelicType type;
    private final float effectMagnitude;
    private boolean isActive;
    private final int buyingValue;

    public Relic(String name, RelicType type, float effectMagnitude, int buyingValue) {
        super(name);
        this.type = type;
        this.effectMagnitude = effectMagnitude;
        this.isActive = false;
        this.buyingValue = buyingValue;
        this.description = buildDescription();
    }

    /** Activates the relic and applies its permanent effect on the player. */
    public void apply(Player player) {
        if (isActive) return;
        isActive = true;

        switch (type) {
            case MAXHEALTH:
                float bonus = player.maxHealth * effectMagnitude;
                player.addMaxHealth(bonus);
                break;
            case DAMAGE_BOOST:
                player.setAttackModifier(effectMagnitude);
                break;
            case DISCOUNT:
            case GOLD_BOOST:
                // These are checked passively by Shop / gold logic
                break;
        }
    }

    private String buildDescription() {
        switch (type) {
            case DISCOUNT:     return "Reduces shop prices by " + (int)(effectMagnitude * 100) + "%";
            case MAXHEALTH:    return "Increases max health by " + (int)(effectMagnitude * 100) + "%";
            case GOLD_BOOST:   return "Earn " + (int)(effectMagnitude * 100) + "% more gold";
            case DAMAGE_BOOST: return "Increases attack damage by " + (int)(effectMagnitude * 100) + "%";
            default:           return "";
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    protected void loadTexture() {
        // Relic textures will be loaded from atlas when assets are ready
    }

    public RelicType getType()        { return type; }
    public float getEffectMagnitude() { return effectMagnitude; }
    public boolean isActive()         { return isActive; }
    public int getBuyingValue()       { return buyingValue; }
}
