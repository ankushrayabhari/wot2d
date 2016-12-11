package worldobject;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.DesktopInput;

import map.GameMap;
import screens.GameScreen;

public class PlayerTank extends TankObject {
    
    private DesktopInput input;
    private final float forceMultiplier = 18; // actual gameplay value = 6
    private final float torqueMultiplier = 0.5f; // old value = 0.3f
    
    public PlayerTank(World world, 
            OrthographicCamera camera, GameMap map, Sprite[] sprites, 
            DesktopInput input, float pixels_per_meter, GameScreen screen) {
        super(world, camera, map, sprites, pixels_per_meter, screen, 1500, 1500);
        this.input = input;
        setUserData("playerhull", "playerturret");
    }

    @Override
    public String getUserData() {
        return getHull().getUserData().toString() + " " + getTurret().getUserData().toString();
    }

    @Override
    public float getTurretAngleInRad() {
        return input.getMouseAngleInRad();
    }

    @Override
    public float getHullTorque() {
        return input.getWasdTorqueDirection() * torqueMultiplier;
    }

    @Override
    public float getTurretTorque() {
        return turretRotateDirection(getTurret()) * torqueMultiplier;
    }

    @Override
    public float getHullForceX() {
        float forceX = 0;
        if (input.isAccelerating()) {
            forceX += (float)(forceMultiplier * 
                    Math.cos(getHull().getAngle()));
        }
        if (input.isReversing()) {
            forceX += (float)(-forceMultiplier * 1.5f / 2 *
                    Math.cos(getHull().getAngle()));
        }
        return forceX;
    }

    @Override
    public float getHullForceY() {
        float forceY = 0;
        if (input.isAccelerating()) {
            forceY += (float)(forceMultiplier * 
                    Math.sin(getHull().getAngle()));
        }
        if (input.isReversing()) {
            forceY += (float)(-forceMultiplier * 1.5f / 2 *
                    Math.sin(getHull().getAngle()));
        }
        return forceY;
    }

    @Override
    public boolean isShooting() {
        return input.isShooting();
    }

    @Override
    public float getAimingAngleInRad() {
        return input.getMouseAngleInRad();
    }

}
