package com.ankushrayabhari.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ankushrayabhari.game.WorldOfTanks;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = WorldOfTanks.TITLE + " " + WorldOfTanks.VERSION;
        config.width = WorldOfTanks.GAME_WIDTH;
        config.height = WorldOfTanks.GAME_HEIGHT;
        new LwjglApplication(new WorldOfTanks(), config);
    }
}
