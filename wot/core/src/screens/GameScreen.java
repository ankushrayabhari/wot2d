package screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.DesktopInput;
import com.mygdx.game.WorldOfTanks;

import map.GameMap;
import tank.TankReader;
import worldobject.Tank;
import worldobject.WorldObject;

public class GameScreen extends ScreenAdapter {

    private final float pixels_per_meter = 50;
    
    private WorldOfTanks game;
    private SpriteBatch batch;
    private Sprite hullSprite, turretSprite;
    private Texture hullImg, turretImg;
    private World world;
    private DesktopInput input;
    private OrthographicCamera camera;
    private GameMap map;
    private Tank tank;
    private ArrayList<WorldObject> worldObjects;

    public GameScreen(WorldOfTanks t) {
        game = t;
        batch = new SpriteBatch();
        hullImg = new Texture("data/t34hull.png");
        hullSprite = new Sprite(hullImg);
        turretImg = new Texture("data/t34turret.png");
        turretSprite = new Sprite(turretImg);
        input = new DesktopInput(t);
        Gdx.input.setInputProcessor(input);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), 
                Gdx.graphics.getHeight());
        
        worldObjects = new ArrayList<WorldObject>();
        Sprite[] tankSprites = {hullSprite, turretSprite};

        world = new World(new Vector2(0, 0f), true);
        map = new GameMap(12, 6, world, camera, pixels_per_meter);
        worldObjects.add(new Tank(world, camera, map, 
                tankSprites, input, pixels_per_meter));
        Iterator<WorldObject> iterator = worldObjects.iterator();
        while (iterator.hasNext()) {
            WorldObject wobj = iterator.next();
            wobj.setupObject();
            if (wobj instanceof Tank) {
                tank = (Tank) wobj;
            }
        }
        try {
            TankReader tank = new TankReader("M4 Sherman");
//            System.out.println(tank);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateWorld(float delta) {
        Iterator<WorldObject> iterator = worldObjects.iterator();
        while (iterator.hasNext()) {
            WorldObject obj = iterator.next();
            obj.updateObject();
        }
        world.step(delta, 8, 5);
    }

    @Override
    public void render(float delta) {
        updateWorld(delta);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.x = tank.getX() + tank.getWidth() / 2;
        camera.position.y = tank.getY() + tank.getHeight() / 2;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        map.draw(batch, camera);
        Iterator<WorldObject> iterator = worldObjects.iterator();
        while (iterator.hasNext()) {
            WorldObject obj = iterator.next();
            obj.drawObject(batch);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        hullImg.dispose();
        world.dispose();
    }
}
