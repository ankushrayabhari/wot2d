package worldobject;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

import map.GameMap;
import screens.GameScreen;

public class EnemyTank extends TankObject {

    private class RayCaster implements RayCastCallback {
        
        private Fixture closestFixture = null;
        private float minFraction = 10000;
        
        public Fixture getFirstFixture() {
            return closestFixture;
        }
        
        public void setMinFraction(float newFraction) {
            minFraction = newFraction;
        }
        
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, 
                Vector2 normal, float fraction) {
            if (fraction < this.minFraction) {
                this.closestFixture = fixture;
                this.minFraction = fraction;
            }
            return 1;
        }
        
    }
    
    GameScreen screen;
    PlayerTank player;
    private final float forceMultiplier = 18; // actual gameplay value = 6
    private final float torqueMultiplier = 0.5f; // old value = 0.3f
    private OrthographicCamera camera;
    private World world;
    private RayCaster rayCaster;
    
    public EnemyTank(World world, OrthographicCamera camera, GameMap map, 
            Sprite[] sprites, float pixels_per_meter,
            GameScreen screen, float x, float y) {
        super(world, camera, map, sprites, pixels_per_meter, screen, x, y);
        this.screen = screen;
        this.camera = camera;
        this.world = world;
        player = screen.getPlayer();
        setUserData(getHull(), getTurret());
        rayCaster = new RayCaster();
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
        world.rayCast(rayCaster, getHull().getPosition(), 
                player.getHull().getPosition());
        rayCaster.setMinFraction(10000);
        if (rayCaster.getFirstFixture() != null) {
            return (GameMap.withinRenderRange(camera, getX(), getY()) && 
                    (rayCaster.getFirstFixture().getFilterData().categoryBits == 0x0001));
        }
        return false;
    }

    @Override
    public float getAimingAngleInRad() {
        return (float) ((Math.atan2(player.getY() - getY(), player.getX() - 
                getX()) + 2 * Math.PI) % (2 * Math.PI));
    }

}
