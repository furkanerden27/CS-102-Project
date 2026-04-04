package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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

public class ShopScreen implements Screen{
    private static final float VIRTUAL_WIDTH = 800f;
    private static final float VIRTUAL_HEIGHT = 480f;

    private final ScreenManager screenManager;
    private final Shop shop;
    private final Inventory inventory;

    Core game;

    private Stage stage;
    private BitmapFont font;
    private BitmapFont font2;
    private Label descLabel;
    private Label goldLabel;
    private Label reminderLabel;

    private Table leftPanel;
    private Table rightPanel;

    Table relicsTable;
    Table cardsTable;
    Table shopTable;

    private ScrollPane cardsScroll;
    private ScrollPane relicsScroll;
    private ScrollPane shopScroll;

    private Label buttonText;
    private Label buttonText2;
    private Label buttonText3;
    private Label buttonText4;
    private Label cardsTitle;
    private Label relicsTitle;
    // private Label shopTitle;
    private Label descTitle;

    private Image invImg;
    private Image descImg;
    private Image buttonImage;
    private Image buttonImage2;
    private Image buttonImage3;
    private Image buttonImage4;
    private Image selectImage;
    private Image selectImage2;



    private Label.LabelStyle labelStyle;
    private Label.LabelStyle chosenStyle;
    private Label.LabelStyle titleStyle;

    private Item selected;
    private boolean isSelected;
    private boolean isBuy;


    Sound buySound;
    Sound buzz;

    //Core is needed for audio. If someone knows how to do it without the core, they can  change it.
    public ShopScreen(ScreenManager screenManager, Shop shop, Core game) {
        this.screenManager = screenManager;
        this.shop = shop;
        this.inventory = shop.getInv();
        this.game = game;

        

        Texture invTexture = new Texture("ui/shop/ShopScreen.png");
        Texture descTexture = new Texture("ui/shop/ShopScreen2.png");
        Texture buttonTexture = new Texture("ui/shop/ShopScreenButton.png");
        Texture selectTexture = new Texture("ui/selected.png");

        buySound = Gdx.audio.newSound(Gdx.files.internal("audio/buySound.mp3"));
        buzz = Gdx.audio.newSound(Gdx.files.internal("audio/buzz.mp3"));

        invImg = new Image(invTexture);
        descImg = new Image(descTexture);
        buttonImage = new Image(buttonTexture);
        buttonImage2 = new Image(buttonTexture);
        buttonImage3 = new Image(buttonTexture);
        buttonImage4 = new Image(buttonTexture);
        selectImage = new Image(selectTexture);
        selectImage2 = new Image(selectTexture);

        isSelected = false;
        isBuy = true;

    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        //Selection cancel when an empty space is clicked
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor hit = stage.hit(x, y, true);

                if (hit == null || !(hit.getUserObject() instanceof String)) {
                    if(selected != null){
                    selected = null;
                    isSelected = false;
                    descLabel.setText("It's okay, take your time.");
                    selectImage.setVisible(false);
                    selectImage2.setVisible(false);
                    }
                }
                

                return false;
            }
        });

        
        selectImage.setVisible(false);
        selectImage.setSize(48, 72);

        selectImage2.setVisible(false);
        selectImage2.setSize(48, 72);
        //UI
        invImg.setSize(220, VIRTUAL_HEIGHT - 200);
        invImg.setPosition(15, 0);

        descImg.setSize(VIRTUAL_WIDTH - 250, VIRTUAL_HEIGHT - 60);
        descImg.setPosition(250, 0);

        descImg.setTouchable(Touchable.disabled);
        invImg.setTouchable(Touchable.disabled);

        //Buttons
        buttonImage.setTouchable(Touchable.enabled);
        buttonImage2.setTouchable(Touchable.enabled);
        buttonImage3.setTouchable(Touchable.enabled);
        buttonImage4.setTouchable(Touchable.enabled);

        buttonImage.setUserObject("Button");
        buttonImage2.setUserObject("Button");
        buttonImage3.setUserObject("Button");
        buttonImage4.setUserObject("Button");

        buttonImage.setSize(VIRTUAL_WIDTH - 680, VIRTUAL_HEIGHT - 450);
        buttonImage.setPosition(120, 290);
        
        //the "Buy Card" button
        buttonImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonText.setStyle(labelStyle);
                buttonText2.setStyle(chosenStyle);
                buttonText4.setStyle(labelStyle);
                buttonText3.setText("Buy");
                cardsTitle.setVisible(false);
                relicsTitle.setVisible(true);
                // shopTitle.setVisible(true);  THIS IS NOW UNUSED, KEEPING IT AS COMMENT IN ANY CASE
        
                relicsScroll.setTouchable(Touchable.disabled);
                cardsScroll.setTouchable(Touchable.disabled);
                shopScroll.setTouchable(Touchable.enabled);
                cardsScroll.setVisible(false);
                shopScroll.setVisible(true);
                relicsScroll.setVisible(false);
                selectImage.setVisible(false);
                selectImage2.setVisible(false);

                descLabel.setText("These are what I have for\nsale!");

                isBuy = true;
            }
        });

        buttonImage2.setSize(VIRTUAL_WIDTH - 680, VIRTUAL_HEIGHT - 450);
        buttonImage2.setPosition(120, 330);

        //the "Sell" button
        buttonImage2.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonText.setStyle(chosenStyle);
                buttonText2.setStyle(labelStyle);
                buttonText4.setStyle(labelStyle);
                buttonText3.setText("Sell");
                cardsTitle.setVisible(true);
                relicsTitle.setVisible(false);
                // shopTitle.setVisible(false);
        
                relicsScroll.setTouchable(Touchable.disabled);
                cardsScroll.setTouchable(Touchable.enabled);
                cardsScroll.setVisible(true);
                relicsScroll.setVisible(false);
                shopScroll.setVisible(false);
                selectImage.setVisible(false);
                selectImage2.setVisible(false);

                descLabel.setText("You wanna sell something?\nSure, lemme take a look...");

                isBuy = false;
            }
        });

        //the "Buy" button
        buttonImage3.setSize(VIRTUAL_WIDTH - 680, VIRTUAL_HEIGHT - 450);
        buttonImage3.setPosition(600, 5);

        buttonImage3.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                
                if(selected != null){
                    if(isBuy){
                        if(selected instanceof Card){
                            descLabel.setText(shop.buyCard(((Card)selected)));
                            if(descLabel.getText().toString().equals("Thank you for your purchase!")){
                                game.getAudioManager().playSfx(buySound);
                            }
                            if(descLabel.getText().toString().equals("You don't have enough\nmoney!") || descLabel.getText().toString().equals("You already have this card!")){
                                game.getAudioManager().playSfx(buzz);
                            }
                            selectImage2.setVisible(false);
                            selected = null;
                            isSelected = false;
                            update(0);
                        }
                        // if(selected instanceof Relic)
                            //TODO
                    }
                    else{
                        if(selected instanceof Card){
                            descLabel.setText(shop.sellCard(((Card)selected)));
                            if(descLabel.getText().toString().equals("Alright, I'll take that!")){
                                game.getAudioManager().playSfx(buySound);
                            }
                            if(descLabel.getText().toString().equals("Even I won't accept \nsomething like that,\nyou know...")){
                                game.getAudioManager().playSfx(buzz);
                            }
                            selectImage.setVisible(false);
                            selected = null;
                            isSelected = false;
                            update(0);
                        }
                        // if(selected instanceof Relic)
                            //TODO
                    }
                }
                else{
                    descLabel.setText("Choose something first!");
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    buttonText3.setStyle(chosenStyle);
                }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    buttonText3.setStyle(labelStyle);
                }
            
        });

        //The "Buy Relic" button
        buttonImage4.setSize(VIRTUAL_WIDTH - 680, VIRTUAL_HEIGHT - 450);
        buttonImage4.setPosition(120, 370);

        buttonImage4.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonText.setStyle(labelStyle);
                buttonText2.setStyle(labelStyle);
                buttonText4.setStyle(chosenStyle);
                buttonText3.setText("Buy");
                cardsTitle.setVisible(false);
                relicsTitle.setVisible(true);
                // shopTitle.setVisible(false);
        
                relicsScroll.setTouchable(Touchable.enabled);
                cardsScroll.setTouchable(Touchable.disabled);
                shopScroll.setTouchable(Touchable.disabled);
                cardsScroll.setVisible(false);
                shopScroll.setVisible(false);
                relicsScroll.setVisible(true);
                selectImage.setVisible(false);
                selectImage2.setVisible(false);

                descLabel.setText("These are what I have for\nsale!");

                isBuy = true;
            }
        });

        //Stage 
        stage.addActor(invImg);
        stage.addActor(descImg);
        stage.addActor(buttonImage);
        stage.addActor(buttonImage2);
        stage.addActor(buttonImage3);
        stage.addActor(buttonImage4);
        font = new BitmapFont();
        font.getData().setScale(2f);
        font2 = new BitmapFont();
        font2.getData().setScale(1f);
        

        //label styles
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

        descTitle = new Label("Merchant", titleStyle);
        leftPanel.add(descTitle).left().padTop(180).padLeft(50).row();

        descLabel = new Label("Hello, dear customer!\nLooking for something?\nI have all you might need!", labelStyle);
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
        buttonText = new Label("Sell", labelStyle);
        buttonText.setPosition(168, 336);
        buttonText.setTouchable(Touchable.disabled);
        stage.addActor(buttonText);

        buttonText2 = new Label("Buy Card", chosenStyle);
        buttonText2.setPosition(152, 295);
        buttonText2.setTouchable(Touchable.disabled);
        stage.addActor(buttonText2);

        buttonText3 = new Label("Buy", labelStyle);
        buttonText3.setPosition(650, 10);
        buttonText3.setTouchable(Touchable.disabled);
        stage.addActor(buttonText3);

        buttonText4 = new Label("Buy Relic", labelStyle);
        buttonText4.setPosition(152, 376);
        buttonText4.setTouchable(Touchable.disabled);
        stage.addActor(buttonText4);

        //Shop section(Visible at start)
            // shopTitle = new Label("Shop", titleStyle);
            // rightPanel.add(shopTitle).center().padLeft(20).padTop(-24).colspan(2).row();

            shopTable = buildShopTable(labelStyle);
            shopScroll = new ScrollPane(shopTable);
            shopScroll.setFadeScrollBars(false);
        // Cards section(not visible at start)

            cardsTitle = new Label("Cards ", titleStyle);
            rightPanel.add(cardsTitle).center().padLeft(20).padTop(-20).colspan(2).row();

            cardsTable = buildCardsTable(labelStyle);
            cardsScroll = new ScrollPane(cardsTable);
            cardsScroll.setFadeScrollBars(false);

        
        // Relics section(Not visible at start)

            relicsTitle = new Label("Shop", titleStyle);
            rightPanel.add(relicsTitle).center().padLeft(20).padTop(-43).colspan(2).row();

            relicsTable = buildRelicsTable(labelStyle);
            relicsScroll = new ScrollPane(relicsTable);
            relicsScroll.setFadeScrollBars(false);

        
        Stack stack = new Stack();
        relicsScroll.setTouchable(Touchable.disabled);
        relicsTitle.setTouchable(Touchable.disabled);
        cardsTitle.setTouchable(Touchable.disabled);
        stack.add(cardsScroll);
        stack.add(relicsScroll);
        stack.add(shopScroll);
        rightPanel.add(stack).width(480).height(300).padTop(10).colspan(2);
        


        cardsScroll.setVisible(false);
        cardsTitle.setVisible(false);
        shopScroll.setVisible(true);
        // shopTitle.setVisible(true);
        relicsScroll.setVisible(false);
        relicsTitle.setVisible(true);
   
        rootTable.add(remindTable).top().left().colspan(2).row();
        rootTable.add(leftPanel).width(220).expandY().fillY().padRight(15).padTop(30);
        rootTable.add(rightPanel).expand().fill();

        // stage.addActor(selectImage);
    }

    //Builds the sell table.
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
                storeLabel.setText(card.getSellingValue() + "G (X" + repeat + ")");
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

                Label nameLabel = new Label(card.getSellingValue() + "G", style);
                nameLabel.setFontScale(0.7f);
                cardCell.add(nameLabel).center();

                storeLabel = nameLabel;

                cardCell.addListener(new ClickListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        if (isSelected == false)
                        descLabel.setText("That is " + card.getName() + "\n\n" + card.getDescription());
                    }

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        descLabel.setText("That is " + card.getName() + "\n\n" + card.getDescription());
                        isSelected = true;
                        selected = card;
                        selectImage.setPosition(cardCell.getX(), cardCell.getY() + 14);
                        selectImage.setVisible(true);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        if(isSelected == false)
                        descLabel.setText("Wanna look for more?\nFair enough.");
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
        table.addActor(selectImage);
        return table;
    }

    //builds the card buy table.
    private Table buildShopTable(Label.LabelStyle style) {
        Table table = new Table();
        table.top().left().padLeft(70).padTop(30);
        

        int col = 0;
        for (Card card : shop.getCardsForSale()) {
            Table cardCell = new Table();
            cardCell.setUserObject("Item");

            TextureRegion region = card.getTextureRegion();
            if (region != null) {
                Image cardImage = new Image(region);
                cardCell.add(cardImage).size(48, 72).row();
            }

            Label nameLabel = new Label(card.getBuyingValue() + "G", style);
            nameLabel.setFontScale(0.7f);
            cardCell.add(nameLabel).center();

            cardCell.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (isSelected == false)
                    descLabel.setText("That is " + card.getName() + "\n\n" + card.getDescription());
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    descLabel.setText("That is " + card.getName() + "\n\n" + card.getDescription());
                    isSelected = true;
                    selected = card;
                    selectImage2.setPosition(cardCell.getX(), cardCell.getY() + 14);
                    selectImage2.setVisible(true);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if(isSelected == false){
                    descLabel.setText("Wanna look for more?\nFair enough.");
                    }
                }


            });

            table.add(cardCell).pad(6);
            col++;
            if (col >= 6) {
                table.row();
                col = 0;
            }
        }
        table.addActor(selectImage2);
        return table;
    }

    //Builds the relic buy table.
    //(TODO)
    private Table buildRelicsTable(Label.LabelStyle style) {
        Table table = new Table();
         table.top().left().padLeft(70).padTop(30);

        if (inventory.getRelicCount() == 0) {
            table.add(new Label("No relics collected yet.", style));
            return table;
        }

        for (final Relic relic : inventory.getRelics()) {
            Table relicCell = new Table();
            relicCell.setUserObject("Item");

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

            table.add(relicCell).pad(8);
        }
        return table;
    }

    public void update(int i){
        goldLabel.setText("Gold: " + inventory.getGold());
        if (i == 0){
            cardsTable.clear();
            cardsTable = buildCardsTable(labelStyle);
            cardsScroll.setWidget(cardsTable);
        }
        if(i == 1){
            //TODO
        }
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
}


