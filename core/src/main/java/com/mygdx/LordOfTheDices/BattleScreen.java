package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.LordOfTheDices.Card.Suit;

public class BattleScreen implements Screen{
    
    private Stage stage;
    private FightManager manager;

    private Card.Suit selectedSuit;

    private Texture cardSpades;
    private Texture cardClubs;
    private Texture cardHearts;
    private Texture cardDiamonds;// TODO burasi atlasla degistirilecek? nasil olcak bilmiyom ama bisiler yapilcak
    private Texture background, lockedDice, rollAllTexture, arrowTexture, inverseArrowTexture;
    
    private Image inverseArrowImage, arrowImage;

    private Label toolTipLabel;
    private Table cardsTable1, cardsTable2, toolTipTable;
    private Table[] cardSlots;
    private Container<Table> cardsWrapper;

    public BattleScreen(Assets assets,FightManager manager, int sizeX, int sizeY) {
        stage = new Stage(new FitViewport(sizeX, sizeY));
        this.manager = manager;

        //TODO get these to atlas or smth
        cardSpades = new Texture("Cards\\card-spades.png");
        cardClubs = new Texture("Cards\\card-clubs.png");
        cardHearts = new Texture("Cards\\card-hearts.png");
        cardDiamonds = new Texture("Cards\\card-diamonds.png");
        background = new Texture("FightBackground.png");
        lockedDice = new Texture("LockedDice.png");
        rollAllTexture = new Texture("RollAllButton.png");
        arrowTexture = new Texture("Arrow.png");
        inverseArrowTexture = new Texture("InverseArrow.png");


        toolTipTable = new Table();
        toolTipLabel = new Label("", new LabelStyle( new BitmapFont(), Color.BLACK));
        toolTipTable.add(toolTipLabel);
        toolTipTable.setVisible(false);
        toolTipLabel.getStyle().font.getData().markupEnabled = true;

        cardSlots = new Table[]{new Table(), new Table(), new Table(), new Table(), new Table(), new Table()};
        Gdx.input.setInputProcessor(stage);
        
        createUI();
    }

    private void createUI(){
        cardsWrapper = new Container<>();
        cardsTable2 = new Table();
        cardsTable1 = new Table();
        Table middleWrapper = new Table();
        Table diceTable = new Table();
        Table mainTable = new Table();

        
        cardsTable1.setDebug(true);
        cardsTable2.setDebug(true);


        mainTable.setFillParent(true);
        
        
        Image backGroundImage = new Image(background);
        backGroundImage.setFillParent(true);
        //backGroundImage.toBack();
        
        stage.addActor(backGroundImage);
        stage.addActor(mainTable);
        

        Dice[] zarlar = manager.getDices();
        ClickListener diceClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                manager.rollAllDice();
                /* 
                for(int i = 0; i < zarlar.length; i++){
                    if(zarlar[i].isClicked(x, y)){ // TODO ZARLAR ITEM OLUNCA CALISACAK
                        manager.diceClicked(zarlar[i]);
                    }
                }
                    
            */
            }
        };
        
        //Envanterden dice array'i aliyor olalim TODO
        
        Image placeholderImage; 
        
        //her zar tiklandiginde kendi basina donmesi icin SPRITE olarak kullanilabilirler. 
        for(int i = 0; i < 6; i++){
            //placeholderImage = new Image(zarlar[i].getTexture()); Dice item'i extendledigi zaman olacak bu
            placeholderImage = new Image(lockedDice); // BU DEGISECEK TODO
            placeholderImage.addListener(diceClickListener);
            diceTable.add(placeholderImage).padBottom(8).row();
        }

        Image rollAllButton = new Image(rollAllTexture);
        rollAllButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                manager.rollAllDice();
            }
        });
        diceTable.add(rollAllButton).padBottom(5).expand().row(); // RollALl Button

        
        
        //CHANGE TODO
        diceTable.setDebug(true);

        Image spadesImage = new Image(cardSpades);
        spadesImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(manager.isPlayerTurn()){
                    selectedSuit = Suit.SPADES;
                    switchToSecondView();
                }
                else{
                    //TODO siranin oyuncuda olmadigibi belirtmek lazim
                }
            }
        });
        
        Image clubsImage = new Image(cardClubs);
        clubsImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(manager.isPlayerTurn()){
                    selectedSuit = Suit.CLUBS;
                    switchToSecondView();
                }
                
            }
        });

        Image diamondsImage = new Image(cardDiamonds);
        diamondsImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(manager.isPlayerTurn()){
                    selectedSuit = Suit.DIAMONDS;
                    switchToSecondView();
                }
            }
        });

        Image heartsImage = new Image(cardHearts);
        heartsImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(manager.isPlayerTurn()){
                    selectedSuit = Suit.HEARTS;
                    switchToSecondView();
                }
            }


        });

        cardsTable1.add(spadesImage).pad(10).minHeight(50);
        cardsTable1.add(clubsImage).pad(10);
        cardsTable1.add(diamondsImage).pad(10);
        cardsTable1.add(heartsImage).pad(10);

        middleWrapper.add().expand().fill().minHeight(250).minWidth(750);
        middleWrapper.row();
        middleWrapper.add(cardsWrapper).expandX().bottom().padBottom(5).align(Align.bottom).minWidth(750);
        

        mainTable.add(middleWrapper);
        mainTable.add(diceTable).width(40).expandY().fillY().align(Align.right);
        
        arrowImage = new Image(arrowTexture);
        inverseArrowImage = new Image(inverseArrowTexture);
        inverseArrowImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                switchToFirstView();
            }
        });
        
        cardsTable2.add(inverseArrowImage).padLeft(10).size(30, 16);
        
        cardsTable2.add(cardSlots[0]).padLeft(60).size(60, 90);

        cardsTable2.add(arrowImage).padLeft(15).size(60, 32);
        
        for(int i = 1; i < 6; i ++){
            cardsTable2.add(cardSlots[i]).padLeft(10).size(60, 90);
        }

        toolTipTable.add(toolTipLabel);

        switchToFirstView();
    }

    public void switchToFirstView(){
        cardsWrapper.clearChildren();
        cardsWrapper.setActor(cardsTable1);
    }

    public void switchToSecondView(){
        cardsWrapper.clearChildren(); // TODO hangi kartin gidecegini belilicen
        updateCards();
        cardsWrapper.setActor(cardsTable2);
    }
    
    public void updateCards(){
        //TODO ????
        // Cardlardan suitine göre info çekilecekx 
        Card[] cards = manager.getHand(selectedSuit); 
        
        Image addedImage;

        cardSlots[0].clearChildren();
        switch (selectedSuit) {
            case SPADES:
                cardSlots[0].add(new Image(cardSpades));
                break;
            case CLUBS:
                cardSlots[0].add(new Image(cardClubs));
                break;
            case DIAMONDS:
                cardSlots[0].add(new Image(cardDiamonds));
                break;
            case HEARTS:
                cardSlots[0].add(new Image(cardHearts));
                break;
            default:
                throw new AssertionError();
        }
        
        cardSlots[1].clearChildren();
        if(cards[0] == null){
             cardSlots[1].add(new Image(lockedDice));
        }
        else{
            cards[0].loadTexture();
            
            addedImage = new Image(cards[0].getTextureRegion());
            addedImage.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    manager.actSelectedCard(cards[0]);
                    switchToFirstView();
                }
            });  
            cardSlots[1].add(addedImage);      
        }


        cardSlots[2].clearChildren();
        if(cards[1] == null){
             cardSlots[2].add(new Image(lockedDice));
        }
        else{
            cards[1].loadTexture();
            addedImage = new Image(cards[1].getTextureRegion());
            addedImage.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    manager.actSelectedCard(cards[1]);
                    switchToFirstView();
                }
            });  
            cardSlots[2].add(addedImage);      
        }

        cardSlots[3].clearChildren();
        if(cards[2] == null){
             cardSlots[3].add(new Image(lockedDice));
        }
        else{
            cards[2].loadTexture();
            addedImage = new Image(cards[2].getTextureRegion());
            addedImage.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    manager.actSelectedCard(cards[2]);
                    switchToFirstView();
                }
            });  
            cardSlots[3].add(addedImage);      
        }

        cardSlots[4].clearChildren();
        if(cards[3] == null){
             cardSlots[4].add(new Image(lockedDice));
        }
        else{
            cards[3].loadTexture();
            addedImage = new Image(cards[3].getTextureRegion());
            addedImage.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    manager.actSelectedCard(cards[3]);
                    switchToFirstView();
                }
            });  
            cardSlots[4].add(addedImage);      
        }

        cardSlots[5].clearChildren();
        if(cards[4] == null){
             cardSlots[5].add(new Image(lockedDice));
        }
        else{
            cards[4].loadTexture();
            addedImage = new Image(cards[4].getTextureRegion());
            addedImage.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    manager.actSelectedCard(cards[4]);
                    switchToFirstView();
                }
            });  
            cardSlots[5].add(addedImage);      
        }
        
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }



    @Override
    public void show() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        cardClubs.dispose();
        cardDiamonds.dispose();
        cardHearts.dispose();
        cardSpades.dispose();
        lockedDice.dispose();   
        background.dispose();
        rollAllTexture.dispose();
        inverseArrowTexture.dispose();
        arrowTexture.dispose();
        
    }
}
