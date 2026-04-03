package com.mygdx.LordOfTheDices;

import java.util.Locale;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Relic extends Item {
   
    
    private static TextureAtlas atlas;

    /* Call once in AssetManager */
    public static void init(TextureAtlas relicAtlas) {
        atlas = relicAtlas;
    }

    private static TextureRegion getAtlasRegion(RelicType type) {
        if (atlas == null)
            throw new IllegalStateException("Call Relic.init() first.");
        String typeName = type.name().toLowerCase(Locale.ENGLISH);
        return atlas.findRegion(typeName + "-relic");
    }

    public enum RelicType {
        ARMOUR,
        BUFF,
        DAMAGE,
        DEBUFF,
        DISCOUNT,
        GOLD,
        HEALTH,
        POTION,
        REBIRTH
    }

    private final RelicType type;
    private final float effectMagnitude;
    private boolean isActive;
    private final int buyingValue;
    private TextureRegion textureRegion;

    //I WILL REMOVE EFFECT MAGNITUDE AND BUYING VALUE LATER, THEY ARE JUST FOR TESTING PURPOSES
    public Relic(String name, RelicType type, float effectMagnitude, int buyingValue) {
        super(name);
        this.type = type;
        this.effectMagnitude = effectMagnitude;
        this.isActive = false;
        this.buyingValue = buyingValue;
        this.description = buildDescription();
        loadTexture();
    }

    /* activates the relic and applies its permanent effect on the player */
    public void apply(Player player) {
        if (isActive) return;
        
        isActive = true;
        switch (type) {
            case ARMOUR: // will change later
                float bonus = player.maxHealth * effectMagnitude;
                player.addMaxHealth(bonus);
                break;
            case BUFF:
                player.setAttackModifier(effectMagnitude);
                break;
            case DAMAGE:
                //TODO
                break;
            case DEBUFF:
                //TODO
                break;
            case DISCOUNT:
                //TODO - IDK
                break;
            case GOLD:
                //TODO
                break;
            case HEALTH:
                //TODO
                break;
            case POTION:
                //TODO
                break;
            case REBIRTH:
                //TODO
                break;  
        }
    }

    private String buildDescription() {
        switch (type) {
            case DISCOUNT:     
                return "Reduces shop prices by " + (int)(effectMagnitude * 100) + "%";
            case ARMOUR:    
                return "Reduces incoming damage by " + (int)(effectMagnitude * 100) + "%";
            case BUFF:   
                return "Increases buff cards' magnitude by " + (int)(effectMagnitude * 100) + "%";
            case DAMAGE: 
                return "Increases attack damage by " + (int)(effectMagnitude * 100) + "%";
            case DEBUFF: 
                return "Increases debuff cards' magnitude by " + (int)(effectMagnitude * 100) + "%";
            case GOLD:
                return "Gain " + (int)(effectMagnitude * 100) + "% more gold from battles";
            case HEALTH:
                return "Increases max health by " + (int)(effectMagnitude * 100) + "%";
            case POTION:
                return "Increases healing effectiveness by " + (int)(effectMagnitude * 100) + "%";
            case REBIRTH:
                return "Revive with " + (int)(effectMagnitude * 100) + "% of max health after dying";
            default:           
                return "";
        }
    }

    @Override
    protected void loadTexture() {
        TextureRegion region = getAtlasRegion(type);
        if (region != null) {
            setRegion(region);
            setSize(region.getRegionWidth(), region.getRegionHeight());
            textureRegion = region;
        }
    }

    public RelicType getRelicType()        { return type; }
    public float getEffectMagnitude() { return effectMagnitude; }
    public boolean isActive()         { return isActive; }
    public int getBuyingValue()       { return buyingValue; }
    public TextureRegion getTextureRegion() { return textureRegion; }
}
