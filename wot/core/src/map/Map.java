package map;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Map {
    private class MapTile
    {
        private TextureRegion floor;
        
        public MapTile() {}
        public MapTile( TextureRegion floor )
        {
            this.setTile( floor );
        }
        public MapTile( MapTile m )
        {
            this( m.floor );
        }
        
        public void reset()
        {
            floor = null;
        }
        
        public void draw( Batch batch, int x, int y )
        {
            batch.draw( floor, x, y );
        }
        
        public void setTile( TextureRegion floor )
        {
            this.floor = floor;
        }
    }
    
    private MapTile[][] map;
    private int[][] mapValues;
    private int width, height;
    private Texture ground, factory, ruins;
    
    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        map = new MapTile[width][height];
        ground = new Texture("data/snow.jpg");
        factory = new Texture("data/factory.png");
        ruins = new Texture("data/ruin_5.png");
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new MapTile(new TextureRegion(ground));
            }
        }
        mapValues = (new MapReader()).getMapValues();
    }
    
    public void draw(Batch batch, Camera cam) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                int x = i * ground.getWidth();
                int y = j * ground.getHeight();
                if (x >= cam.position.x - cam.viewportWidth && x <= 
                        cam.position.x + cam.viewportWidth / 2
                        && y >= cam.position.y - cam.viewportHeight && 
                        y <= cam.position.y + cam.viewportHeight / 2) {
                    batch.draw(ground, x, y);
                }
            }
        }
        for (int i = 0; i < mapValues.length; i++) {
            for (int j = 0; j < mapValues[i].length; j++) {
                if (mapValues[i][j] != 0) {
                    int x = i * ground.getWidth();
                    int y = j * ground.getHeight();
                    if (x >= cam.position.x - cam.viewportWidth && x <= 
                            cam.position.x + cam.viewportWidth / 2
                            && y >= cam.position.y - cam.viewportHeight && 
                            y <= cam.position.y + cam.viewportHeight / 2) {
                        if (mapValues[i][j] == 1) {
                            batch.draw(ruins, x, y);
                        } else {
                            batch.draw(factory, x, y);
                        }
                    }
                }
            }
        }
    }
}
