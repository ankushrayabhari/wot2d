package worldobject;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import map.GameMap;

public abstract class WorldObject {
    private Body[] bodies;
    private World world;
    private OrthographicCamera camera;
    private GameMap map;
    private Sprite[] sprites;
    
    public WorldObject(World world, 
            OrthographicCamera camera, GameMap map, Sprite[] sprites) {
        this.world = world;
        this.camera = camera;
        this.map = map;
        this.sprites = sprites;
    }
    
    public WorldObject(World world, 
            OrthographicCamera camera, Sprite sprite) {
        this.world = world;
        this.camera = camera;
    }
    
    public Body[] getBodies() {
        return bodies;
    }
    
    public Sprite[] getSprites() {
        return sprites;
    }
    
    public float getX() {
        return sprites[0].getX();
    }
    
    public float getY() {
        return sprites[0].getY();
    }
    
    public float getHeight() {
        return sprites[0].getHeight();
    }
    
    public float getWidth() {
        return sprites[0].getWidth();
    }
    
    public abstract void updateObject();
    
    public abstract void setupObject();
    
    public abstract void drawObject(Batch batch);
}
