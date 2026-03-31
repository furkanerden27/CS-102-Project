package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class InventoryScreen implements Screen {

    private static final float VIRTUAL_WIDTH = 800f;
    private static final float VIRTUAL_HEIGHT = 480f;

    private final ScreenManager screenManager;
    private final Inventory inventory;

    private Stage stage;
    private BitmapFont font;
    private Label descriptionLabel;
    private Label goldLabel;

    public InventoryScreen(ScreenManager screenManager, Inventory inventory) {
        this.screenManager = screenManager;
        this.inventory = inventory;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        font.getData().setScale(1f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(15);
        stage.addActor(rootTable);

        Table leftPanel = new Table();
        leftPanel.top().left();

        Label descTitle = new Label("Item Info", titleStyle);
        leftPanel.add(descTitle).left().padBottom(10).row();

        descriptionLabel = new Label("Hover over an item to see its description.", labelStyle);
        descriptionLabel.setWrap(true);
        leftPanel.add(descriptionLabel).width(200).left().expandY().top();

        Table rightPanel = new Table();
        rightPanel.top();

        goldLabel = new Label("Gold: " + inventory.getGold(), titleStyle);
        rightPanel.add(goldLabel).left().padBottom(10).colspan(2).row();

        Label diceLabel = new Label("Dice: " + inventory.getDiceCount(), labelStyle);
        rightPanel.add(diceLabel).left().padBottom(15).colspan(2).row();

        Label cardsTitle = new Label("-- Cards --", titleStyle);
        rightPanel.add(cardsTitle).center().padBottom(8).colspan(2).row();

        Table cardsTable = buildCardsTable(labelStyle);
        ScrollPane cardsScroll = new ScrollPane(cardsTable);
        cardsScroll.setFadeScrollBars(false);
        rightPanel.add(cardsScroll).width(480).height(220).colspan(2).row();

        Label relicsTitle = new Label("-- Relics --", titleStyle);
        rightPanel.add(relicsTitle).center().padTop(10).padBottom(8).colspan(2).row();

        Table relicsTable = buildRelicsTable(labelStyle);
        ScrollPane relicsScroll = new ScrollPane(relicsTable);
        relicsScroll.setFadeScrollBars(false);
        rightPanel.add(relicsScroll).width(480).height(80).colspan(2).row();

        rootTable.add(leftPanel).width(220).expandY().fillY().padRight(15);
        rootTable.add(rightPanel).expand().fill();
    }

    private Table buildCardsTable(Label.LabelStyle style) {
        Table table = new Table();
        table.top().left();

        int col = 0;
        for (final Card card : inventory.getCards()) {
            Table cardCell = new Table();

            TextureRegion region = card.getTextureRegion();
            if (region != null) {
                Image cardImage = new Image(region);
                cardCell.add(cardImage).size(48, 72).row();
            }

            Label nameLabel = new Label(card.getRank().name(), style);
            nameLabel.setFontScale(0.7f);
            cardCell.add(nameLabel).center();

            cardCell.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    descriptionLabel.setText(card.getName() + "\n\n" + card.getDescription());
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    descriptionLabel.setText("Hover over an item to see its description.");
                }
            });

            table.add(cardCell).pad(6);
            col++;
            if (col >= 6) {
                table.row();
                col = 0;
            }
        }
        return table;
    }

    private Table buildRelicsTable(Label.LabelStyle style) {
        Table table = new Table();
        table.top().left();

        if (inventory.getRelicCount() == 0) {
            table.add(new Label("No relics collected yet.", style));
            return table;
        }

        for (final Relic relic : inventory.getRelics()) {
            Table relicCell = new Table();

            Label relicName = new Label(relic.getName(), style);
            relicName.setFontScale(0.85f);
            relicCell.add(relicName);

            relicCell.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    descriptionLabel.setText(relic.getName() + "\n\n" + relic.getDescription()
                        + (relic.isActive() ? "\n[ACTIVE]" : "\n[INACTIVE]"));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    descriptionLabel.setText("Hover over an item to see its description.");
                }
            });

            table.add(relicCell).pad(8);
        }
        return table;
    }

    @Override
    public void render(float delta) {
        // ESC to go back
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
