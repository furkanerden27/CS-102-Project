package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Assets {

    private final AssetManager manager = new AssetManager();

    public static final String BG_MAIN_MENU = "ui/Main_Menu/main_menu_bg.png";
    public static final String TEXTURE_DICE = "dice.png";
    public static final String TEXTURE_FIGHT_BG = "FightBackground.png";
    public static final String TEX_LOCKED_DICE = "LockedDice.png";
    public static final String TEX_ROLL_ALL_BTN = "RollAllButton.png";
    public static final String TEX_ARROW           = "Arrow.png";
    public static final String TEX_INVERSE_ARROW   = "InverseArrow.png";
    public static final String TEX_CARD_SPADES     = "Cards/card-spades.png";
    public static final String TEX_CARD_CLUBS      = "Cards/card-clubs.png";
    public static final String TEX_CARD_HEARTS     = "Cards/card-hearts.png";
    public static final String TEX_CARD_DIAMONDS   = "Cards/card-diamonds.png";

    private static final int STORY_BEGIN_COUNT = 5;
    private static final String STORY_BEGIN_FMT = "ui/Story/Story_Beginning_bg/%d.Story_bg.jpeg";

    private static final int STORY_END_COUNT = 6;
    private static final String STORY_END_FMT = "ui/Story/Story_Ending_bg/%d.Story_End_bg.jpeg";

    public static final String ATLAS_UI_NORMAL      = "ui/Main_Menu/uic.atlas";
    public static final String ATLAS_UI_HOVER       = "ui/Main_Menu/hoverr.atlas";
    public static final String ATLAS_OPTIONS        = "ui/Options_Menu/options_menu.atlas";
    public static final String ATLAS_SAVE_PANEL     = "ui/Save_Name_Screen/SetSaveNamePanel.atlas";
    public static final String ATLAS_STORY_BUTTONS  = "ui/Story/Story_Buttons/Story_buttons.atlas";
    public static final String ATLAS_CARDS          = "cards.atlas";
    public static final String ATLAS_ENTITIES       = "Atlas/Entities.atlas";
    public static final String ATLAS_EFFECTS        = "Effects.atlas";

    public static final String SFX_HOVER = "audio/hoverSound.mp3";

    public static final String MAP_1 = "Maps/Map 1.tmx";
    public static final String MAP_2 = "Maps/Map 2.tmx";

    public void load() {
        // TiledMap loader
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        // Texture'lar
        manager.load(BG_MAIN_MENU, Texture.class);
        manager.load(TEXTURE_DICE, Texture.class);
        manager.load(TEXTURE_FIGHT_BG, Texture.class);
        manager.load(TEX_LOCKED_DICE, Texture.class);
        manager.load(TEX_ROLL_ALL_BTN, Texture.class);
        manager.load(TEX_ARROW, Texture.class);
        manager.load(TEX_INVERSE_ARROW,  Texture.class);
        manager.load(TEX_CARD_SPADES, Texture.class);
        manager.load(TEX_CARD_CLUBS,Texture.class);
        manager.load(TEX_CARD_HEARTS,    Texture.class);
        manager.load(TEX_CARD_DIAMONDS,  Texture.class);

        for (int i = 1; i <= STORY_BEGIN_COUNT; i++)
            manager.load(String.format(STORY_BEGIN_FMT, i), Texture.class);

        for (int i = 1; i <= STORY_END_COUNT; i++)
            manager.load(String.format(STORY_END_FMT, i), Texture.class);

        manager.load(ATLAS_UI_NORMAL,     TextureAtlas.class);
        manager.load(ATLAS_UI_HOVER,      TextureAtlas.class);
        manager.load(ATLAS_OPTIONS,       TextureAtlas.class);
        manager.load(ATLAS_SAVE_PANEL,    TextureAtlas.class);
        manager.load(ATLAS_STORY_BUTTONS, TextureAtlas.class);
        manager.load(ATLAS_CARDS,         TextureAtlas.class);
        manager.load(ATLAS_ENTITIES,      TextureAtlas.class);
        manager.load(ATLAS_EFFECTS,       TextureAtlas.class);

        manager.load(SFX_HOVER, Sound.class);

        manager.load(MAP_1, TiledMap.class);
        manager.load(MAP_2, TiledMap.class);
    }

    public void finishLoading() {
        manager.finishLoading();
    }

    public <T> T get(String path, Class<T> type) {
        return manager.get(path, type);
    }

    public Texture getTexture(String path) {
        return manager.get(path, Texture.class);
    }

    public TextureAtlas getAtlas(String path) {
        return manager.get(path, TextureAtlas.class);
    }

    public Sound getSound(String path) {
        return manager.get(path, Sound.class);
    }

    public TiledMap getMap(String path) {
        return manager.get(path, TiledMap.class);
    }

    public Texture getStoryBeginSlide(int index) {
        return manager.get(String.format(STORY_BEGIN_FMT, index + 1), Texture.class);
    }

    public Texture getStoryEndSlide(int index) {
        return manager.get(String.format(STORY_END_FMT, index + 1), Texture.class);
    }

    public int getStoryBeginCount(){
        return STORY_BEGIN_COUNT;
    }
    public int getStoryEndCount(){ 
        return STORY_END_COUNT;
    }

    public void dispose() {
        manager.dispose();
    }
}
