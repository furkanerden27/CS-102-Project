package com.mygdx.LordOfTheDices;

public class SpecialCard extends Card {

    private final Effect effect;
    private final DiceCondition condition;

    public enum DiceCondition {
        NO_ONES,
        AT_LEAST_A_SIX,
        ALL_DIFFERENT,
        TOTAL_ABOVE_20
    }

    public SpecialCard(Suit suit, Rank rank) {
        super(suit, rank, true);
        this.effect = buildEffect();
        this.condition = assignCondition();
        description = buildDescription();
        
    }

    public boolean checkSpecialPlay(int[] diceValues) {
        switch (condition) {
            case NO_ONES:
                for (int v : diceValues) {
                    if (v == 1) return false;
                }
                return true;

            case AT_LEAST_A_SIX:
                for (int v : diceValues) {
                    if (v == 6) return true;
                }
                return false;

            case ALL_DIFFERENT:
                boolean[] seen = new boolean[7];
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
        } else {
            super.apply(player, mob);
        }
    }

    private Effect buildEffect() {
        float mag = getEffectMagnitude();
        switch (suit) {
            case SPADES:   return new Bleeding(3, mag);
            case HEARTS:   return new Lure(2, mag);
            case CLUBS:    return new Poison(3, mag);
            case DIAMONDS: return new Stun(1, mag);
            default:       return null;
        }
    }

    private float getEffectMagnitude() {
        switch (rank) {
            case JACK:   return 3f;
            case QUEEN:  return 5f;
            case KING:   return 8f;
            case ACE:    return 12f;
            default:     return 3f;
        }
    }

    private DiceCondition assignCondition() {
        switch (rank) {
            case JACK:   return DiceCondition.NO_ONES;
            case QUEEN:  return DiceCondition.AT_LEAST_A_SIX;
            case KING:   return DiceCondition.ALL_DIFFERENT;
            case ACE:    return DiceCondition.TOTAL_ABOVE_20;
            default:     return DiceCondition.NO_ONES;
        }
    }

    private String conditionText() {
        switch (condition) {
            case NO_ONES:        return "No 1s on dice";
            case AT_LEAST_A_SIX: return "At least one 6";
            case ALL_DIFFERENT:  return "All dice different";
            case TOTAL_ABOVE_20: return "Dice total > 20";
            default:             return "";
        }
    }

    private String buildDescription() {
        String effectDesc;
        switch (suit) {
            case SPADES:
                effectDesc = "Bleeding: " + (int) effect.baseValue + " dmg/turn for " + effect.durationLeft + " turns";
                break;
            case HEARTS:
                effectDesc = "Lures enemy for " + effect.durationLeft + " turns";
                break;
            case CLUBS:
                effectDesc = "Poison: " + (int) effect.baseValue + " dmg/turn for " + effect.durationLeft + " turns";
                break;
            case DIAMONDS:
                effectDesc = "Stuns enemy for " + effect.durationLeft + " turn";
                break;
            default:
                effectDesc = "";
        }
        return effectDesc + "\nCondition: " + conditionText()
            + (expendable ? "\n[Single use]" : "");
    }

    public Effect getEffect()          { return effect; }
    public DiceCondition getCondition() { return condition; }
}
