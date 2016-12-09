package screens;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.DesktopInput;
import com.mygdx.game.WorldOfTanks;

import map.Map;
import tank.TankReader;

public class GameScreen extends ScreenAdapter {

    private final float forceMultiplier = 12; // actual gameplay value = 6
    private final float torqueMultiplier = 0.3f;
    private final float pixels_per_meter = 50;
    private final float kFriction = 0.45f;
    private final float torqueFriction = 0.15f;
    
    private WorldOfTanks game;
    private SpriteBatch batch;
    private Sprite hullSprite, turretSprite;
    private Texture hullImg, turretImg;
    private World world;
    private Body hull, turret;
    private DesktopInput input;
    private OrthographicCamera camera;
    private Map map;

    public GameScreen(WorldOfTanks t) {
        game = t;
        batch = new SpriteBatch();
        hullImg = new Texture("data/t34hull.png");
        hullSprite = new Sprite(hullImg);
        turretImg = new Texture("data/t34turret.png");
        turretSprite = new Sprite(turretImg);
        map = new Map(12, 6);
        
        input = new DesktopInput(t);
        Gdx.input.setInputProcessor(input);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), 
                Gdx.graphics.getHeight());

        hullSprite.setPosition(Gdx.graphics.getWidth() / 2 - hullSprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);

        world = new World(new Vector2(0, 0f), true);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(hullSprite.getX() / pixels_per_meter, 
                hullSprite.getY() / pixels_per_meter);

        hull = world.createBody(bodyDef);

        PolygonShape hullShape = new PolygonShape();
        hullShape.setAsBox(hullSprite.getWidth() / 2 / pixels_per_meter, 
                hullSprite.getHeight() / 2 / pixels_per_meter);
        
        float turretDistance = (float) 
                Math.sqrt(Math.pow(hullSprite.getHeight() * (0.5 - 16f / 31), 2) + 
                Math.pow(hullSprite.getWidth() * (0.5 - 36f / 61), 2));
          
        turretSprite.setPosition(hullSprite.getX() + hullSprite.getWidth() / 2 
                + (float) (turretDistance * Math.cos(hull.getAngle())) - turretSprite.getWidth() * 16f / 59, 
                hullSprite.getY() + hullSprite.getHeight() / 2 + (float) 
                (turretDistance * Math.sin(hull.getAngle())) - turretSprite.getHeight() / 2);
        
        turretSprite.setOrigin(turretSprite.getWidth() * 15f / 59, turretSprite.getHeight() * 16f / 31);
        turret = world.createBody(bodyDef);
        PolygonShape turretShape = new PolygonShape();
        turretShape.setAsBox(turretSprite.getWidth() * 14f / 59 / pixels_per_meter, 
                turretSprite.getHeight() / 2 / pixels_per_meter);
        
        FixtureDef hullFixtureDef = new FixtureDef();
        hullFixtureDef.shape = hullShape;
        hullFixtureDef.density = 1f;

        FixtureDef turretFixtureDef = new FixtureDef();
        turretFixtureDef.shape = hullShape;
        turretFixtureDef.density = 1f;

        Fixture turretFixture = turret.createFixture(turretFixtureDef);
        
        Fixture hullFixture = hull.createFixture(hullFixtureDef);

        hullShape.dispose();
        turretShape.dispose();
        try {
            TankReader tank = new TankReader("M4 Sherman");
//            System.out.println(tank);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    private void updateWorld(float delta) {
        float hullTorque = input.getWasdTorqueDirection((float) 
                Math.toDegrees(hull.getAngle())) * torqueMultiplier;
        if (input.sameQuadrant(hull)) {
            hull.applyForceToCenter((float)(forceMultiplier * 
                    Math.cos(hull.getAngle())), (float)(forceMultiplier * 
                            Math.sin(hull.getAngle())), true);
        }
        if (input.isReversing()) {
            hull.applyForceToCenter((float)(-forceMultiplier * 1.5f / 2 *
                    Math.cos(hull.getAngle())), (float)(-forceMultiplier * 1.5f / 2 *
                            Math.sin(hull.getAngle())), true);
        }
        if (hullTorque != 0) {
            if (hull.getAngularVelocity() < 35) {
                if (!input.closeAngle((float) 
                        Math.toDegrees((hull.getAngle() + 360) % 360), true)) {
                    hull.applyTorque(hullTorque, true);
                } else {
                    hull.applyTorque(hullTorque / 2, true);
                }
            }
        }
        if (hull.getAngularVelocity() != 0) {
            hull.applyTorque(-hull.getAngularVelocity() * hull.getMass() * 
                    9.8f * torqueFriction, true);
        }
        if (!hull.getLinearVelocity().isZero()) {
            hull.applyForceToCenter(- hull.getLinearVelocity().x * 
                    hull.getMass() * 9.8f * kFriction, 
                    - hull.getLinearVelocity().y * hull.getMass() * 
                    9.8f * kFriction, true);
        }
        float turretTorque = turretRotateDirection(turret) * 2f * torqueMultiplier;
        if (turretTorque != 0) {
            if (turret.getAngularVelocity() < 39) {
                if (!input.closeAngle((float) 
                        Math.toDegrees((turret.getAngle() + 360) % 360), false)) {
                    turret.applyTorque(turretTorque, true);
                } else {
                    turret.applyTorque(turretTorque * 3f / 4, true);
                }
            }
        }
        if (turret.getAngularVelocity() != 0) {
            turret.applyTorque(-turret.getAngularVelocity() * turret.getMass() * 
                    9.8f * torqueFriction, true);
        }
        world.step(delta, 6, 2);
    }

    @Override
    public void render(float delta) {
        updateWorld(delta);
        hullSprite.setPosition(hull.getPosition().x * pixels_per_meter, 
                hull.getPosition().y * pixels_per_meter);
        hullSprite.setRotation((float) (Math.toDegrees(hull.getAngle())));
        
        float turretDistance = (float) 
              Math.sqrt(Math.pow(hullSprite.getHeight() * (0.5 - 16f / 31), 2) + 
              Math.pow(hullSprite.getWidth() * (0.5 - 36f / 61), 2));
        
        turretSprite.setPosition(hullSprite.getX() + hullSprite.getWidth() / 2 
                + (float) (turretDistance * Math.cos(hull.getAngle())) - turretSprite.getWidth() * 16f / 59, 
                hullSprite.getY() + hullSprite.getHeight() / 2 + (float) 
                (turretDistance * Math.sin(hull.getAngle())) - turretSprite.getHeight() / 2);
        turretSprite.setRotation((float) (Math.toDegrees(turret.getAngle())));

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        camera.translate((hull.getPosition().x - camera.position.x), 
//                (hull.getPosition().y - camera.position.y));
        camera.position.x = hullSprite.getX() + hullSprite.getWidth() / 2;
        camera.position.y = hullSprite.getY() + hullSprite.getHeight() / 2;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        map.draw(batch, camera);
        batch.draw(hullSprite, hullSprite.getX(), hullSprite.getY(),hullSprite.getOriginX(),
                hullSprite.getOriginY(),
         hullSprite.getWidth(),hullSprite.getHeight(),hullSprite.getScaleX(),hullSprite.
                         getScaleY(),hullSprite.getRotation());
        batch.draw(turretSprite, turretSprite.getX(), turretSprite.getY(), 
                turretSprite.getOriginX(), turretSprite.getOriginY(),
         turretSprite.getWidth(),turretSprite.getHeight(),turretSprite.getScaleX(),turretSprite.
                         getScaleY(),turretSprite.getRotation());
        batch.end();
    }

    @Override
    public void dispose() {
        hullImg.dispose();
        world.dispose();
    }
}
