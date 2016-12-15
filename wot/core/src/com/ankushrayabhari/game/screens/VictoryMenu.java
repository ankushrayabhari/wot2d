package com.ankushrayabhari.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ankushrayabhari.game.WorldOfTanks;

public class VictoryMenu extends ScreenAdapter {

    private WorldOfTanks game;
    public static final int BUTTON_WIDTH = 450;
    public static final int BUTTON_HEIGHT = 105;
    private Label titleLabel;
    private TextButton newGameButton, homeButton, exitButton;
    private Skin skin;
    private Stage stage;

    public VictoryMenu(final WorldOfTanks t, final Skin s) {
        game = t;
        skin = s;
        stage = new Stage();
        titleLabel = new Label("Victory!", skin, "default");
        newGameButton = new TextButton("Play Again", skin);
        homeButton = new TextButton("Return to Main Menu (will lead to bugs)", skin);
        exitButton = new TextButton("Exit", skin);

        float centerX = Gdx.graphics.getWidth() / 2;
        float centerButtonX = Gdx.graphics.getWidth() / 2 - BUTTON_WIDTH / 2;
        float titleX = centerX - titleLabel.getPrefWidth() / 2;
        titleLabel.setPosition(titleX, Gdx.graphics.getHeight() * 3 / 4);
        newGameButton.setPosition(centerButtonX, Gdx.graphics.getHeight() / 2);
        homeButton.setPosition(centerButtonX, Gdx.graphics.getHeight() * 3 / 8);
        exitButton.setPosition(centerButtonX, Gdx.graphics.getHeight() / 4);

        // listeners
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, skin));
                newGameButton.toggle();
            }
        });
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(t, s));
                homeButton.toggle();
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(titleLabel);
        stage.addActor(newGameButton);
        stage.addActor(homeButton);
        stage.addActor(exitButton);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
