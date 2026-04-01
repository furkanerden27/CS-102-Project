package com.mygdx.LordOfTheDices;

public class SpecialCard extends Card {
    
    private final Effect effect;
    private final DiceCondition condition;

    // conditions might change later to balance the game
    public enum DiceCondition {
        ALL_EVEN,       // All dice must show even numbers
        ALL_ODD,        // All dice must show odd numbers
        ALL_SAME,       // All dice must show the same value
        ALL_DIFFERENT,       // Dice values form a consecutive sequence
        TOTAL_ABOVE_20  // Sum of all dice must exceed 20
    }

    public SpecialCard(Suit suit, Rank rank, Effect effect, DiceCondition condition) {
        super(suit, rank, true);
        this.effect = effect;
        this.condition = condition;
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

            case ALL_SAME:
                for (int i = 1; i < diceValues.length; i++) {
                    if (diceValues[i] != diceValues[0]) return false;
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
            effect.applyEffect(mob);
        } else {
            super.apply(player, mob);
        }
    }

    public Effect getEffect()           { return effect; }
    public DiceCondition getCondition()  { return condition; }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n[Special: " + condition.name() + "]";
    }
}
