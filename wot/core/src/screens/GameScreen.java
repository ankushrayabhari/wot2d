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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.CollisionListener;
import com.mygdx.game.DesktopInput;
import com.mygdx.game.WorldOfTanks;

import map.GameMap;
import tank.TankReader;
import worldobject.EnemyTank;
import worldobject.PlayerTank;
import worldobject.Shell;
import worldobject.TankObject;
import worldobject.WorldObject;

public class GameScreen extends ScreenAdapter {

    private final float pixels_per_meter = 50;
    
    private WorldOfTanks game;
    private SpriteBatch batch;
    private Sprite hullSprite, turretSprite;
    private Texture playerHullImg, playerTurretImg, enemyHullImg, enemyTurretImg;
    private World world;
    private DesktopInput input;
    private OrthographicCamera camera;
    private GameMap map;
    private PlayerTank playerTank;
    private ArrayList<WorldObject> worldObjects;
    private ArrayList<TankObject> tankObjects;
    Matrix4 debugMatrix;
    Box2DDebugRenderer debugRenderer;

    public GameScreen(WorldOfTanks t) {
        game = t;
        batch = new SpriteBatch();
        playerHullImg = new Texture("data/t34hull.png");
        hullSprite = new Sprite(playerHullImg);
        playerTurretImg = new Texture("data/t34turret.png");
        enemyHullImg = new Texture("data/panzer4hull.png");
        enemyTurretImg = new Texture("data/panzer4turret.png");
        turretSprite = new Sprite(playerTurretImg);
        input = new DesktopInput(t);
        Gdx.input.setInputProcessor(input);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), 
                Gdx.graphics.getHeight());
        
        worldObjects = new ArrayList<WorldObject>();
        Sprite[] playerSprites = {hullSprite, turretSprite};
        Sprite[][] enemySprites = new Sprite[3][2];
        for (int i = 0; i < enemySprites.length; i++) {
            enemySprites[i] = new Sprite[]{new Sprite(enemyHullImg), 
                    new Sprite(enemyTurretImg)};
        }
        tankObjects = new ArrayList<TankObject>();
        world = new World(new Vector2(0, 0f), true);
        world.setContactListener(new CollisionListener());
        playerTank = new PlayerTank(world, camera, map, 
                playerSprites, input, pixels_per_meter, this);
        worldObjects.add(playerTank);
        tankObjects.add(playerTank);
        for (int i = 0; i < enemySprites.length; i++) {
            EnemyTank enemy = new EnemyTank(world, camera, map, 
                    enemySprites[i], pixels_per_meter, this, 3000, 1000 + 500 * i);
            worldObjects.add(enemy);
            tankObjects.add(enemy);
        }
        map = new GameMap(this, 12, 6, world, camera, pixels_per_meter);
        Iterator<WorldObject> iterator = worldObjects.iterator();
        while (iterator.hasNext()) {
            WorldObject wobj = iterator.next();
            wobj.setupObject();
        }
        debugRenderer = new Box2DDebugRenderer();
        try {
            TankReader tank = new TankReader("M4 Sherman");
//            System.out.println(tank);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void addWorldObject(WorldObject wobj) {
        worldObjects.add(wobj);
    }
    
    public PlayerTank getPlayer() {
        return playerTank;
    }

    private void updateWorld(float delta) {
        Iterator<TankObject> tankIter = tankObjects.iterator();
        while (tankIter.hasNext()) {
            TankObject obj = tankIter.next();
            if (obj.getHealth() > 0) {
                obj.shoot();
            }
        }
        Iterator<WorldObject> objectIter = worldObjects.iterator();
        while (objectIter.hasNext()) {
            WorldObject obj = objectIter.next();
            obj.updateObject();
            if (obj instanceof Shell && ((Shell) obj).hasHitObject()) {
                world.destroyBody(obj.getUserData());
                objectIter.remove();
            }
        }
        world.step(1/60f, 8, 5);
    }

    @Override
    public void render(float delta) {
        updateWorld(delta);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.x = playerTank.getX() + playerTank.getWidth() / 2;
        camera.position.y = playerTank.getY() + playerTank.getHeight() / 2;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(pixels_per_meter,
                pixels_per_meter, 0);
        batch.begin();
        map.draw(batch, camera);
        Iterator<WorldObject> iterator = worldObjects.iterator();
        while (iterator.hasNext()) {
            WorldObject obj = iterator.next();
            obj.drawObject(batch);
        }
        batch.end();
        debugRenderer.render(world, debugMatrix);
    }

    @Override
    public void dispose() {
        playerHullImg.dispose();
        world.dispose();
    }
}
