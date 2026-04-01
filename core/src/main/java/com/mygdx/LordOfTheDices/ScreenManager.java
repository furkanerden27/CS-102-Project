package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.sun.org.apache.xerces.internal.impl.xs.ElementPSVImpl;

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

        else if(screen == Screens.OPTIONS)      game.setScreen(new OptionsScreen(game, this));

        else if(screen == Screens.PLAY)         game.setScreen(new PlayScreen(game));

        // else if(screen == Screens.PAUSE)    game.setScreen(new PauseScreen(game, this));

        // else if(screen == Screens.MENU_PAUSE)    game.setScreen(new MenuPauseScreen(game, this));

    }

    public void showScreen(Screens screen, FightManager fightManager) {
        if (screen == Screens.BATTLE) {
            game.setScreen(new BattleScreen(game.getAssets(), fightManager, 800, 400));
        }
    }

    public void showScreen(Screens screen, Inventory inventory) {
        if (screen == Screens.INVENTORY) {
            previousScreen = game.getScreen();
            game.setScreen(new InventoryScreen(this, inventory));
        }
    }

}
