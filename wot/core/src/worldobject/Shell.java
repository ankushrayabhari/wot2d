package worldobject;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import map.GameMap;

public class Shell extends WorldObject {

    private float pixels_per_meter;
    private Sprite sprite;
    private Body shell;
    private BodyDef bodyDef;
    private float x, y;
    private OrthographicCamera camera;
    private World world;
    private float angle;
    private float velocity = 20;
    
    public Shell(World world, OrthographicCamera camera, 
            GameMap map, Sprite sprite, float pixels_per_meter, float x, float y, float angleInRad) {
        super(world, camera, sprite);
        this.sprite = sprite;
        this.world = world;
        this.pixels_per_meter = pixels_per_meter;
        this.camera = camera;
        this.angle = angleInRad;
        this.x = x;
        this.y = y;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getHeight() {
        return sprite.getHeight();
    }

    @Override
    public float getWidth() {
        return sprite.getWidth();
    }

    @Override
    public void updateObject() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setupObject() {
        this.sprite.setPosition(x, y);
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        bodyDef.position.setAngleRad(angle + (float) (Math.PI / 2));
        bodyDef.position.set((sprite.getX() - sprite.getWidth() / 2) / pixels_per_meter, 
                (sprite.getY()) / pixels_per_meter);
        shell = world.createBody(bodyDef);
        shell.setTransform(shell.getPosition(), angle - (float) (Math.PI / 2));
        shell.setUserData("shell");
        shell.setLinearVelocity((float) (Math.cos(angle) * velocity), (float) 
                (Math.sin(angle) * velocity));
        PolygonShape shellShape = new PolygonShape();
        shellShape.setAsBox(sprite.getWidth() / 2 / pixels_per_meter, 
                sprite.getHeight() / 2 / pixels_per_meter);
        
        FixtureDef shellFixtureDef = new FixtureDef();
        shellFixtureDef.shape = shellShape;
        shellFixtureDef.density = 1f;
        
        Fixture obstacleFixture = shell.createFixture(shellFixtureDef);

        shellShape.dispose();
    }

    @Override
    public void drawObject(Batch batch) {
        float x = shell.getPosition().x * pixels_per_meter - sprite.getWidth() / 2;
        float y = shell.getPosition().y * pixels_per_meter - sprite.getHeight() / 2;
        sprite.setPosition(x, y);
        sprite.setRotation((float) (Math.toDegrees(shell.getAngle())));
        if (GameMap.withinRenderRange(camera, x, y)) {
            batch.draw(sprite, x, y, sprite.getOriginX(), 
                    sprite.getOriginY(), sprite.getWidth(), 
                    sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
        }
    }

    @Override
    public String getUserData() {
        return "shell";
    }

}
