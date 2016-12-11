package worldobject;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;

import map.GameMap;
import screens.GameScreen;

public class EnemyTank extends TankObject {

    GameScreen screen;
    PlayerTank player;
    private final float forceMultiplier = 18; // actual gameplay value = 6
    private final float torqueMultiplier = 0.5f; // old value = 0.3f
    private OrthographicCamera camera;
    
    public EnemyTank(World world, OrthographicCamera camera, GameMap map, Sprite[] sprites, float pixels_per_meter,
            GameScreen screen, float x, float y) {
        super(world, camera, map, sprites, pixels_per_meter, screen, x, y);
        this.screen = screen;
        this.camera = camera;
        player = screen.getPlayer();
        setUserData(getHull(), getTurret());
    }

    @Override
    public float getHullTorque() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getTurretTorque() {
        return turretRotateDirection(getTurret()) * torqueMultiplier;
    }

    @Override
    public float getHullForceX() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getHullForceY() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isShooting() {
        return (GameMap.withinRenderRange(camera, getX(), getY()));
    }

    @Override
    public float getAimingAngleInRad() {
        return (float) ((Math.atan2(player.getY() - getY(), player.getX() - 
                getX()) + 2 * Math.PI) % (2 * Math.PI));
    }

}
