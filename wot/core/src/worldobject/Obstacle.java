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

    private float pixels_per_meter;
    private Sprite sprite;
    private Body obstacle;
    private BodyDef bodyDef;
    private int x, y;
    
    public Obstacle(World world, OrthographicCamera camera, 
            GameMap map, Sprite sprite, float pixels_per_meter, int x, int y) {
        super(world, camera, sprite);
        this.sprite = sprite;
        this.pixels_per_meter = pixels_per_meter;
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(sprite.getX() / pixels_per_meter, 
                sprite.getY() / pixels_per_meter);
        obstacle = world.createBody(bodyDef);
        obstacle.setUserData("obstacle");
        this.x = x;
        this.y = y;
    }

    @Override
    public void updateObject() {
    }

    @Override
    public void setupObject() {
        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(sprite.getX() / pixels_per_meter, 
                sprite.getY() / pixels_per_meter);

        PolygonShape obstacleShape = new PolygonShape();
        obstacleShape.setAsBox(sprite.getWidth() / 2 / pixels_per_meter, 
                sprite.getHeight() / 2 / pixels_per_meter);
        
        FixtureDef obstacleFixtureDef = new FixtureDef();
        obstacleFixtureDef.shape = obstacleShape;
        obstacleFixtureDef.density = 1f;
        
        Fixture obstacleFixture = obstacle.createFixture(obstacleFixtureDef);

        obstacleShape.dispose();
    }

    @Override
    public void drawObject(Batch batch) {
        batch.draw(sprite, x, 
                y,sprite.getOriginX(),
                sprite.getOriginY(),
         sprite.getWidth(),sprite.getHeight(),
         sprite.getScaleX(),sprite.
                         getScaleY(),sprite.getRotation());
    }

}
