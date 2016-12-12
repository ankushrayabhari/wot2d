package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class GUITemplate extends ScreenAdapter {
    public static final int BUTTON_WIDTH = 450;
    public static final int BUTTON_HEIGHT = 105;

    private Skin skin;
    private Image background;
    private Stage stage;

    public GUITemplate() {

    }

    public static final void setDefaultSizes(Button... buttons) {
        for (Button b : buttons)
            b.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }
}
