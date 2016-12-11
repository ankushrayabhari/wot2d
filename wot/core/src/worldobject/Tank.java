package worldobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.game.DesktopInput;

import map.GameMap;
import screens.GameScreen;

public class Tank extends WorldObject {
    
    private DesktopInput input;
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
    
    public Tank(World world, 
            OrthographicCamera camera, GameMap map, Sprite[] sprites, 
            DesktopInput input, float pixels_per_meter, GameScreen screen) {
        super(world, camera, map, pixels_per_meter);
        this.input = input;
        this.world = world;
        this.camera = camera;
        this.map = map;
        this.pixels_per_meter = pixels_per_meter;
        this.screen = screen;
        tankSprite = sprites;
        Texture shellTexture = new Texture("data/shell.png");
        shellSprite = new Sprite(shellTexture);
        bodyDef = new BodyDef[2];
        for (int i = 0; i < bodyDef.length; i++) {
            bodyDef[i] = new BodyDef();
            bodyDef[i].type = BodyDef.BodyType.DynamicBody;
            bodyDef[i].position.set((tankSprite[i].getX() + 1500) / pixels_per_meter, 
                    (tankSprite[i].getY() + 1500) / pixels_per_meter);
        }
        hull = world.createBody(bodyDef[0]);
        bodyDef[1].position.set((tankSprite[1].getX() + 1500) / pixels_per_meter, 
                (tankSprite[1].getY() + 1500) / pixels_per_meter);
        turret = world.createBody(bodyDef[1]);
        bodies = new Body[2];
        bodies[0] = hull;
        bodies[1] = turret;
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = bodies[0];
        jointDef.bodyB = bodies[1];
        jointDef.localAnchorA.x = tankSprite[0].getWidth() / pixels_per_meter * 4f / 59;
        jointDef.maxMotorTorque = 5;

        RevoluteJoint joint = (RevoluteJoint) world.createJoint(jointDef);
        bodies[0].setUserData("playerhull");
        bodies[1].setUserData("playerturret");
    }
    
    private int turretRotateDirection(Body body) {
        float aimingAngle = input.getMouseAngleInRad();
        if (aimingAngle < 0) {
            return 0;
        }
        while (body.getAngle() < 0) {
            body.setTransform(body.getPosition(), body.getAngle() + (float) 
                    (Math.PI * 2));
        }
        if (body.getAngle() >= Math.PI * 2) {
            body.setTransform(body.getPosition(), body.getAngle() % (float) 
                    (Math.PI * 2));
        }
        float currentAngle = body.getAngle();
        if (aimingAngle - currentAngle > 0 && aimingAngle - currentAngle <= Math.PI) {
            return 1;
        } else if (currentAngle - aimingAngle > 0 && currentAngle - aimingAngle >= Math.PI) {
            return 1;
        }
        return -1;
    }
    
    public void shoot() {
        if (input.isShooting() && timeElapsed > 0.1f) {
            timeElapsed = 0;
            float turretDistance = (float) 
                    Math.sqrt(Math.pow(tankSprite[0].getHeight() * (0.5 - 16f / 31), 2) + 
                    Math.pow(tankSprite[0].getWidth() * (0.5 - 36f / 61), 2));
            float x = tankSprite[0].getX() + tankSprite[0].getWidth() / 2 
                    + (float) (turretDistance * Math.cos(bodies[0].getAngle())) 
                    - tankSprite[1].getWidth() * 16f / 59 + tankSprite[1].getWidth() * 51f / 59 * 
                    (float) (Math.cos(bodies[1].getAngle()));
            float y = tankSprite[0].getY() + tankSprite[0].getHeight() / 2 + (float) 
                    (turretDistance * Math.sin(bodies[0].getAngle())) - 
                    tankSprite[1].getHeight() / 2 + tankSprite[1].getWidth() * 51f / 59 * 
                    (float) (Math.sin(bodies[1].getAngle()));
            Shell shell = new Shell(world, camera, map, shellSprite, 
                    pixels_per_meter, x, y, bodies[1].getAngle());
            shell.setupObject();
            screen.addWorldObject(shell);
        }
    }

    @Override
    public void updateObject() {
        timeElapsed += 1/60f;
//        System.out.println(timeElapsed);
        float hullTorque = input.getWasdTorqueDirection() * torqueMultiplier;
        if (input.isAccelerating()) {
            bodies[0].applyForceToCenter((float)(forceMultiplier * 
                    Math.cos(bodies[0].getAngle())), (float)(forceMultiplier * 
                            Math.sin(bodies[0].getAngle())), true);
        }
        if (input.isReversing()) {
            bodies[0].applyForceToCenter((float)(-forceMultiplier * 1.5f / 2 *
                    Math.cos(bodies[0].getAngle())), (float)(-forceMultiplier * 1.5f / 2 *
                            Math.sin(bodies[0].getAngle())), true);
        }
        if (hullTorque != 0) {
            if (bodies[0].getAngularVelocity() < 35) {
                bodies[0].applyTorque(hullTorque, true);
            }
        }
        if (bodies[0].getAngularVelocity() != 0) {
            bodies[0].applyTorque(-bodies[0].getAngularVelocity() * bodies[0].getMass() * 
                    9.8f * torqueFriction, true);
        }
        if (!bodies[0].getLinearVelocity().isZero()) {
            bodies[0].applyForceToCenter(- bodies[0].getLinearVelocity().x * 
                    bodies[0].getMass() * 9.8f * kFriction, 
                    - bodies[0].getLinearVelocity().y * bodies[0].getMass() * 
                    9.8f * kFriction, true);
        }
        float turretTorque = turretRotateDirection(turret) * torqueMultiplier;
        if (turretTorque != 0) {
            if (bodies[1].getAngularVelocity() < 39) {
                if (!input.closeAngle((float) 
                        Math.toDegrees((bodies[1].getAngle() + 360) % 360), false)) {
                    bodies[1].applyTorque(turretTorque, true);
                } else {
                    bodies[1].applyTorque(turretTorque * 3f / 4, true);
                }
            }
        }
        if (bodies[1].getAngularVelocity() != 0) {
            bodies[1].applyTorque(-bodies[1].getAngularVelocity() * bodies[1].getMass() * 
                    9.8f * torqueFriction, true);
        }
        
        tankSprite[0].setPosition(bodies[0].getPosition().x * pixels_per_meter 
                - tankSprite[0].getWidth() / 2, 
                bodies[0].getPosition().y * pixels_per_meter -
                tankSprite[0].getHeight() / 2);
        tankSprite[0].setRotation((float) (Math.toDegrees(bodies[0].getAngle())));
        
        float turretDistance = (float) 
              Math.sqrt(Math.pow(tankSprite[0].getHeight() * (0.5 - 16f / 31), 2) + 
              Math.pow(tankSprite[0].getWidth() * (0.5 - 36f / 61), 2));
        
        tankSprite[1].setPosition(tankSprite[0].getX() + tankSprite[0].getWidth() / 2 
                + (float) (turretDistance * Math.cos(bodies[0].getAngle())) - tankSprite[1].getWidth() * 16f / 59, 
                tankSprite[0].getY() + tankSprite[0].getHeight() / 2 + (float) 
                (turretDistance * Math.sin(bodies[0].getAngle())) - tankSprite[1].getHeight() / 2);
        tankSprite[1].setRotation((float) (Math.toDegrees(bodies[1].getAngle())));
    }

    @Override
    public void setupObject() {
        tankSprite[0].setPosition(Gdx.graphics.getWidth() / 2 - tankSprite[0].getWidth() / 2,
                Gdx.graphics.getHeight() / 2);

        PolygonShape hullShape = new PolygonShape();
        hullShape.setAsBox(tankSprite[0].getWidth() / 2 / pixels_per_meter, 
                tankSprite[0].getHeight() / 2 / pixels_per_meter);
        
        float turretDistance = (float) 
                Math.sqrt(Math.pow(tankSprite[0].getHeight() * (0.5 - 16f / 31), 2) + 
                Math.pow(tankSprite[0].getWidth() * (0.5 - 36f / 61), 2));
          
        tankSprite[1].setPosition(tankSprite[0].getX() + tankSprite[0].getWidth() / 2 
                + (float) (turretDistance * Math.cos(bodies[0].getAngle())) - tankSprite[1].getWidth() * 16f / 59, 
                tankSprite[0].getY() + tankSprite[0].getHeight() / 2 + (float) 
                (turretDistance * Math.sin(bodies[0].getAngle())) - tankSprite[1].getHeight() / 2);
        
        tankSprite[1].setOrigin(tankSprite[1].getWidth() * 15f / 59, tankSprite[1].getHeight() * 16f / 31);
        PolygonShape turretShape = new PolygonShape();
        turretShape.setAsBox(tankSprite[1].getWidth() * 14f / 59 / pixels_per_meter, 
                tankSprite[1].getHeight() / 2 / pixels_per_meter);
        
        FixtureDef hullFixtureDef = new FixtureDef();
        hullFixtureDef.shape = hullShape;
        hullFixtureDef.density = 1f;

        FixtureDef turretFixtureDef = new FixtureDef();
        turretFixtureDef.shape = turretShape;
        turretFixtureDef.density = 1f;

        Fixture turretFixture = bodies[1].createFixture(turretFixtureDef);
        
        Fixture hullFixture = bodies[0].createFixture(hullFixtureDef);

        hullShape.dispose();
        turretShape.dispose();
    }

    @Override
    public void drawObject(Batch batch) {
        batch.draw(tankSprite[0], tankSprite[0].getX(), tankSprite[0].getY(),tankSprite[0].getOriginX(),
                tankSprite[0].getOriginY(),
         tankSprite[0].getWidth(),tankSprite[0].getHeight(),tankSprite[0].getScaleX(),tankSprite[0].
                         getScaleY(),tankSprite[0].getRotation());
        batch.draw(tankSprite[1], tankSprite[1].getX(), tankSprite[1].getY(), 
                tankSprite[1].getOriginX(), tankSprite[1].getOriginY(),
         tankSprite[1].getWidth(),tankSprite[1].getHeight(),tankSprite[1].getScaleX(),tankSprite[1].
                         getScaleY(),tankSprite[1].getRotation());
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

}
