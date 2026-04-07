package com.mygdx.LordOfTheDices;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.LordOfTheDices.Card.Suit;

public class BattleScreen implements Screen {

    private Stage stage;
    private FightManager manager;
    private FitViewport viewport;

    private Card.Suit selectedSuit;

    private Texture cardSpades, cardClubs, cardHearts, cardDiamonds;
    private Texture background, lockedDice, rollAllTexture, arrowTexture, inverseArrowTexture;
    private TextureAtlas effectsAtlas;

    private Image inverseArrowImage, arrowImage;

    private Label descLabel;
    private Table cardsTable1, cardsTable2;
    private Table[] cardSlots;
    private Container<Table> cardsWrapper;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont uiFont;
    private BitmapFont smallFont;

    private Image[] diceImages;

    private static final float EFFECT_ICON_SIZE = 18f;
    private static final float HP_BAR_WIDTH = 80f;
    private static final float HP_BAR_HEIGHT = 6f;

    public BattleScreen(Assets assets, FightManager manager, int sizeX, int sizeY) {
        viewport = new FitViewport(sizeX, sizeY);
        stage = new Stage(viewport);
        this.manager = manager;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        uiFont = new BitmapFont();
        uiFont.getData().setScale(1.2f);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(0.8f);

        cardSpades = assets.getTexture(Assets.TEX_CARD_SPADES);
        cardClubs = assets.getTexture(Assets.TEX_CARD_CLUBS);
        cardHearts = assets.getTexture(Assets.TEX_CARD_HEARTS);
        cardDiamonds = assets.getTexture(Assets.TEX_CARD_DIAMONDS);
        background = assets.getTexture(Assets.TEXTURE_FIGHT_BG);
        lockedDice = assets.getTexture(Assets.TEX_LOCKED_DICE);
        rollAllTexture = assets.getTexture(Assets.TEX_ROLL_ALL_BTN);
        arrowTexture = assets.getTexture(Assets.TEX_ARROW);
        inverseArrowTexture = assets.getTexture(Assets.TEX_INVERSE_ARROW);
        effectsAtlas = Effect.effects;

        diceImages = new Image[6];
        for (int i = 0; i < 6; i++) {
            diceImages[i] = new Image(lockedDice);
        }

        cardSlots = new Table[]{new Table(), new Table(), new Table(), new Table(), new Table(), new Table()};
        Gdx.input.setInputProcessor(stage);

        createUI();
    }

    private void createUI() {
        cardsWrapper = new Container<>();
        cardsTable2 = new Table();
        cardsTable1 = new Table();
        Table middleWrapper = new Table();
        Table diceTable = new Table();
        Table mainTable = new Table();

        mainTable.setFillParent(true);

        Image backGroundImage = new Image(background);
        backGroundImage.setFillParent(true);

        stage.addActor(backGroundImage);
        stage.addActor(mainTable);

        LabelStyle descStyle = new LabelStyle(smallFont, Color.WHITE);
        descLabel = new Label("", descStyle);
        descLabel.setWrap(true);
        descLabel.setAlignment(Align.topLeft);

        for (int i = 0; i < 6; i++) {
            final int diceIndex = i;
            diceImages[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (diceIndex < manager.dices.size()) {
                        manager.diceClicked(manager.dices.get(diceIndex));
                    }
                }
            });
            diceTable.add(diceImages[i]).size(32, 32).padBottom(4).row();
        }

        Image rollAllButton = new Image(rollAllTexture);
        rollAllButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.rollAllDice();
            }
        });
        diceTable.add(rollAllButton).size(40, 20).padTop(4).row();

        Image skipTurnButton = new Image(inverseArrowTexture);
        skipTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.skipTurn();
            }
        });
        diceTable.add(skipTurnButton).size(30, 16).padTop(4).expand().row();

        Image spadesImage = new Image(cardSpades);
        spadesImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (manager.getState() == FightManager.FightState.PLAYER_PICK_CARD) {
                    selectedSuit = Suit.SPADES;
                    switchToSecondView();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                descLabel.setText("SPADES\n\nDeal direct damage to the enemy.\nHigher rank = more damage.\nBoost with DIAMONDS cards.");
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                descLabel.setText(SUIT_OVERVIEW);
            }
        });

        Image clubsImage = new Image(cardClubs);
        clubsImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (manager.getState() == FightManager.FightState.PLAYER_PICK_CARD) {
                    selectedSuit = Suit.CLUBS;
                    switchToSecondView();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                descLabel.setText("CLUBS\n\nWeaken the enemy.\nReduces enemy damage by %.\nStacks multiplicatively.");
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                descLabel.setText(SUIT_OVERVIEW);
            }
        });

        Image diamondsImage = new Image(cardDiamonds);
        diamondsImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (manager.getState() == FightManager.FightState.PLAYER_PICK_CARD) {
                    selectedSuit = Suit.DIAMONDS;
                    switchToSecondView();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                descLabel.setText("DIAMONDS\n\nStrengthen yourself.\nIncreases your damage by %.\nMultiple stacks add up.");
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                descLabel.setText(SUIT_OVERVIEW);
            }
        });

        Image heartsImage = new Image(cardHearts);
        heartsImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (manager.getState() == FightManager.FightState.PLAYER_PICK_CARD) {
                    selectedSuit = Suit.HEARTS;
                    switchToSecondView();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                descLabel.setText("HEARTS\n\nHeal yourself.\nHigher rank = more healing.\nCan restore up to max HP.");
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                descLabel.setText(SUIT_OVERVIEW);
            }
        });

        cardsTable1.add(spadesImage).pad(10).minHeight(50);
        cardsTable1.add(clubsImage).pad(10);
        cardsTable1.add(diamondsImage).pad(10);
        cardsTable1.add(heartsImage).pad(10);

        Table descPanel = new Table();
        descPanel.add(descLabel).width(200).pad(8).top().left().expand();

        middleWrapper.add(descPanel).width(220).expandY().fillY().top().left();
        middleWrapper.add().expand().fill();
        middleWrapper.row();
        middleWrapper.add(cardsWrapper).expandX().bottom().padBottom(5).align(Align.bottom).colspan(2).minWidth(730);

        mainTable.add(middleWrapper).expand().fill();
        mainTable.add(diceTable).width(60).expandY().fillY().align(Align.right).padRight(5);

        arrowImage = new Image(arrowTexture);
        inverseArrowImage = new Image(inverseArrowTexture);
        inverseArrowImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToFirstView();
            }
        });

        cardsTable2.add(inverseArrowImage).padLeft(10).size(30, 16);
        cardsTable2.add(cardSlots[0]).padLeft(40).size(60, 90);
        cardsTable2.add(arrowImage).padLeft(10).size(50, 28);
        for (int i = 1; i < 6; i++) {
            cardsTable2.add(cardSlots[i]).padLeft(8).size(60, 90);
        }

        switchToFirstView();
    }

    private static final String SUIT_OVERVIEW =
        "SPADES  - Deal damage\n" +
        "CLUBS   - Weaken enemy (reduces damage%)\n" +
        "DIAMONDS - Strengthen self (increases damage%)\n" +
        "HEARTS  - Heal yourself";

    public void switchToFirstView() {
        descLabel.setText(SUIT_OVERVIEW);
        cardsWrapper.clearChildren();
        cardsWrapper.setActor(cardsTable1);
    }

    public void switchToSecondView() {
        cardsWrapper.clearChildren();
        updateCards();
        cardsWrapper.setActor(cardsTable2);
    }

    public void updateCards() {
        ArrayList<Card> cards = manager.getHand(selectedSuit);

        cardSlots[0].clearChildren();
        switch (selectedSuit) {
            case SPADES:   cardSlots[0].add(new Image(cardSpades)); break;
            case CLUBS:    cardSlots[0].add(new Image(cardClubs)); break;
            case DIAMONDS: cardSlots[0].add(new Image(cardDiamonds)); break;
            case HEARTS:   cardSlots[0].add(new Image(cardHearts)); break;
            default: throw new AssertionError();
        }

        for (int i = 0; i < 5; i++) {
            int slotIndex = i + 1;
            cardSlots[slotIndex].clearChildren();

            if (i >= cards.size()) {
                cardSlots[slotIndex].add(new Image(lockedDice));
                continue;
            }

            final Card card = cards.get(i);
            card.loadTexture();
            Image cardImage = new Image(card.getTextureRegion());

            cardImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    descLabel.setText("");
                    manager.actSelectedCard(card);
                    switchToFirstView();
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    descLabel.setText(card.name + "\n\n" + card.description);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    descLabel.setText("");
                }
            });

            cardSlots[slotIndex].add(cardImage);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        manager.updateFight(delta);

        updateDiceImages();

        stage.act(delta);
        stage.draw();

        Matrix4 proj = stage.getCamera().combined;
        batch.setProjectionMatrix(proj);
        shapeRenderer.setProjectionMatrix(proj);

        Player player = manager.getPlayer();
        Mob mob = manager.getMob();

        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float playerTopY = player.getY() + player.getHeight();
        float mobCenterX = mob.getX() + mob.getWidth() / 2f;
        float mobTopY = mob.getY() + mob.getHeight();

        drawHealthBar(player, playerCenterX - HP_BAR_WIDTH / 2f, playerTopY + 8);
        drawEffectIcons(player, playerCenterX - HP_BAR_WIDTH / 2f, playerTopY + 24);

        drawHealthBar(mob, mobCenterX - HP_BAR_WIDTH / 2f, mobTopY + 8);
        drawEffectIcons(mob, mobCenterX - HP_BAR_WIDTH / 2f, mobTopY + 24);

        batch.begin();
        player.draw(batch);
        mob.draw(batch);

        if (manager.isPlayerTurn()) {
            uiFont.setColor(Color.GREEN);
            uiFont.draw(batch, "Player's Turn", 10, 20);
        } else {
            uiFont.setColor(Color.RED);
            uiFont.draw(batch, "Enemy's Turn", 10, 20);
        }

        uiFont.setColor(Color.WHITE);
        uiFont.draw(batch, "Dice: " + manager.getDiceTotal(), 10, 55);

        uiFont.setColor(Color.LIGHT_GRAY);
        FightManager.FightState currentState = manager.getState();
        if (currentState == FightManager.FightState.PLAYER_PICK_CARD) {
            uiFont.draw(batch, "Pick a card!", 10, 38);
        } else if (currentState == FightManager.FightState.PLAYER_ROLL) {
            Card sel = manager.getSelectedCard();
            if (sel != null) {
                uiFont.draw(batch, "Selected: " + sel.name + " - Roll!", 10, 38);
            }
        }

        String msg = manager.getLastMessage();
        if (msg != null) {
            uiFont.setColor(Color.YELLOW);
            float msgX = viewport.getWorldWidth() / 2f - 80;
            uiFont.draw(batch, msg, msgX, viewport.getWorldHeight() / 2f);
        }

        batch.end();
        uiFont.setColor(Color.WHITE);
    }

    private void updateDiceImages() {
        for (int i = 0; i < 6; i++) {
            if (i < manager.dices.size()) {
                Dice d = manager.dices.get(i);
                TextureRegion frame = d.getCurrentFrame();
                if (frame != null) {
                    diceImages[i].setDrawable(new TextureRegionDrawable(frame));
                } else {
                    diceImages[i].setDrawable(new TextureRegionDrawable(lockedDice));
                }
            } else {
                diceImages[i].setDrawable(new TextureRegionDrawable(lockedDice));
            }
        }
    }

    private void drawHealthBar(Entity e, float x, float y) {
        float healthRatio = e.health / e.maxHealth;

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.setColor(0.35f, 0.05f, 0.05f, 0.9f);
        shapeRenderer.rect(x, y, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        shapeRenderer.setColor(1f - healthRatio, healthRatio * 0.85f, 0.05f, 1f);
        shapeRenderer.rect(x, y, HP_BAR_WIDTH * healthRatio, HP_BAR_HEIGHT);
        shapeRenderer.end();

        batch.begin();
        smallFont.setColor(Color.WHITE);
        smallFont.draw(batch, (int) e.health + "/" + (int) e.maxHealth, x, y + HP_BAR_HEIGHT + 10f);
        batch.end();
    }

    private void drawEffectIcons(Entity e, float startX, float startY) {
        ArrayList<Effect> effects = e.getEffects();
        if (effects == null || effects.isEmpty()) return;

        batch.begin();
        float offsetX = 0;
        for (Effect eff : effects) {
            if (effectsAtlas != null && eff.getName() != null) {
                TextureRegion region = effectsAtlas.findRegion(eff.getName());
                if (region != null) {
                    batch.draw(region, startX + offsetX, startY, EFFECT_ICON_SIZE, EFFECT_ICON_SIZE);
                    smallFont.setColor(Color.WHITE);
                    smallFont.draw(batch, String.valueOf(eff.getDurationLeft()),
                        startX + offsetX + EFFECT_ICON_SIZE - 2, startY + 8);
                    offsetX += EFFECT_ICON_SIZE + 3;
                }
            }
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
