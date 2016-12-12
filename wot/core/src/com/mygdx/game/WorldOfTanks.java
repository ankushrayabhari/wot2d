package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import screens.GameScreen;
import screens.MainMenu;
import screens.SplashScreen;

public class WorldOfTanks extends Game {
    public static final String TITLE = "World of Tanks 2D", VERSION = "0.7";
    public static final int GAME_WIDTH = 1600;
    public static final int GAME_HEIGHT = 900;
    private ScreenAdapter splashScreen, mainMenu;
    private Viewport viewport;
    private boolean isLoading;

//    public ScreenAdapter getGameScreen() {
//        return gameScreen;
//    }
    
    public ScreenAdapter getMainMenu() {
        return mainMenu;
    }

    public Viewport getViewport() {
        return viewport;
    }

    @Override
    public void create() {
        splashScreen = new SplashScreen(this);
        mainMenu = new MainMenu(this, new Skin( Gdx.files.internal("ui/uiskin.json" )));
        viewport = new FitViewport(GAME_WIDTH, GAME_HEIGHT);
        setScreen(splashScreen);
    }

    @Override
    public void dispose() {
        splashScreen.dispose();
    }
}
