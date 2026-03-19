package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.LordOfTheDices.Card.Suit;

public class BattleScreen1 implements Screen{
    
    private Stage stage;
    private TextureAtlas atlas;
    private FightManager manager;


    private Texture cardSpades;
    private Texture cardClubs;
    private Texture cardHearts;
    private Texture cardDiamonds;// TODO burasi atlasla degistirilecek? nasil olcak bilmiyom ama bisiler yapilcak
    private Texture background, lockedDice, rollAllTexture, arrowTexture;
    
    private Table cardsWrapper, cardsTable1, cardsTable2;

    public BattleScreen1(FightManager manager, int sizeX, int sizeY) {
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

        Gdx.input.setInputProcessor(stage);
        
        createUI();
    }

    private void createUI(){
        cardsWrapper = new Table();
        cardsTable2 = new Table();
        cardsTable1 = new Table();
        Table middleWrapper = new Table();
        Table diceTable = new Table();
        Table mainTable = new Table();

        cardsTable1.setWidth(110000);
        cardsTable2.setWidth(800);
        cardsTable1.setDebug(true);
        cardsTable2.setDebug(true);


        mainTable.setFillParent(true);
        
        
        Image backGroundImage = new Image(background);
        backGroundImage.setFillParent(true);
        //backGroundImage.toBack();
        
        stage.addActor(backGroundImage);
        stage.addActor(mainTable);
        

        ClickListener diceClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                //manager.rollDice();
                System.out.println("ZAR ATILDI"); // TODO
            }
        };
        
        //Envanterden dice array'i aliyor olalim TODO
        Dice[] zarlar = new Dice[6];
        Image placeholderImage; 
        TextureRegion a = new TextureRegion();
        a.getTexture();
        for(int i = 0; i < 6; i++){
            //placeholderImage = new Image(zarlar[i].getTexture()); Dice item'i extendledigi zaman olacak bu
            placeholderImage = new Image(lockedDice); // BU DEGISECEK TODO
            placeholderImage.addListener(diceClickListener);
            diceTable.add(placeholderImage).padBottom(8).row();
        }

        Image rollAllButton = new Image(rollAllTexture);
        rollAllButton.addListener(diceClickListener);
        diceTable.add(rollAllButton).padBottom(5).expand().row(); // RollALl Button

        
        
        //CHANGE TODO
        diceTable.setDebug(true);

        Image spadesImage = new Image(cardSpades);
        spadesImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                switchToSecondView(Suit.SPADES);
            }
        });

        Image clubsImage = new Image(cardClubs);
        clubsImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                switchToSecondView(Suit.CLUBS);
            }
        });

        Image diamondsImage = new Image(cardDiamonds);
        diamondsImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                switchToSecondView(Suit.DIAMONDS);
            }
        });

        Image heartsImage = new Image(cardHearts);
        heartsImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                switchToSecondView(Suit.HEARTS);
            }
        });

        cardsTable1.add(spadesImage).pad(10).minHeight(50);
        cardsTable1.add(clubsImage).pad(10);
        cardsTable1.add(diamondsImage).pad(10);
        cardsTable1.add(heartsImage).pad(10);

        middleWrapper.add().expand().fill().minHeight(250).minWidth(750);
        middleWrapper.row();
        middleWrapper.add(cardsWrapper).expandX().bottom().padBottom(5).align(Align.bottom);
        

        mainTable.add(middleWrapper);
        mainTable.add(diceTable).width(40).expandY().fillY().align(Align.right);

        switchToFirstView();
    }

    public void switchToFirstView(){
        cardsWrapper.clearChildren();
        cardsWrapper.add(cardsTable1);
    }

    public void switchToSecondView(Suit selectedSuit){
        cardsWrapper.clearChildren(); // TODO hangi kartin gidecegini belilicen
        updateCards(selectedSuit);
        cardsWrapper.add(cardsTable2);
    }
    
    public void updateCards(Suit selected){
        Image inverseArrowImage = new Image(arrowTexture);
        inverseArrowImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                switchToFirstView();
            }
        });
        inverseArrowImage.setRotation(180);
        cardsTable2.add(inverseArrowImage).padLeft(10).width(80).height(40);

        Image arrowImage = new Image(arrowTexture);
        //TODO ????
        
        cardsTable2.add(new Image(cardSpades)).padLeft(10); // Cardlardan suitine göre info çekilecekx 
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
    public void dispose() {}
    
}
