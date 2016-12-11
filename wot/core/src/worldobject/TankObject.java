package worldobject;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.DesktopInput;

import map.GameMap;
import screens.GameScreen;

public abstract class TankObject extends WorldObject {

    private float pixels_per_meter;
    private final float forceMultiplier = 18; // actual gameplay value = 6
    private final float torqueMultiplier = 0.5f; // old value = 0.3f
    private final float kFriction = 0.45f;
    private final float torqueFriction = 0.15f;
    private Sprite[] tankSprite;
    private Body[] bodies;
    private Body hull;
    private Body turret;
    private BodyDef[] bodyDef;
    private float timeElapsed = 0;
    private GameScreen screen;
    private OrthographicCamera camera;
    private World world;
    private GameMap map;
    private Sprite shellSprite;
    
    public TankObject(World world, 
            OrthographicCamera camera, GameMap map, Sprite[] sprites, 
            float pixels_per_meter, GameScreen screen) {
        super(world, camera, map, pixels_per_meter);
        this.world = world;
        this.camera = camera;
        this.map = map;
        this.pixels_per_meter = pixels_per_meter;
        this.screen = screen;
        tankSprite = sprites;
    }

    @Override
    public String getUserData() {
        return bodies[0].getUserData().toString() + " " + bodies[1].getUserData().toString();
    }

    @Override
    public float getX() {
        return tankSprite[0].getX();
    }

    @Override
    public float getY() {
        return tankSprite[0].getY();
    }

    @Override
    public float getHeight() {
        return tankSprite[0].getHeight();
    }

    @Override
    public float getWidth() {
        return tankSprite[0].getWidth();
    }

    @Override
    public void updateObject() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setupObject() {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawObject(Batch batch) {
        // TODO Auto-generated method stub

    }

}
