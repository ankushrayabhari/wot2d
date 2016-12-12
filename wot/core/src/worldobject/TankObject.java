package worldobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import map.GameMap;
import screens.GameScreen;

public abstract class TankObject extends WorldObject {

    private float pixels_per_meter;
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
    private int health;
    
    public TankObject(World world, 
            OrthographicCamera camera, GameMap map, Sprite[] sprites, 
            float pixels_per_meter, GameScreen screen, float x, float y) {
        super(world, camera, map, pixels_per_meter);
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
            bodyDef[i].position.set((tankSprite[i].getX() + x) / pixels_per_meter, 
                    (tankSprite[i].getY() + y) / pixels_per_meter);
        }
        hull = world.createBody(bodyDef[0]);
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
        health = 10;
    }
    
    
    public void setUserData(Body hullUserData, Body turretUserData) {
        bodies[0].setUserData(hullUserData);
        bodies[1].setUserData(turretUserData);
    }
    
    public Body getHull() {
        return bodies[0];
    }
    
    public Body getTurret() {
        return bodies[1];
    }
    
    public void loseHealth() {
        health -= 2;
    }
    
    public int getHealth() {
        return health;
    }
    
    public void shoot() {
        if (isShooting() && timeElapsed > 1f) {
            timeElapsed = 0;
            float turretDistance = (float) 
                    Math.sqrt(Math.pow(tankSprite[0].getHeight() * (0.5 - 16f / 31), 2) + 
                    Math.pow(tankSprite[0].getWidth() * (0.5 - 36f / 61), 2));
            float x = tankSprite[0].getX() + tankSprite[0].getWidth() / 2 
                    + (float) (turretDistance * Math.cos(bodies[0].getAngle())) 
                    + tankSprite[1].getWidth() * 42f / 59 * 
                    (float) (Math.cos(bodies[1].getAngle()));
            float y = tankSprite[0].getY() + tankSprite[0].getHeight() / 2 + (float) 
                    (turretDistance * Math.sin(bodies[0].getAngle()))
                    + tankSprite[1].getWidth() * 42f / 59 * 
                    (float) (Math.sin(bodies[1].getAngle()));
            Shell shell = new Shell(bodies[0], world, camera, map, shellSprite, 
                    pixels_per_meter, x, y, bodies[1].getAngle());
            shell.setupObject();
            screen.addWorldObject(shell);
        }
    }
    
    public boolean closeAngle(float angleInDegrees, float aimingAngle) {
        if (Math.abs(Math.toRadians(aimingAngle - angleInDegrees)) < 35) {
            return true;
        }
        return false;
    }
    
    public abstract float getHullTorque();
    
    public abstract float getTurretTorque();
    
    public abstract float getHullForceX();
    
    public abstract float getHullForceY();
    
    public abstract boolean isShooting();
    
    public abstract float getAimingAngleInRad();

    @Override
    public Body getUserData() {
        return bodies[0];
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
    
    public int turretRotateDirection(Body body) {
        float aimingAngle = getAimingAngleInRad();
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

    @Override
    public void updateObject() {
        timeElapsed += 1/60f;
//      System.out.println(timeElapsed);
      float hullTorque;
      if (health <= 0) {
          hullTorque = 0;
      } else {
          hullTorque = getHullTorque();
      }
      if (health > 0) {
          bodies[0].applyForceToCenter(getHullForceX(), getHullForceY(), true);
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
      float turretTorque;
      if (health <= 0) {
          turretTorque = 0;
      } else {
          turretTorque = getTurretTorque();
      }
      if (turretTorque != 0) {
          if (bodies[1].getAngularVelocity() < 39) {
              if (!closeAngle((float) 
                      Math.toDegrees((bodies[1].getAngle() + 360) % 360), getAimingAngleInRad())) {
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
        hullFixtureDef.filter.categoryBits = 0x0001;

        FixtureDef turretFixtureDef = new FixtureDef();
        turretFixtureDef.shape = turretShape;
        turretFixtureDef.density = 1f;
        turretFixtureDef.filter.categoryBits = 0x0001;

        Fixture turretFixture = bodies[1].createFixture(turretFixtureDef);
        
        Fixture hullFixture = bodies[0].createFixture(hullFixtureDef);
        
        turretFixture.setUserData(this);
        hullFixture.setUserData(this);

        hullShape.dispose();
        turretShape.dispose();
    }

    @Override
    public void drawObject(Batch batch) {
        if (GameMap.withinRenderRange(camera, getX(), getY())) {
            batch.draw(tankSprite[0], tankSprite[0].getX(), tankSprite[0].getY(),tankSprite[0].getOriginX(),
                    tankSprite[0].getOriginY(),
             tankSprite[0].getWidth(),tankSprite[0].getHeight(),tankSprite[0].getScaleX(),tankSprite[0].
                             getScaleY(),tankSprite[0].getRotation());
            if (health > 0) {
                batch.draw(tankSprite[1], tankSprite[1].getX(), tankSprite[1].getY(), 
                        tankSprite[1].getOriginX(), tankSprite[1].getOriginY(),
                 tankSprite[1].getWidth(),tankSprite[1].getHeight(),tankSprite[1].getScaleX(),tankSprite[1].
                                 getScaleY(),tankSprite[1].getRotation());
            }
        }
    }

}
