package com.mygdx.LordOfTheDices;
//
// NO ASSETS YET, WILL UPDATE IT LATER
//
public class Relic extends Item {
    
    //types can change later on
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

    /* activates the relic and applies its permanent effect on the player */
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
                //TODO
                break;
            case GOLD_BOOST:
                //TODO - IDK
                break;
        }
    }

    private String buildDescription() {
        switch (type) {
            case DISCOUNT:     
                return "Reduces shop prices by " + (int)(effectMagnitude * 100) + "%";
            case MAXHEALTH:    
                return "Increases max health by " + (int)(effectMagnitude * 100) + "%";
            case GOLD_BOOST:   
                return "Earn " + (int)(effectMagnitude * 100) + "% more gold";
            case DAMAGE_BOOST: 
                return "Increases attack damage by " + (int)(effectMagnitude * 100) + "%";
            default:           
                return "";
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    protected void loadTexture() {
        // TODO
    }

    public RelicType getRelicType()        { return type; }
    public float getEffectMagnitude() { return effectMagnitude; }
    public boolean isActive()         { return isActive; }
    public int getBuyingValue()       { return buyingValue; }
}
