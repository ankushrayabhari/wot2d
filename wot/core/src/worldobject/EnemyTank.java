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
    private final float forceMultiplier = 9; // actual gameplay value = 6
    private final float torqueMultiplier = 0.8f; // old value = 0.3f
    private OrthographicCamera camera;
    private World world;
    private RayCaster rayCaster;
    private boolean chase = false;
    
    public EnemyTank(World world, OrthographicCamera camera, GameMap map, 
            Sprite[] sprites, float pixels_per_meter,
            GameScreen screen, float x, float y) {
        super(world, camera, map, sprites, pixels_per_meter, screen, x, y, 0, 0);
        this.screen = screen;
        this.camera = camera;
        this.world = world;
        player = screen.getPlayer();
        setUserData(getHull(), getTurret());
        rayCaster = new RayCaster();
    }
    
    public EnemyTank(World world, OrthographicCamera camera, GameMap map, 
            Sprite[] sprites, float pixels_per_meter,
            GameScreen screen, float x, float y, float hullAngle, float turretAngle) {
        super(world, camera, map, sprites, pixels_per_meter, screen, x, y, 
                hullAngle, turretAngle);
        this.screen = screen;
        this.camera = camera;
        this.world = world;
        player = screen.getPlayer();
        setUserData(getHull(), getTurret());
        rayCaster = new RayCaster();
    }
    
    public boolean clearPath() {
        world.rayCast(rayCaster, getHull().getPosition(), 
                player.getHull().getPosition());
        rayCaster.setMinFraction(10000);
        if (rayCaster.getFirstFixture() != null) {
            return (rayCaster.getFirstFixture().getFilterData().categoryBits == 0x0001);
        }
        return false;
    }
    
    public float getDistance() {
        return (float) (Vector2.dst2(getHull().getPosition().x, 
                getHull().getPosition().y, 
                player.getHull().getPosition().x, player.getHull().getPosition().y));
    }

    @Override
    public float getHullTorque() {
        if (GameMap.withinRenderRange(camera, getX(), getY()) && clearPath()) {
            chase = true;
        } else {
            chase = false;
        }
        float finalAngle = (float) ((Math.atan2(player.getY() - getY(), player.getX() - 
                getX()) + 2 * Math.PI) % (2 * Math.PI));
        float angle = getHull().getAngle();
        while (angle < 0) {
            angle += Math.PI * 2;
        }
        if (angle >= Math.PI * 2) {
            angle %= Math.PI * 2;
        }
        while (finalAngle < 0) {
            finalAngle += Math.PI * 2;
        }
        if (finalAngle >= Math.PI * 2) {
            finalAngle %= Math.PI * 2;
        }
        double deltaThreshold = Math.PI / 4;
        if (chase) {
            deltaThreshold = 0;
        }
        if (Math.abs(finalAngle - angle) > deltaThreshold && 
                (GameMap.withinRenderRange(camera, getX(), getY()) || clearPath())) {
            if (angle >= Math.PI && angle <= Math.PI * 2) {
                if (finalAngle >= angle || finalAngle <= angle - Math.PI) {
                    return 1;
                }
                return -1;
            } else if (angle >= Math.PI / 2 && angle <= Math.PI) {
                if (finalAngle >= angle && finalAngle <= angle + Math.PI) {
                    return 1;
                }
                return -1;
            } else {
                if (finalAngle <= angle + Math.PI && finalAngle >= angle) {
                    return 1;
                }
                return -1;
            }
        }
        return 0;
    }

    @Override
    public float getTurretTorque() {
        return turretRotateDirection(getTurret()) * torqueMultiplier;
    }

    @Override
    public float getHullForceX() {
        float forceX = 0;
        float finalAngle = (float) ((Math.atan2(player.getY() - getY(), player.getX() - 
                getX()) + 2 * Math.PI) % (2 * Math.PI));
        float angle = getHull().getAngle();
        while (angle < 0) {
            angle += Math.PI * 2;
        }
        if (angle >= Math.PI * 2) {
            angle %= Math.PI * 2;
        }
        while (finalAngle < 0) {
            finalAngle += Math.PI * 2;
        }
        if (finalAngle >= Math.PI * 2) {
            finalAngle %= Math.PI * 2;
        }
        if (chase == true) {
            if (Math.abs(finalAngle - angle) >= Math.PI / 2 || getDistance() < 20) {
                forceX = (float)(-forceMultiplier * 1.5f / 2 *
                        Math.cos(getHull().getAngle()));
            } else if (getDistance() > 40) {
                forceX = (float)(forceMultiplier * 
                        Math.cos(getHull().getAngle()));
            }
        }
        return forceX;
    }

    @Override
    public float getHullForceY() {
        float forceY = 0;
        float finalAngle = (float) ((Math.atan2(player.getY() - getY(), player.getX() - 
                getX()) + 2 * Math.PI) % (2 * Math.PI));
        float angle = getHull().getAngle();
        while (angle < 0) {
            angle += Math.PI * 2;
        }
        if (angle >= Math.PI * 2) {
            angle %= Math.PI * 2;
        }
        while (finalAngle < 0) {
            finalAngle += Math.PI * 2;
        }
        if (finalAngle >= Math.PI * 2) {
            finalAngle %= Math.PI * 2;
        }
        if (chase == true) {
            if (Math.abs(finalAngle - angle) >= Math.PI / 2 || getDistance() < 20) {
                forceY = (float)(-forceMultiplier * 1.5f / 2 *
                        Math.sin(getHull().getAngle()));
            } else if (getDistance() > 40) {
                forceY = (float)(forceMultiplier * 
                        Math.sin(getHull().getAngle()));
            }
        }
        return forceY;
    }

    @Override
    public boolean isShooting() {
        return (GameMap.withinRenderRange(camera, getX(), getY()) && 
                clearPath());
    }

    @Override
    public float getAimingAngleInRad() {
        double angle = (Math.atan2(player.getY() - getY(), player.getX() - 
                getX()) + 2 * Math.PI) % (2 * Math.PI);
        while (angle < 0) {
            angle += Math.PI * 2;
        }
        if (angle >= Math.PI * 2) {
            angle %= Math.PI * 2;
        }
        return (float) angle;
    }
    
    @Override
    public String toString() {
        return "enemy_tank " + super.toString();
    }

}
