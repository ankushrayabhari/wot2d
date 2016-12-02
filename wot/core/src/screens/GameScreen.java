package screens;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.DesktopInput;
import com.mygdx.game.WorldOfTanks;

import tank.Tank;

public class GameScreen extends ScreenAdapter {

    private final float forceMultiplier = 20;
    private final float pixels_per_meter = 100;
    
    private WorldOfTanks game;
    private SpriteBatch batch;
    private Sprite sprite;
    private Texture img;
    private World world;
    private Body body;
    private DesktopInput input;

    public GameScreen(WorldOfTanks t) {
        game = t;
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        sprite = new Sprite(img);
        input = new DesktopInput(t);
        Gdx.input.setInputProcessor(input);

        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);

        world = new World(new Vector2(0, 0f), true);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(sprite.getX() / pixels_per_meter, 
                sprite.getY() / pixels_per_meter);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2 / pixels_per_meter, 
                sprite.getHeight() / 2 / pixels_per_meter);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture = body.createFixture(fixtureDef);

        shape.dispose();
        try {
            Tank tank = new Tank("M4 Sherman");
//            System.out.println(tank);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateWorld(float delta) {
        body.applyForceToCenter(input.getForce().getX() * forceMultiplier, 
                input.getForce().getY() * forceMultiplier, true);
        world.step(delta, 6, 2);
    }

    @Override
    public void render(float delta) {
        updateWorld(delta);
        sprite.setPosition(body.getPosition().x * pixels_per_meter, 
                body.getPosition().y * pixels_per_meter);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(sprite, sprite.getX(), sprite.getY());
        batch.end();
    }

    @Override
    public void dispose() {
        img.dispose();
        world.dispose();
    }
}
