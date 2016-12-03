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
import com.mygdx.game.Duple;
import com.mygdx.game.WorldOfTanks;

import tank.Tank;

public class GameScreen extends ScreenAdapter {

    private final float forceMultiplier = 6;
    private final float torqueMultiplier = 0.6f;
    private final float pixels_per_meter = 50;
    private final float kFriction = 0.3f;
    private final float torqueFriction = 0.1f;
    
    private WorldOfTanks game;
    private SpriteBatch batch;
    private Sprite sprite;
    private Texture img;
    private World world;
    private Body body;
    private DesktopInput input;
    private OrthographicCamera camera;

    public GameScreen(WorldOfTanks t) {
        game = t;
        batch = new SpriteBatch();
        img = new Texture("data/t34full.png");
        sprite = new Sprite(img);
        input = new DesktopInput(t);
        Gdx.input.setInputProcessor(input);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), 
                Gdx.graphics.getHeight());

        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);

        world = new World(new Vector2(0, 0f), true);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(sprite.getX() / pixels_per_meter, 
                sprite.getY() / pixels_per_meter);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2 / pixels_per_meter, 
                sprite.getHeight() / 2 / pixels_per_meter);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture = body.createFixture(fixtureDef);

        shape.dispose();
        try {
            Tank tank = new Tank("M4 Sherman");
//            System.out.println(tank);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private boolean sameQuadrant(Body body, Duple dir) {
        float angle, bodyAngle = (float) (Math.toDegrees(body.getAngle()) + 
                360) % 360;
        if (dir.getX() == 0 && dir.getY() == 0) {
            return false;
        } else if (dir.getX() == 0 && dir.getY() == 1) {
            angle = 90;
        } else if (dir.getX() == 0 && dir.getY() == -1) {
            angle = 270;
        } else if (dir.getX() == 1 && dir.getY() == 0) {
            angle = 0;
        } else if (dir.getX() == -1 && dir.getY() == 0) {
            angle = 180;
        } else {
            angle = ((float)Math.toDegrees(Math.atan2(dir.getY(), dir.getX()))
                    + 360) % 360;
        }
        if (bodyAngle >= 0 && bodyAngle <= 90 && 
                angle >= 0 && angle <= 90) {
            return true;
        } else if (bodyAngle >= 90 && bodyAngle <= 180 && 
                angle >= 90 && angle <= 180) {
            return true;
        } else if (bodyAngle >= 180 && bodyAngle <= 270 && 
                angle >= 180 && angle <= 270) {
            return true;
        } else if (bodyAngle >= 270 && bodyAngle <= 360 && 
                ((angle >= 270 && angle <= 360) || angle == 0)) {
            return true;
        }
        return false;
    }

    private void updateWorld(float delta) {
        float torque = input.getTorqueDirection((float) 
                Math.toDegrees(body.getAngle())) * torqueMultiplier;
        Duple dir = input.getDirection();
        if (sameQuadrant(body, dir)) {
            body.applyForceToCenter((float)(forceMultiplier * 
                    Math.cos(body.getAngle())), (float)(forceMultiplier * 
                            Math.sin(body.getAngle())), true);
        }
        if (torque != 0) {
            if (body.getAngularVelocity() < 35) {
                if (!input.closeAngle((float) 
                        Math.toDegrees((body.getAngle() + 360) % 360))) {
                    body.applyTorque(torque, true);
                } else {
                    body.applyTorque(torque / 2, true);
                }
            }
//            body.applyForceToCenter((float)(forceMultiplier * 
//                    Math.sin(body.getAngle())), (float)(forceMultiplier * 
//                            Math.cos(body.getAngle())), true);
        }
//        if (torque != 0) {
//            body.applyTorque(torque, true);
//        } else {
//            body.setAngularVelocity(0);
//            Force forceDir = input.getForceDirection();
//            body.applyForceToCenter(forceDir.getX() * forceMultiplier, 
//                    forceDir.getY() * forceMultiplier, true);
//        }
//        System.out.println(input.getTorque(body.getLinearVelocity()));
//        System.out.println(body.getAngle());
        if (body.getAngularVelocity() != 0) {
            body.applyTorque(-body.getAngularVelocity() * body.getMass() * 
                    9.8f * torqueFriction, true);
        }
        if (!body.getLinearVelocity().isZero()) {
            body.applyForceToCenter(- body.getLinearVelocity().x * 
                    body.getMass() * 9.8f * kFriction, 
                    - body.getLinearVelocity().y * body.getMass() * 
                    9.8f * kFriction, true);
        }
        world.step(delta, 6, 2);
    }

    @Override
    public void render(float delta) {
//        camera.update();
        updateWorld(delta);
        sprite.setPosition(body.getPosition().x * pixels_per_meter, 
                body.getPosition().y * pixels_per_meter);
        sprite.setRotation((float) (Math.toDegrees(body.getAngle())));

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(sprite, sprite.getX(), sprite.getY(),sprite.getOriginX(),
                sprite.getOriginY(),
         sprite.getWidth(),sprite.getHeight(),sprite.getScaleX(),sprite.
                         getScaleY(),sprite.getRotation());
        batch.end();
    }

    @Override
    public void dispose() {
        img.dispose();
        world.dispose();
    }
}
