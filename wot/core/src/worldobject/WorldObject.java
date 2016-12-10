package worldobject;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import map.Map;

public abstract class WorldObject {
    private Body[] bodies;
    private Texture[] textures;
    private World world;
    private OrthographicCamera camera;
    private Map map;
    private Sprite[] sprites;
    
    public WorldObject(Texture[] textures, World world, 
            OrthographicCamera camera, Map map, Sprite[] sprites) {
        this.textures = textures;
        this.world = world;
        this.camera = camera;
        this.map = map;
        this.sprites = sprites;
    }
    
    public Body[] getBodies() {
        return bodies;
    }
    
    public Texture[] getTextures() {
        return textures;
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
