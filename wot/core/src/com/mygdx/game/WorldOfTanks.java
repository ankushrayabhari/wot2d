package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import screens.SplashScreen;

public class WorldOfTanks extends Game {
    public static final String TITLE = "World of Tanks 2D", VERSION = "0.9";
    public static final int GAME_WIDTH = 1600;
    public static final int GAME_HEIGHT = 900;
    private Viewport viewport;
    

//    public ScreenAdapter getGameScreen() {
//        return gameScreen;
//    }

    public Viewport getViewport() {
        return viewport;
    }

    @Override
    public void create() {
        viewport = new FitViewport(GAME_WIDTH, GAME_HEIGHT);
        setScreen(new SplashScreen(this));
    }
}
