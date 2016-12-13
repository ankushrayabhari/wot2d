package screens;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
    
    // TODO: 1. save files 2. smoke and explosions 3. aiming reticle + reloading
    // 4. inaccuracy depending on velocity
    
    private class SaveFileReader {
        
        private List<WorldObject> list;
        
        public SaveFileReader(String fileName) throws IOException {
            list = new ArrayList<WorldObject>();
            Sprite[] playerSprites = {playerHullSprite, playerTurretSprite};
            FileReader fr = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fr);
            while (reader.ready()) {
                String[] lineTokens = reader.readLine().split(" ");
                if (lineTokens[0].equals("player_tank")) {
                    PlayerTank tank = new PlayerTank(world, camera, map, 
                            playerSprites, input, pixels_per_meter, GameScreen.this,
                            Float.parseFloat(lineTokens[6]), Float.parseFloat(lineTokens[9]),
                            Float.parseFloat(lineTokens[12]), Float.parseFloat(lineTokens[15]));
                    tank.setHealth((int) Float.parseFloat(lineTokens[3]));
                    list.add(tank);
                    playerTank = tank;
                } else if (lineTokens[0].equals("enemy_tank")) {
                    EnemyTank tank = new EnemyTank(world, camera, map, 
                            new Sprite[]{new Sprite(enemyHullImg), 
                            new Sprite(enemyTurretImg)}, pixels_per_meter, GameScreen.this, 
                            Float.parseFloat(lineTokens[6]), Float.parseFloat(lineTokens[9]),
                            Float.parseFloat(lineTokens[12]), Float.parseFloat(lineTokens[15]));
                    tank.setHealth((int) Float.parseFloat(lineTokens[3]));
                    list.add(tank);
                } else if (lineTokens[0].equals("shell")) {
                    Shell shell = new Shell(null, world, camera, map,
                            new Sprite(shellTexture), pixels_per_meter, 
                            Float.parseFloat(lineTokens[3]), Float.parseFloat(lineTokens[6]),
                            Float.parseFloat(lineTokens[9]));
                    list.add(shell);
                } 
            }
            reader.close();
            fr.close();
        }
        
        public List<WorldObject> getObjectList() {
            return list;
        }
    }
    
    private WorldOfTanks game;
    private SpriteBatch batch;
    private Sprite playerHullSprite, playerTurretSprite;
    private Texture playerHullImg, playerTurretImg, enemyHullImg, 
        enemyTurretImg, shellTexture;
    private World world;
    private DesktopInput input;
    private OrthographicCamera camera;
    private GameMap map;
    private PlayerTank playerTank;
    private ArrayList<WorldObject> miscObjects;
    private ArrayList<TankObject> tankObjects;
    private int numEnemies = 3;
    private float gameOverTimer = 0;
    private float saveGameTimer = 0;
    Matrix4 debugMatrix;
    private Skin skin;
    Box2DDebugRenderer debugRenderer;

    public GameScreen(WorldOfTanks t, Skin s) {
        game = t;
        skin = s;
        batch = new SpriteBatch();
        playerHullImg = new Texture("data/t34hull.png");
        playerHullSprite = new Sprite(playerHullImg);
        shellTexture = new Texture("data/shell.png");
        playerTurretImg = new Texture("data/t34turret.png");
        enemyHullImg = new Texture("data/panzer4hull.png");
        enemyTurretImg = new Texture("data/panzer4turret.png");
        playerTurretSprite = new Sprite(playerTurretImg);
        input = new DesktopInput(t);
        Gdx.input.setInputProcessor(input);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), 
                Gdx.graphics.getHeight());
        
        miscObjects = new ArrayList<WorldObject>();
        Sprite[] playerSprites = {playerHullSprite, playerTurretSprite};
        Sprite[][] enemySprites = new Sprite[numEnemies][2];
        for (int i = 0; i < enemySprites.length; i++) {
            enemySprites[i] = new Sprite[]{new Sprite(enemyHullImg), 
                    new Sprite(enemyTurretImg)};
        }
        tankObjects = new ArrayList<TankObject>();
        world = new World(new Vector2(0, 0f), true);
        world.setContactListener(new CollisionListener());
        playerTank = new PlayerTank(world, camera, map, 
                playerSprites, input, pixels_per_meter, this);
        tankObjects.add(playerTank);
        for (int i = 0; i < enemySprites.length; i++) {
            EnemyTank enemy = new EnemyTank(world, camera, map, 
                    enemySprites[i], pixels_per_meter, this, 2000 + (int) 
                    (Math.random() * 2000), 1000 + (500 + (int) (Math.random() 
                            * 100)) * i);
            tankObjects.add(enemy);
        }
        map = new GameMap(this, 12, 6, world, camera, pixels_per_meter);
        Iterator<WorldObject> miscIterator = miscObjects.iterator();
        while (miscIterator.hasNext()) {
            WorldObject wobj = miscIterator.next();
            wobj.setupObject();
        }
        Iterator<TankObject> tankIterator = tankObjects.iterator();
        while (tankIterator.hasNext()) {
            TankObject wobj = tankIterator.next();
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
    
    public GameScreen(WorldOfTanks t, Skin s, String fileName) throws IOException {
        game = t;
        skin = s;
        batch = new SpriteBatch();
        playerHullImg = new Texture("data/t34hull.png");
        playerHullSprite = new Sprite(playerHullImg);
        playerTurretImg = new Texture("data/t34turret.png");
        shellTexture = new Texture("data/shell.png");
        enemyHullImg = new Texture("data/panzer4hull.png");
        enemyTurretImg = new Texture("data/panzer4turret.png");
        playerTurretSprite = new Sprite(playerTurretImg);
        input = new DesktopInput(t);
        Gdx.input.setInputProcessor(input);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), 
                Gdx.graphics.getHeight());

        world = new World(new Vector2(0, 0f), true);
        world.setContactListener(new CollisionListener());
        miscObjects = new ArrayList<WorldObject>();
        tankObjects = new ArrayList<TankObject>();
        map = new GameMap(this, 12, 6, world, camera, pixels_per_meter);
        
        numEnemies = 0;
        SaveFileReader reader = new SaveFileReader(fileName);
        List<WorldObject> objects = reader.getObjectList();
        Iterator<WorldObject> iter = objects.iterator();
        while (iter.hasNext()) {
            WorldObject obj = iter.next();
            if (obj instanceof TankObject) {
                tankObjects.add((TankObject) obj);
                if (obj instanceof EnemyTank) {
                    numEnemies++;
                }
            }
        }
        
        Sprite[][] enemySprites = new Sprite[numEnemies][2];
        for (int i = 0; i < enemySprites.length; i++) {
            enemySprites[i] = new Sprite[]{new Sprite(enemyHullImg), 
                    new Sprite(enemyTurretImg)};
        }
        Iterator<WorldObject> iterator = miscObjects.iterator();
        while (iterator.hasNext()) {
            WorldObject wobj = iterator.next();
            wobj.setupObject();
        }
        Iterator<TankObject> tankIterator = tankObjects.iterator();
        while (tankIterator.hasNext()) {
            TankObject wobj = tankIterator.next();
            wobj.setupObject();
        }
        debugRenderer = new Box2DDebugRenderer();
    }
    
    public void addWorldObject(WorldObject wobj) {
        miscObjects.add(wobj);
    }
    
    public PlayerTank getPlayer() {
        return playerTank;
    }
    
    private void saveGame() {
        try {
            FileWriter fileWriter = new FileWriter(new File("save" + 
        System.currentTimeMillis() + ".wot2dsave"));
            BufferedWriter writer = new BufferedWriter(fileWriter);
            Iterator<WorldObject> objectIter = miscObjects.iterator();
            while (objectIter.hasNext()) {
                WorldObject obj = objectIter.next();
                writer.write(obj.toString());
                writer.newLine();
            }
            writer.flush();
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateWorld(float delta) {
        world.step(1/60f, 8, 5);
        int numDeadEnemies = 0;
        Iterator<TankObject> tankIter = tankObjects.iterator();
        while (tankIter.hasNext()) {
            TankObject obj = tankIter.next();
            if (obj.getHealth() > 0) {
                obj.shoot();
            } else if (obj instanceof EnemyTank) {
                numDeadEnemies++;
            } else if (obj instanceof PlayerTank) {
                gameOverTimer += delta;
                if (gameOverTimer > 3) {
                    game.setScreen(new DefeatMenu(game, skin));
                }
            }
            obj.updateObject();
        }
        if (numDeadEnemies >= numEnemies) {
            gameOverTimer += delta;
            if (gameOverTimer > 3) {
                game.setScreen(new VictoryMenu(game, skin));
            }
        }
        Iterator<WorldObject> objectIter = miscObjects.iterator();
        while (objectIter.hasNext()) {
            WorldObject obj = objectIter.next();
            obj.updateObject();
            if (obj instanceof Shell && ((Shell) obj).hasHitObject()) {
                world.destroyBody(obj.getUserData());
                objectIter.remove();
            }
        }
    }

    @Override
    public void render(float delta) {
        updateWorld(delta);
        if (input.saveFile() && saveGameTimer >= 5 && 
                playerTank.getHealth() > 0) {
            saveGame();
            saveGameTimer = 0;
        }
        saveGameTimer += delta;
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
        Iterator<WorldObject> miscIterator = miscObjects.iterator();
        while (miscIterator.hasNext()) {
            WorldObject obj = miscIterator.next();
            obj.drawObject(batch);
        }
        Iterator<TankObject> tankIterator = tankObjects.iterator();
        while (tankIterator.hasNext()) {
            WorldObject obj = tankIterator.next();
            obj.drawObject(batch);
        }
        batch.end();
        debugRenderer.render(world, debugMatrix);
    }

    @Override
    public void dispose() {
        playerHullImg.dispose();
        enemyHullImg.dispose();
        playerTurretImg.dispose();
        enemyTurretImg.dispose();
        world.dispose();
        batch.dispose();
        debugRenderer.dispose();
    }
}
