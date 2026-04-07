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
    private int buyingValue;
    private TextureRegion textureRegion;

    public Relic(RelicType type) {
        super(type.name() + " RELIC");  //placeholder name, open to suggestions
        this.type = type;
        this.effectMagnitude = assignEffectMagnitude(type);
        this.isActive = false;
        this.buyingValue = (int) (Math.random() * 30) + 60;
        this.description = buildDescription();
        loadTexture();
    }

    public Relic(RelicType type, boolean isActive) {
        super(type.name() + " RELIC");
        this.type = type;
        this.effectMagnitude = assignEffectMagnitude(type);
        this.isActive = isActive;
        this.buyingValue = (int) (Math.random() * 30) + 60;
        this.description = buildDescription();
        loadTexture();
    }

    /** Reapplies effects of already-active relics (used after loading a save). */
    public void reapply(Player player) {
        if (!isActive) return;
        applyEffect(player);
    }

    /* activates the relic and applies its permanent effect on the player */
    public void apply(Player player) {
        if (isActive) return;
        isActive = true;
        applyEffect(player);
    }

    private void applyEffect(Player player) {
        switch (type) {
            case ARMOUR:
                player.addRelicArmourMultiplier(effectMagnitude);
                break;
            case BUFF:
                player.addRelicBuffIncrease(effectMagnitude);
                break;
            case DAMAGE:
                player.addRelicDamageMultiplier(effectMagnitude);
                break;
            case DEBUFF:
                player.addRelicDebuffIncrease(effectMagnitude);
                break;
            case DISCOUNT:
                player.addRelicDiscountMultiplier(effectMagnitude);
                break;
            case GOLD:
                player.addRelicGoldMultiplier(effectMagnitude);
                break;
            case HEALTH:
                player.addMaxHealth(player.getMaxHealth() * effectMagnitude);
                break;
            case POTION:
                player.addRelicPotionMultiplier(effectMagnitude);
                break;
            case REBIRTH:
                player.addRebirthCount();
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

    private float assignEffectMagnitude(RelicType type) {
        switch (type) {
            case DISCOUNT:     return 0.25f;
            case ARMOUR:       return 0.1f;
            case BUFF:         return 0.1f;
            case DAMAGE:       return 0.1f;
            case DEBUFF:       return 0.1f;
            case GOLD:         return 0.2f;
            case HEALTH:       return 0.25f;
            case POTION:       return 0.1f;
            case REBIRTH:      return 0.5f;
            default:           return 0f;
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

    public RelicType getRelicType() { return type; }
    public float getEffectMagnitude() { return effectMagnitude; }
    public boolean isActive(){ return isActive; }
    public int getBuyingValue(){ return buyingValue; }
    public TextureRegion getTextureRegion() { return textureRegion; }
}
