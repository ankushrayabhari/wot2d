package map;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

import worldobject.Obstacle;

public class GameMap {
    private class MapTile
    {
        private TextureRegion floor;
        
        public MapTile( TextureRegion floor )
        {
            this.floor = floor;
        }
        
        public void draw( Batch batch, int x, int y )
        {
            batch.draw( floor, x, y );
        }
        
        public TextureRegion getTile()
        {
            return floor;
        }
    }
    
    private MapTile[][] map;
    private int[][] mapValues;
    private Texture[] ruins = {new Texture("data/ruin_1.png"), new Texture("data/ruin_2.png"),
            new Texture("data/ruin_3.png"), new Texture("data/ruin_4.png"), 
            new Texture("data/ruin_5.png"), new Texture("data/ruin_6.png")};
    private Texture[] houses = {new Texture("data/house_1.png"), 
            new Texture("data/house_1d.png"), new Texture("data/house_2.png"), 
            new Texture("data/house_2d.png")};
    private int width, height;
    private Texture ground, factory;
    private ArrayList<Obstacle> obstacles;
    
    public GameMap(int width, int height, World world, 
            OrthographicCamera camera, float pixels_per_meter) {
        this.width = width;
        this.height = height;
        map = new MapTile[width][height];
        ground = new Texture("data/snow.jpg");
        factory = new Texture("data/factory.png");
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new MapTile(new TextureRegion(ground));
            }
        }
        mapValues = (new MapReader()).getMapValues();
        obstacles = new ArrayList<Obstacle>();
        Sprite[] factorySprite = {new Sprite(factory)};
        Sprite[] ruinsSprites = new Sprite[ruins.length];
        for (int i = 0; i < ruins.length; i++) {
            ruinsSprites[i] = new Sprite(ruins[i]);
        }
        for (int i = 0; i < mapValues.length; i++) {
            for (int j = 0; j < mapValues[i].length; j++) {
                if (mapValues[i][j] == 2) {
                    obstacles.add(new Obstacle(world, camera, this, 
                            factorySprite[0], pixels_per_meter, i * 
                            ground.getWidth(), j * ground.getHeight()));
                } else if (mapValues[i][j] == 1) {
                    obstacles.add(new Obstacle(world, camera, this, 
                            ruinsSprites[(i + j) % ruinsSprites.length], 
                            pixels_per_meter, i * ground.getWidth(), j * ground.getHeight()));
                }
            }
        }
    }
    
    public void draw(Batch batch, Camera cam) {
        drawSnow(batch, cam);
        drawObstacles(batch, cam);
        drawBoundaries(batch, cam);
    }
    
    private boolean withinRenderRange(Camera cam, int x, int y) {
        if (x >= cam.position.x - cam.viewportWidth && x <= 
                        cam.position.x + cam.viewportWidth / 2
                        && y >= cam.position.y - cam.viewportHeight && 
                        y <= cam.position.y + cam.viewportHeight / 2) {
            return true;
        }
        return false;
    }
    
    private void drawSnow(Batch batch, Camera cam) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                Texture texture = map[i][j].getTile().getTexture();
                int x = i * texture.getWidth();
                int y = j * texture.getHeight();
                if (withinRenderRange(cam, x, y)) {
                    map[i][j].draw(batch, x, y);
                }
            }
        }
    }
    
    private void drawObstacles(Batch batch, Camera cam) {
        int count = 0;
        for (int i = 0; i < mapValues.length; i++) {
            for (int j = 0; j < mapValues[i].length; j++) {
                if (mapValues[i][j] != 0) {
                    int x = i * ground.getWidth();
                    int y = j * ground.getHeight();
                    if (withinRenderRange(cam, x, y)) {
                        obstacles.get(count).drawObject(batch);
                        count++;
                    }
                }
            }
        }
    }
    
    private void drawBoundaries(Batch batch, Camera cam) {
        for (int i = 0; i < map.length; i++) {
            int x = i * ground.getWidth();
            if (withinRenderRange(cam, x, 0)) {
                int index = (i^2) % houses.length;
                batch.draw(houses[index], x, 0);
                batch.draw(houses[(index + i + 1) % houses.length], x + houses[index].getWidth(), 0);
            }
            if (withinRenderRange(cam, x, ground.getHeight() * map[0].length)) {
                int index = (i^2 + map[0].length) % houses.length;
                batch.draw(houses[index], x, ground.getHeight() * map[0].length); 
                batch.draw(houses[(index + i + 1) % houses.length], x + houses[index].getWidth(), 
                        ground.getHeight() * map[0].length);
            }
        }
        for (int j = 0; j <= map[0].length; j++) {
            int y = j * ground.getHeight();
            if (withinRenderRange(cam, 0, y)) {
                int index = (j^2) % houses.length;
                batch.draw(houses[(index + j + 1) % houses.length], 0, y + houses[index].getHeight());
                batch.draw(houses[index], 0, y);
            }
            if (withinRenderRange(cam, ground.getWidth() * map.length, y)) {
                int index = (j^2 + map.length) % houses.length;
                batch.draw(houses[(index + j + 1) % houses.length], 
                        ground.getWidth() * map.length, y + houses[index].getHeight());
                batch.draw(houses[index], ground.getWidth() * map.length, y);
            }
        }
    }
}
