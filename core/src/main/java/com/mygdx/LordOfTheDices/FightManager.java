package com.mygdx.LordOfTheDices;

import java.util.ArrayList;
import java.util.Collections;

public class FightManager {

    public enum FightState {
        PLAYER_PICK_CARD,
        PLAYER_ROLL,
        MOB_TURN,
        FIGHT_END
    }

    private Player player;
    private Mob mob;
    private ScreenManager screenManager;

    private FightState state;
    private boolean isPlayerTurn;
    private Card selectedCard;

    private ArrayList<Card> spadesCards;
    private ArrayList<Card> clubsCards;
    private ArrayList<Card> diamondsCards;
    private ArrayList<Card> heartsCards;

    public ArrayList<Dice> dices;

    private static final float PLAYER_BATTLE_SIZE = 72f;
    private static final float MOB_BATTLE_SIZE = 140f;
    private static final float BATTLE_Y = 180f;
    public static final float PLAYER_X = 180f;
    public float playerBattleY, mobBattleX, mobBattleY;
    public float playerLastX;
    public float playerLastY;
    private float playerOrigW, playerOrigH;
    private float mobOrigW, mobOrigH;

    private float mobTurnTimer;
    private static final float MOB_TURN_DELAY = 2.5f;

    private float endFightTimer;
    private static final float END_FIGHT_DELAY = 2.0f;
    private boolean endFightStarted;

    private String lastMessage;
    private float messageTimer;
    private static final float MESSAGE_DURATION = 1.5f;

    public FightManager(Player player, Mob mob, ScreenManager screenManager) {
        this.player = player;
        this.mob = mob;
        player.setLocked(true);

        state = FightState.PLAYER_PICK_CARD;
        isPlayerTurn = true;
        selectedCard = null;
        endFightStarted = false;
        mobTurnTimer = 0;
        endFightTimer = 0;
        lastMessage = null;
        messageTimer = 0;

        dices = player.getInventory().getDice();
        this.screenManager = screenManager;
        screenManager.showBattleScreen(this);

        if (player != null) {
            playerLastX = player.getX();
            playerLastY = player.getY();
            playerOrigW = player.getWidth();
            playerOrigH = player.getHeight();
            float pScale = PLAYER_BATTLE_SIZE / Math.max(playerOrigW, playerOrigH);
            float pW = playerOrigW * pScale;
            float pH = playerOrigH * pScale;
            player.setSize(pW, pH);
            playerBattleY = BATTLE_Y;
            player.setPosition(PLAYER_X, playerBattleY);
        }
        if (mob != null) {
            mobOrigW = mob.getWidth();
            mobOrigH = mob.getHeight();
            float mScale = MOB_BATTLE_SIZE / Math.max(mobOrigW, mobOrigH);
            float mW = mobOrigW * mScale;
            float mH = mobOrigH * mScale;
            mob.setSize(mW, mH);
            mobBattleX = 520f;
            mobBattleY = BATTLE_Y;
            mob.setPosition(mobBattleX, mobBattleY);
        }

        determineHands();
    }

    private void determineHands() {
        spadesCards = player.getInventory().getCardsBySuit(Card.Suit.SPADES);
        clubsCards = player.getInventory().getCardsBySuit(Card.Suit.CLUBS);
        diamondsCards = player.getInventory().getCardsBySuit(Card.Suit.DIAMONDS);
        heartsCards = player.getInventory().getCardsBySuit(Card.Suit.HEARTS);

        if (spadesCards.size() > 5) spadesCards = getRandomCards(spadesCards);
        if (clubsCards.size() > 5) clubsCards = getRandomCards(clubsCards);
        if (diamondsCards.size() > 5) diamondsCards = getRandomCards(diamondsCards);
        if (heartsCards.size() > 5) heartsCards = getRandomCards(heartsCards);
    }

    private ArrayList<Card> getRandomCards(ArrayList<Card> list) {
        ArrayList<Card> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, 5));
    }

    public void updateFight(float delta) {
        player.update(delta);
        mob.update(delta);
        for (Dice d : dices) {
            d.update(delta);
        }

        if (lastMessage != null) {
            messageTimer -= delta;
            if (messageTimer <= 0) lastMessage = null;
        }

        if (state == FightState.MOB_TURN) {
            mobTurnTimer += delta;
            if (mobTurnTimer >= MOB_TURN_DELAY) executeMobTurn();
        }

        if (state == FightState.FIGHT_END && endFightStarted) {
            endFightTimer += delta;
            if (endFightTimer >= END_FIGHT_DELAY) finishFight();
        }

        if (state != FightState.FIGHT_END) {
            if (!player.isAlive || !mob.isAlive) {
                state = FightState.FIGHT_END;
                isPlayerTurn = false;
                endFightStarted = true;
                endFightTimer = 0;
                showMessage(player.isAlive ? "Victory!" : "Defeated...");
            }
        }
    }

    public void actSelectedCard(Card card) {
        if (state != FightState.PLAYER_PICK_CARD) return;

        selectedCard = card;
        state = FightState.PLAYER_ROLL;
        showMessage("Now roll the dice!");
    }

    public void rollAllDice() {
        if (state != FightState.PLAYER_ROLL) return;

        for (Dice d : dices) {
            if (!d.isLocked()) {
                d.startRolling();
                d.roll();
            }
        }

        resolveCardPlay();
    }

    private void resolveCardPlay() {
        if (selectedCard == null) {
            startMobTurn();
            return;
        }

        int diceTotal = getDiceTotal();
        boolean success;

        if (selectedCard instanceof SpecialCard) {
            success = ((SpecialCard) selectedCard).checkSpecialPlay(getDiceValues());
        } else {
            success = selectedCard.checkPlay(diceTotal);
        }

        if (success) {
            player.playBattleAttack();
            selectedCard.apply(player, mob);

            if (mob instanceof Envy) {
                ((Envy) mob).setLastPlayedCard(selectedCard);
            }

            if (selectedCard.isExpendable()) {
                player.getInventory().removeCard(selectedCard);
                removeCardFromHand(selectedCard);
            }

            switch (selectedCard.getSuit()) {
                case SPADES:
                    mob.showFloatingText("-" + (int) selectedCard.getPower(), com.badlogic.gdx.graphics.Color.RED);
                    break;
                case HEARTS:
                    player.showFloatingText("+" + (int) selectedCard.getPower(), com.badlogic.gdx.graphics.Color.GREEN);
                    break;
                case CLUBS:
                    mob.showFloatingText("Weakened!", com.badlogic.gdx.graphics.Color.PURPLE);
                    break;
                case DIAMONDS:
                    player.showFloatingText("Strengthened!", com.badlogic.gdx.graphics.Color.CYAN);
                    break;
            }
            showMessage("Card played!");
        } else {
            if (selectedCard instanceof SpecialCard) {
                showMessage("Condition failed! Turn wasted.");
            } else {
                showMessage("Need " + selectedCard.getDiceRequirement() + ", got " + diceTotal + "! Turn wasted.");
            }
            player.showFloatingText("Failed!", com.badlogic.gdx.graphics.Color.RED);
        }

        selectedCard = null;
        startMobTurn();
    }

    public void diceClicked(Dice clickedDice) {
        if (state != FightState.PLAYER_ROLL) return;
        clickedDice.setLocked(!clickedDice.isLocked());
    }

    public void skipTurn() {
        if (state == FightState.PLAYER_PICK_CARD || state == FightState.PLAYER_ROLL) {
            showMessage("Turn skipped");
            selectedCard = null;
            startMobTurn();
        }
    }

    public int getDiceTotal() {
        int total = 0;
        for (Dice d : dices) total += d.getValue();
        return total;
    }

    public int[] getDiceValues() {
        int[] values = new int[dices.size()];
        for (int i = 0; i < dices.size(); i++) values[i] = dices.get(i).getValue();
        return values;
    }

    private void startMobTurn() {
        isPlayerTurn = false;
        state = FightState.MOB_TURN;
        mobTurnTimer = 0;
    }

    private void executeMobTurn() {
        mob.applyEffects();

        if (mob.isAlive && !mob.isStunned) {
            mob.specialAttack(player);
        } else if (mob.isStunned) {
            mob.showFloatingText("Stunned!", com.badlogic.gdx.graphics.Color.YELLOW);
            mob.setStun(false);
        }

        startPlayerTurn();
    }

    private void startPlayerTurn() {
        player.applyEffects();
        if (!player.isAlive || !mob.isAlive) return;

        isPlayerTurn = true;
        selectedCard = null;

        for (Dice d : dices) {
            d.canRoll = true;
            d.setLocked(false);
        }

        mob.setEffectiveAttackDamage(mob.getBaseAttackDamage());
        player.setAttackModifier(0);

        state = FightState.PLAYER_PICK_CARD;
    }

    private void finishFight() {
        player.setSize(playerOrigW, playerOrigH);
        mob.setSize(mobOrigW, mobOrigH);
        mob.removeAllEffects();
        player.removeAllEffects();

        if (player.isAlive) {
            player.setPosition(playerLastX, playerLastY);
            player.addGold(mob.getGold());
            player.setLocked(false);
            screenManager.goBack();
        } else {
            player.setLocked(false);
            screenManager.restartLevel();
        }
    }

    private void removeCardFromHand(Card card) {
        spadesCards.remove(card);
        clubsCards.remove(card);
        diamondsCards.remove(card);
        heartsCards.remove(card);
    }

    private void showMessage(String msg) {
        lastMessage = msg;
        messageTimer = MESSAGE_DURATION;
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public ArrayList<Card> getHand(Card.Suit selectedSuit) {
        switch (selectedSuit) {
            case SPADES:   return spadesCards;
            case CLUBS:    return clubsCards;
            case DIAMONDS: return diamondsCards;
            case HEARTS:   return heartsCards;
            default: throw new AssertionError();
        }
    }

    public boolean isPlayerTurn() { return isPlayerTurn; }
    public FightState getState() { return state; }
    public String getLastMessage() { return lastMessage; }
    public Player getPlayer() { return player; }
    public Mob getMob() { return mob; }
}
