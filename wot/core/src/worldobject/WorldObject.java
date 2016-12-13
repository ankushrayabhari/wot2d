package worldobject;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import map.GameMap;

public abstract class WorldObject {
    
    public WorldObject(World world, 
            OrthographicCamera camera, GameMap map, float pixels_per_meter) {
    }
    
    public WorldObject(World world, 
            OrthographicCamera camera, Sprite sprite) {
    }
    
    public WorldObject(World world, float pixels_per_meter) {
    }
    
    public abstract float getX();
    
    public abstract float getY();
    
    public abstract float getHeight();
    
    public abstract float getWidth();
    
    public abstract void updateObject();
    
    public abstract void setupObject();
    
    public abstract void drawObject(Batch batch);
    
    public abstract Body getUserData();
    
    @Override
    public abstract String toString();
}
