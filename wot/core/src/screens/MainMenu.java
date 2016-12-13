package screens;

import java.awt.FileDialog;
import java.io.IOException;

import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.WorldOfTanks;

public class MainMenu extends ScreenAdapter {

    private WorldOfTanks game;
    public static final int BUTTON_WIDTH = 450;
    public static final int BUTTON_HEIGHT = 105;
    private Label titleLabel;
    private TextButton newGameButton, loadGameButton, exitButton;
    private Skin skin;
    private Stage stage;

    public MainMenu(WorldOfTanks t, Skin s) {
        game = t;
        skin = s;
        stage = new Stage();
        titleLabel = new Label(WorldOfTanks.TITLE, skin, "default");
        newGameButton = new TextButton("New Game", skin);
        loadGameButton = new TextButton("Load Game (aka new game)", skin);
        exitButton = new TextButton("Exit", skin);

        float centerX = Gdx.graphics.getWidth() / 2;
        float centerButtonX = Gdx.graphics.getWidth() / 2 - BUTTON_WIDTH / 2;
        float titleX = centerX - titleLabel.getPrefWidth() / 2;
        titleLabel.setPosition(titleX, Gdx.graphics.getHeight() * 3 / 4);
        newGameButton.setPosition(centerButtonX, Gdx.graphics.getHeight() / 2);
        loadGameButton.setPosition(centerButtonX, Gdx.graphics.getHeight() * 3 / 8);
        exitButton.setPosition(centerButtonX, Gdx.graphics.getHeight() / 4);

        // listeners
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, skin));
                newGameButton.toggle();
            }
        });
        loadGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FileDialog fileChooser = new FileDialog(new JFrame(), 
                        "Choose a wot2d Save File", FileDialog.LOAD);
                fileChooser.setDirectory(Gdx.files.getLocalStoragePath());
                fileChooser.setVisible(true);
                String fileName = fileChooser.getFile();
                if (fileName == null) {
                    game.setScreen(new GameScreen(game, skin));
                } else {
                    while (!fileName.endsWith(".wot2dsave")) {
                        fileChooser.dispose();
                        fileChooser = new FileDialog(new JFrame(), 
                                fileName + " is not a valid wot2d save file", FileDialog.LOAD);
                        fileChooser.setDirectory(Gdx.files.getLocalStoragePath());
                        fileChooser.setVisible(true);
                        fileName = fileChooser.getFile();
                        if (fileName == null) {
                            break;
                        }
                    }
                    fileChooser.dispose();
                    if (fileName != null) {
                        game.setScreen(new LoadSaveConfirmation(game, skin, fileName));
                    } else {
                        game.setScreen(new GameScreen(game, skin));
                    }
                }
                loadGameButton.toggle();
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
        stage.addActor(loadGameButton);
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
    public void resume() {
        Gdx.input.setInputProcessor(stage);
        if (loadGameButton.isChecked()) {
            loadGameButton.toggle();
        }
        if (newGameButton.isChecked()) {
            newGameButton.toggle();
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
