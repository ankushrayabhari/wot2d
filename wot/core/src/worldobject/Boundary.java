package worldobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Boundary extends WorldObject {
    
    World world;
    float pixels_per_meter, boundarySize, width, height, gWidth, gHeight;
    
    public Boundary(World world, float pixels_per_meter, float boundarySize, 
            float pixelWidth, float pixelHeight, float groundW, float groundH) {
        super(world, pixels_per_meter);
        this.pixels_per_meter = pixels_per_meter;
        this.world = world;
        this.gWidth = groundW;
        this.gHeight = groundH;
        this.boundarySize = boundarySize;
        width = pixelWidth / pixels_per_meter;
        height = pixelHeight / pixels_per_meter;
    }

    BodyDef bodyDef;
    Body boundary;
    
    @Override
    public void updateObject() {
    }

    @Override
    public void setupObject() {
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        boundary = world.createBody(bodyDef);
        boundary.setUserData("boundary");
        ChainShape obstacleShape = new ChainShape();
        float boundXSize = boundarySize * gWidth / pixels_per_meter;
        float boundYSize = boundarySize * gHeight / pixels_per_meter;
//        System.out.println((height - 2 * boundYSize) + " " + (width - 2 * boundXSize));
        obstacleShape.createChain(new float[]{boundXSize, boundYSize, 
                boundXSize, height - boundYSize, width - boundXSize, 
                height - boundYSize, width - boundXSize, boundYSize, boundXSize, boundYSize});
//        obstacleShape.createChain(new float[]{0, 0, 0, height, width, height, width, 0, 0, 0});
        
        FixtureDef obstacleFixtureDef = new FixtureDef();
        obstacleFixtureDef.shape = obstacleShape;
        obstacleFixtureDef.density = 1f;
        obstacleFixtureDef.filter.categoryBits = 0x0004;
        
        Fixture obstacleFixture = boundary.createFixture(obstacleFixtureDef);

        obstacleShape.dispose();
    }

    @Override
    public void drawObject(Batch batch) {
    }

    @Override
    public Body getUserData() {
        return boundary;
    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getWidth() {
        return width;
    }
    
    @Override
    public String toString() {
        return "boundary";
    }

}
