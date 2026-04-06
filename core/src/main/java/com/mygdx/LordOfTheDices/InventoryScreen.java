package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.LordOfTheDices.Card.Suit;

public class InventoryScreen implements Screen {

    private static final float VIRTUAL_WIDTH = 800f;
    private static final float VIRTUAL_HEIGHT = 480f;

    private final ScreenManager screenManager;
    private final Inventory inventory;

    private Stage stage;
    private BitmapFont font;
    private BitmapFont font2;
    private Label descLabel;
    private Label goldLabel;
    private Label reminderLabel;

    private Table leftPanel;
    private Table rightPanel;

    private ScrollPane cardsScroll;
    private ScrollPane relicsScroll;
    private ScrollPane diceScroll;

    private Label buttonText;
    private Label buttonText2;
    private Label buttonText3;
    private Label cardsTitle;
    private Label relicsTitle;
    private Label DiceTitle;

    private Image invImg;
    private Image descImg;
    private Image buttonImage;
    private Image buttonImage2;
    private Image buttonImage3;

    private boolean mode = false;

    private Label.LabelStyle labelStyle;
    private Label.LabelStyle chosenStyle;
    private Label.LabelStyle titleStyle;


    public InventoryScreen(ScreenManager screenManager, Inventory inventory) {
        this.screenManager = screenManager;
        this.inventory = inventory;

        Texture invTexture = new Texture("Inventory//inventoryScreen2.png");
        Texture descTexture = new Texture("Inventory//inventoryScreen2Desc.png");
        Texture buttonTexture = new Texture("Inventory//inventoryButton.png");

        invImg = new Image(invTexture);
        descImg = new Image(descTexture);
        buttonImage = new Image(buttonTexture);
        buttonImage2 = new Image(buttonTexture);
        buttonImage3 = new Image(buttonTexture);
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        invImg.setSize(220, VIRTUAL_HEIGHT - 200);
        invImg.setPosition(15, 0);

        descImg.setSize(VIRTUAL_WIDTH - 250, VIRTUAL_HEIGHT - 60);
        descImg.setPosition(250, 0);

        descImg.setTouchable(Touchable.disabled);
        invImg.setTouchable(Touchable.disabled);
        buttonImage.setTouchable(Touchable.enabled);
        buttonImage2.setTouchable(Touchable.enabled);
        buttonImage3.setTouchable(Touchable.enabled);

        buttonImage.setSize(VIRTUAL_WIDTH - 680, VIRTUAL_HEIGHT - 450);
        buttonImage.setPosition(120, 290);
        
        buttonImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonText.setStyle(labelStyle);
                buttonText2.setStyle(chosenStyle);
                buttonText3.setStyle(labelStyle);
                cardsTitle.setVisible(false);
                relicsTitle.setVisible(true);
                DiceTitle.setVisible(false);
        
                relicsScroll.setTouchable(Touchable.enabled);
                cardsScroll.setTouchable(Touchable.disabled);
                cardsScroll.setVisible(false);
                relicsScroll.setVisible(true);
                diceScroll.setVisible(false);
            }
        });

        buttonImage2.setSize(VIRTUAL_WIDTH - 680, VIRTUAL_HEIGHT - 450);
        buttonImage2.setPosition(120, 330);

        buttonImage2.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonText.setStyle(chosenStyle);
                buttonText2.setStyle(labelStyle);
                buttonText3.setStyle(labelStyle);
                cardsTitle.setVisible(true);
                relicsTitle.setVisible(false);
                DiceTitle.setVisible(false);
                
                relicsScroll.setTouchable(Touchable.disabled);
                cardsScroll.setTouchable(Touchable.enabled);
                diceScroll.setVisible(false);
                cardsScroll.setVisible(true);
                relicsScroll.setVisible(false);
                diceScroll.setVisible(false);

            }
        });

        buttonImage3.setSize(VIRTUAL_WIDTH - 680, VIRTUAL_HEIGHT - 450);
        buttonImage3.setPosition(120, 370);

        buttonImage3.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonText.setStyle(labelStyle);
                buttonText2.setStyle(labelStyle);
                buttonText3.setStyle(chosenStyle);
                cardsTitle.setVisible(false);
                relicsTitle.setVisible(false);
                DiceTitle.setVisible(true);
        
                relicsScroll.setTouchable(Touchable.disabled);
                cardsScroll.setTouchable(Touchable.disabled);
                diceScroll.setTouchable(Touchable.enabled);
                cardsScroll.setVisible(false);
                relicsScroll.setVisible(false);
                diceScroll.setVisible(true);

            }
        });


        stage.addActor(invImg);
        stage.addActor(descImg);
        stage.addActor(buttonImage);
        stage.addActor(buttonImage2);
        stage.addActor(buttonImage3);
        font = new BitmapFont();
        font.getData().setScale(2f);
        font2 = new BitmapFont();
        font2.getData().setScale(1f);
        

        labelStyle = new Label.LabelStyle(font2, Color.WHITE);
        chosenStyle = new Label.LabelStyle(font2, Color.GOLD);
        titleStyle = new Label.LabelStyle(font, Color.GOLD);

        //Root table
        Table rootTable = new Table();
        rootTable.top().left();
        rootTable.setFillParent(true);
        rootTable.pad(15);
        stage.addActor(rootTable);

        //Left panel: descriptions
        
        leftPanel = new Table();
        leftPanel.bottom().left();

        Label descTitle = new Label("Item Info", titleStyle);
        leftPanel.add(descTitle).left().padTop(180).padLeft(50).row();

        descLabel = new Label("Hover over an item to see its description.", labelStyle);
        descLabel.setWrap(true);
        leftPanel.add(descLabel).width(200).left().padTop(50).padLeft(18).expandY().top();

        //Right panel: inventory
        rightPanel = new Table();
        rightPanel.top();

        // Gold display
        goldLabel = new Label("Gold: " + inventory.getGold(), titleStyle);
        rightPanel.add(goldLabel).left().padBottom(10).padRight(70).colspan(2).row();

        //Reminder display
        Table remindTable = new Table();
        remindTable.top().left();
        reminderLabel = new Label("Press ESC to exit ", labelStyle);
        remindTable.add(reminderLabel).top().left().padRight(300).row();

        //Button texts
        buttonText = (mode) ? new Label("Cards", labelStyle) : new Label("Cards", chosenStyle);
        buttonText.setPosition(160, 336);
        buttonText.setTouchable(Touchable.disabled);
        stage.addActor(buttonText);

        buttonText2 = (mode) ? new Label("Relics", chosenStyle) : new Label("Relics", labelStyle);
        buttonText2.setPosition(160, 295);
        buttonText2.setTouchable(Touchable.disabled);
        stage.addActor(buttonText2);

        buttonText3 = new Label("Dice", labelStyle);
        buttonText3.setPosition(160, 377);
        buttonText3.setTouchable(Touchable.disabled);
        stage.addActor(buttonText3);


        // Cards section(Visible at start)

            cardsTitle = new Label("Cards ", titleStyle);
            rightPanel.add(cardsTitle).center().padLeft(20).padTop(-10).colspan(2).row();

            Table cardsTable = buildCardsTable(labelStyle);
            cardsScroll = new ScrollPane(cardsTable);
            cardsScroll.setFadeScrollBars(false);

        
        // Relics section(Not visible at start)

            relicsTitle = new Label("Relics", titleStyle);
            rightPanel.add(relicsTitle).center().padLeft(20).padTop(-40).colspan(2).row();


            Table relicsTable = buildRelicsTable(labelStyle);
            relicsScroll = new ScrollPane(relicsTable);
            relicsScroll.setFadeScrollBars(false);

        //Dice section(Not visible at start)
            DiceTitle = new Label("Dice", titleStyle);
            rightPanel.add(DiceTitle).center().padLeft(20).padTop(-40).colspan(2).row();

            Table diceTable = buildDiceTable(labelStyle);
            diceScroll = new ScrollPane(diceTable);
            diceScroll.setFadeScrollBars(false);
        
        Stack stack = new Stack();
        relicsScroll.setTouchable(Touchable.disabled);
        relicsTitle.setTouchable(Touchable.disabled);
        cardsTitle.setTouchable(Touchable.disabled);
        stack.add(cardsScroll);
        stack.add(relicsScroll);
        stack.add(diceScroll);
        rightPanel.add(stack).width(480).height(300).padTop(10).colspan(2);

        


        cardsScroll.setVisible(true);
        relicsScroll.setVisible(false);
        relicsTitle.setVisible(false);
        diceScroll.setVisible(false);
        DiceTitle.setVisible(false);
   
        rootTable.add(remindTable).top().left().colspan(2).row();
        rootTable.add(leftPanel).width(220).expandY().fillY().padRight(15).padTop(30);
        rootTable.add(rightPanel).expand().fill();
    }

    private Table buildCardsTable(Label.LabelStyle style) {
        Table table = new Table();
        table.top().left().padLeft(70).padTop(30);

        int repeat = 1;
        int value = 0;
        Suit suit = null;
        Label storeLabel = null;

        int col = 0;
        for (Card card : inventory.getCards()) {
            if(card.getRank().getNumericValue() == value && card.getSuit() == suit){
                repeat++;
                storeLabel.setText(card.getRank().name() + "(X" + repeat + ")");
            }
            else{
                    repeat = 1;
                    value = card.getRank().getNumericValue();
                    suit = card.getSuit();
                    Table cardCell = new Table();
                    cardCell.setUserObject("Item");

                    TextureRegion region = card.getTextureRegion();
                    if (region != null) {
                        Image cardImage = new Image(region);
                        cardCell.add(cardImage).size(48, 72).row();
                    }

                    Label nameLabel = new Label(card.getRank().name(), style);
                    nameLabel.setFontScale(0.7f);
                    cardCell.add(nameLabel).center();

                    storeLabel = nameLabel;

                    cardCell.addListener(new ClickListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        descLabel.setText(card.getName() + "\n\n" + card.getDescription());
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        descLabel.setText("Hover over an item to see its description.");
                    }
                });

                table.add(cardCell).pad(6);
                col++;
                if (col >= 6) {
                    table.row();
                    col = 0;
                }
            }
        }
        return table;
    }

    private Table buildDiceTable(Label.LabelStyle style) {
        Table table = new Table();
        table.top().left().padLeft(70).padTop(30);

        int count = inventory.getDiceCount();

        int col = 0;
        for (Dice dice : inventory.getDice()) {
                    Table diceCell = new Table();
                    diceCell.setUserObject("Item");

                    TextureRegion region = dice.getCurrentFrame();
                    if (region != null) {
                        Image diceImage = new Image(region);
                        diceCell.add(diceImage).size(48, 48).row();
                    }

                    Label nameLabel = new Label(dice.getName(), style);
                    nameLabel.setFontScale(0.7f);
                    diceCell.add(nameLabel).center();


                    diceCell.addListener(new ClickListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        descLabel.setText(dice.getName() + "\n\n" + dice.getDescription());
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        descLabel.setText("Hover over an item to see its description.");
                    }
                });

                table.add(diceCell).pad(6);
                col++;
                if (col >= 3) {
                    table.row();
                    col = 0;
                }
            
        }

        for(int i = 0; i < 6 - count; i++){
            Table diceCell = new Table();
                    diceCell.setUserObject("Item");

                    Texture region = new Texture("dice/LockedDice.png");
                    Image diceImage = new Image(region);
                    diceCell.add(diceImage).size(48, 48).row();

                    Label nameLabel = new Label("Locked", style);
                    nameLabel.setFontScale(0.7f);
                    diceCell.add(nameLabel).center();


                    diceCell.addListener(new ClickListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        descLabel.setText("Locked" + "\n\n" + "You don't have this dice yet.");
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        descLabel.setText("Hover over an item to see its description.");
                    }
                });

                table.add(diceCell).pad(6);
                col++;
                if (col >= 3) {
                    table.row();
                    col = 0;
                }
        }
        return table;
    }

    private Table buildRelicsTable(Label.LabelStyle style) {
        Table table = new Table();
         table.top().left().padLeft(70).padTop(30);

        if (inventory.getRelicCount() == 0) {
            table.add(new Label("No relics collected yet.", style));
            return table;
        }

        int col = 0;
        for (Relic relic : inventory.getRelics()) {
            
            Table relicCell = new Table();
            relicCell.setUserObject("Item");

            relicCell.setUserObject("Relic");

            TextureRegion region = relic.getTextureRegion();
            if (region != null) {
                Image relicImage = new Image(region);
                relicCell.add(relicImage).size(48, 48).row();
            }

            Label relicName = new Label(relic.getName(), style);
            relicName.setFontScale(0.85f);
            relicCell.add(relicName);

            relicCell.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {

                        descLabel.setText(relic.getName() + "\n\n" + relic.getDescription()
                        + (relic.isActive() ? "\n[ACTIVE]" : "\n[INACTIVE]"));
                    
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {

                    descLabel.setText("Hover over an item to see its description.");
                
                }
            });
            if(col == 0){
                table.add(relicCell).padTop(10);
            }
            else{
                table.add(relicCell).padLeft(90).padTop(10);;
            }
            col++;
                if (col >= 3) {
                    table.row();
                    col = 0;
                }
        }
        return table;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (font != null) font.dispose();
    }

    @Override
    public void render(float delta) {
        // ESC or I to go back
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            screenManager.goBack();
            return;
        }

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        goldLabel.setText("Gold: " + inventory.getGold());

        stage.act(delta);
        stage.draw();
    }
}





