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

import map.GameMap;

public class Obstacle extends WorldObject {

    private float pixels_per_meter, xCrop, yCrop;
    private Sprite sprite;
    private Body obstacle;
    private BodyDef bodyDef;
    private float x, y;
    private OrthographicCamera camera;
    private World world;
    private String obstacleType;
    
    public Obstacle(String obstacleType, World world, OrthographicCamera camera, 
            GameMap map, Sprite sprite, float pixels_per_meter, float x, float y) {
        super(world, camera, sprite);
        this.sprite = sprite;
        this.world = world;
        this.pixels_per_meter = pixels_per_meter;
        this.camera = camera;
        this.obstacleType = obstacleType;
        this.x = x;
        this.y = y;
    }

    @Override
    public void updateObject() {
    }

    @Override
    public void setupObject() {
        this.sprite.setPosition(x, y);
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        if (obstacleType.equals("factory")) {
            bodyDef.position.set((sprite.getX() + sprite.getWidth() * 0.51f) / pixels_per_meter, 
                    (sprite.getY() + sprite.getHeight() * 0.67f) / pixels_per_meter);
            xCrop = 0.95f;
            yCrop = 0.65f;
        } else {
            bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / pixels_per_meter, 
                    (sprite.getY() + sprite.getHeight() * 0.45f) / pixels_per_meter);
            xCrop = 0.7f;
            yCrop = 0.65f;
        }
        obstacle = world.createBody(bodyDef);
        obstacle.setUserData("obstacle");
        PolygonShape obstacleShape = new PolygonShape();
        obstacleShape.setAsBox(sprite.getWidth() / 2 / pixels_per_meter * xCrop, 
                sprite.getHeight() / 2 / pixels_per_meter * yCrop);
        
        FixtureDef obstacleFixtureDef = new FixtureDef();
        obstacleFixtureDef.shape = obstacleShape;
        obstacleFixtureDef.density = 1f;
        obstacleFixtureDef.filter.categoryBits = 0x0003;
        
        Fixture obstacleFixture = obstacle.createFixture(obstacleFixtureDef);

        obstacleShape.dispose();
    }

    @Override
    public void drawObject(Batch batch) {
        if (GameMap.withinRenderRange(camera, x, y)) {
            batch.draw(sprite, x, 
                    y,sprite.getOriginX(),
                    sprite.getOriginY(),
             sprite.getWidth(),sprite.getHeight(),
             sprite.getScaleX(),sprite.
                             getScaleY(),sprite.getRotation());
        }
    }

    @Override
    public Body getUserData() {
        return obstacle;
    }

    @Override
    public float getX() {
        return sprite.getX();
    }

    @Override
    public float getY() {
        return sprite.getY();
    }

    @Override
    public float getHeight() {
        return sprite.getHeight();
    }

    @Override
    public float getWidth() {
        return sprite.getWidth();
    }

}
