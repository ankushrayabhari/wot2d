package screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.WorldOfTanks;

public class SplashScreen extends ScreenAdapter {
    private SpriteBatch batch;
    private Texture splashImg;
    private float time;
    private WorldOfTanks game;

    public SplashScreen(WorldOfTanks t) {
        batch = new SpriteBatch();
        time = 0;
        splashImg = new Texture("images/wotsplash.jpg");
        game = t;
    }

    @Override
    public void render(float delta) {
        time += delta;
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(splashImg, Gdx.graphics.getWidth() / 2 - splashImg.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - splashImg.getHeight() / 2);
        batch.end();
        if (time > 1) {
            game.setScreen(new MainMenu(game, 
                    new Skin( Gdx.files.internal("ui/uiskin.json" ))));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        splashImg.dispose();
    }
}
