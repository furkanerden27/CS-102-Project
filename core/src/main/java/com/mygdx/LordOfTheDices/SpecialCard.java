package com.mygdx.LordOfTheDices;

public class SpecialCard extends Card {
    
    private final Effect effect;
    private final DiceCondition condition;

    // conditions might change later to balance the game
    public enum DiceCondition {
        ALL_EVEN,       // All dice must show even numbers
        ALL_ODD,        // All dice must show odd numbers
        ALL_DIFFERENT,  // Dice values form a consecutive sequence
        TOTAL_ABOVE_20  // Sum of all dice must exceed 20
    }

    public SpecialCard(Suit suit, Rank rank) {
        super(suit, rank, true);
        this.effect = buildEffect();
        description = buildDescription();
        this.condition = assignCondition();
    }

    /* checks if the dice values meet this cards unique condition.*/
    public boolean checkSpecialPlay(int[] diceValues) {
        switch (condition) {
            case ALL_EVEN:
                for (int v : diceValues) {
                    if (v % 2 != 0) return false;
                }
                return true;

            case ALL_ODD:
                for (int v : diceValues) {
                    if (v % 2 == 0) return false;
                }
                return true;

            case ALL_DIFFERENT:
                boolean[] seen = new boolean[7]; // 1-6
                for (int v : diceValues) {
                    if (v < 1 || v > 6 || seen[v]) return false;
                    seen[v] = true;
                }
                return true;

            case TOTAL_ABOVE_20:
                int sum = 0;
                for (int v : diceValues) sum += v;
                return sum > 20;

            default:
                return false;
        }
    }

    @Override
    public void apply(Player player, Mob mob) {
        if (effect != null) {
            mob.addEffect(effect);
        } 
        else {
            super.apply(player, mob);
        }
    }

    private Effect buildEffect() {
        switch (suit) {
            case SPADES:
                return new Bleeding(4, getEffectMagnitude());
            case HEARTS:
                return new Lure(4, getEffectMagnitude());
            case CLUBS:
                return new Poison(4, getEffectMagnitude());
            case DIAMONDS:
                return new Stun(4, getEffectMagnitude());
            default:
                return null;
        }
    }

    private float getEffectMagnitude() {
        switch (rank) {
            case JACK:   return 0.25f;
            case QUEEN:  return 0.5f;
            case KING:   return 0.75f;
            case ACE:    return 1.0f;
            default:     return 0.25f; // default fallback
        }
    }

    private DiceCondition assignCondition() {
        switch (rank) {
            case JACK:   return DiceCondition.ALL_EVEN;
            case QUEEN: return DiceCondition.ALL_ODD;
            case KING:  return DiceCondition.ALL_DIFFERENT;
            case ACE:  return DiceCondition.TOTAL_ABOVE_20;
            default:    return DiceCondition.ALL_EVEN; // default fallback
        }
    }

    private String buildDescription() {
        String effectDesc;
        switch (suit) {
            case SPADES:   effectDesc = "Deals bleeding damage for " + effect.durationLeft + " turns";     break;
            case HEARTS:   effectDesc = "Lures the enemy for " + effect.durationLeft + " turns";     break;
            case CLUBS:    effectDesc = "Poisons the enemy for " + effect.durationLeft + " turns";   break;
            case DIAMONDS: effectDesc = "Stuns the enemy for " + effect.durationLeft + " turns";   break;
            default:       effectDesc = "";
        }
        return effectDesc + "\nRequired rolls: " + condition.name() + (expendable ? "\n[Discarded when used]" : "");
    }

    public Effect getEffect()           { return effect; }
    public DiceCondition getCondition()  { return condition; }

}
