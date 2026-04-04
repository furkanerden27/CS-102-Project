package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Screen;

public class ScreenManager {

    private final Core game;
    private Screen previousScreen;

    public ScreenManager(Core game) {
        this.game = game;
    }

    /** Returns to the previous screen without creating a new one. */
    public void goBack() {
        if (previousScreen != null) {
            game.setScreen(previousScreen);
            previousScreen = null;
        }
    }

    public void showScreen(Screens screen){
        //Ama PAUSE için dikkat! Pause ekranına geçerken eski ekranı dispose etmemelisin,
        // çünkü oyuna geri dönmek isteyeceksin. PAUSE'u ekrana hazır olduğunda bunu düşün.


        if(screen == Screens.MAIN_MENU)         game.setScreen(new MainMenuScreen(game, this));

        else if(screen == Screens.NEW_SAVE)    game.setScreen(new setSaveNameScreen(game, this));

        else if(screen == Screens.STORY_BEGIN)  game.setScreen(new StoryBeginScreen(game, this));

        else if(screen == Screens.STORY_END)    game.setScreen(new StoryEndScreen(game, this));

        else if(screen == Screens.LOAD_SAVE)        game.setScreen(new LoadGameScreen(game, this));

        else if(screen == Screens.OPTIONS) {
            previousScreen = game.getScreen();
            game.setScreen(new OptionsScreen(game, this));
        }

        else if(screen == Screens.PLAY)         game.setScreen(new PlayScreen(game));
    }

    public void showScreen(Screens screen, int gold, String saveName, Level level) {
        if (screen == Screens.PLAY) {
            game.setScreen(new PlayScreen(game, gold, saveName, level));
        }
    }

    public BattleScreen showBattleScreen(FightManager fightManager) {
        BattleScreen battleScreen =new BattleScreen(game.getAssets(), fightManager, 800, 400);
        game.setScreen(battleScreen);
        return battleScreen;
    }

    public void showScreen(Screens screen, Inventory inventory) {
        if (screen == Screens.INVENTORY) {
            previousScreen = game.getScreen();
            game.setScreen(new InventoryScreen(this, inventory));
        }
    }

    public void showShop(Shop shop) {
        previousScreen = game.getScreen();
        game.setScreen(new ShopScreen(this, shop, game));
    }

    public void showScreen(Screens screen, Screen playScreen) {
        if (screen == Screens.PAUSE) {
            game.setScreen(new PauseScreen(game, this, playScreen));
        }
    }

}
