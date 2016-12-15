package com.ankushrayabhari.game.map;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

import com.ankushrayabhari.game.screens.GameScreen;
import com.ankushrayabhari.game.worldobject.Boundary;
import com.ankushrayabhari.game.worldobject.Obstacle;

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
            new Texture("data/house_1d.png"), new Texture("data/house_2d.png"), 
            new Texture("data/house_2.png")};
    private int pixelWidth, pixelHeight;
    private Texture ground, factory;
    final private int boundarySize = 3;
    
    public GameMap(GameScreen screen, int width, int height, World world, 
            OrthographicCamera camera, float pixels_per_meter) {
        ground = new Texture("data/snow.jpg");
        pixelWidth = width * ground.getWidth();
        pixelHeight = height * ground.getHeight();
        map = new MapTile[width + 2 * boundarySize][height + 2 * boundarySize];
        factory = new Texture("data/factory.png");
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new MapTile(new TextureRegion(ground));
            }
        }
        mapValues = (new MapReader()).getMapValues();
        Sprite[] factorySprite = {new Sprite(factory)};
        Sprite[] ruinsSprites = new Sprite[ruins.length];
        for (int i = 0; i < ruins.length; i++) {
            ruinsSprites[i] = new Sprite(ruins[i]);
        }
        for (int i = 0; i < mapValues.length; i++) {
            for (int j = 0; j < mapValues[i].length; j++) {
                if (mapValues[i][j] == 2) {
                    screen.addWorldObject(new Obstacle("factory", world, camera, this, 
                            factorySprite[0], pixels_per_meter, (i + boundarySize) * 
                            ground.getWidth(), (j + boundarySize) * ground.getHeight()));
                } else if (mapValues[i][j] == 1) {
                    screen.addWorldObject(new Obstacle("ruin", world, camera, this, 
                            ruinsSprites[(i + j) % ruinsSprites.length], 
                            pixels_per_meter, (i + boundarySize) * 
                            ground.getWidth(), (j + boundarySize) * ground.getHeight()));
                }
            }
        }
        screen.addWorldObject(new Boundary(world, pixels_per_meter, boundarySize,
                pixelWidth + 2 * boundarySize * ground.getWidth(), 
                pixelHeight + 2 * boundarySize * ground.getHeight(), 
                ground.getWidth(), ground.getHeight()));
    }
    
    public void draw(Batch batch, Camera cam) {
        drawSnow(batch, cam);
        drawBoundaries(batch, cam);
    }
    
    public static boolean withinRenderRange(Camera cam, float xPixels, float yPixels) {
        if (xPixels >= cam.position.x - cam.viewportWidth && xPixels <= 
                        cam.position.x + cam.viewportWidth / 2
                        && yPixels >= cam.position.y - cam.viewportHeight && 
                        yPixels <= cam.position.y + cam.viewportHeight / 2) {
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
    
//    private void drawObstacles(Batch batch, Camera cam) {
//        int count = 0;
//        for (int i = 0; i < mapValues.length; i++) {
//            for (int j = 0; j < mapValues[i].length; j++) {
//                if (mapValues[i][j] != 0) {
//                    int x = i * ground.getWidth();
//                    int y = j * ground.getHeight();
//                    if (withinRenderRange(cam, x, y)) {
//                        obstacles.get(count).drawObject(batch);
//                        count++;
//                    }
//                }
//            }
//        }
//    }
    
    private void drawBoundaries(Batch batch, Camera cam) {
        for (int i = 0; i <= map.length - 1; i++) {
            int index = (i^2) % houses.length;
            int x = i * ground.getWidth();
            for (int y = -houses[index].getHeight() + boundarySize * 
                    ground.getHeight() - 4 * houses[index].getHeight(); 
                    y <= -houses[index].getHeight() + boundarySize * 
                    ground.getHeight(); y += 2 * houses[index].getHeight()) {
                index = (i^2 + y^2) % houses.length;
                if (withinRenderRange(cam, x, y)) {
                    batch.draw(houses[(index + 4 * i) % houses.length], x, y);
                    batch.draw(houses[(index + 9 * i + 1) % houses.length], x + 
                            houses[index].getWidth(), y);
                }
            }
            index = (i^2 + (ground.getHeight() * (map[0].length - 
                    boundarySize))^2 + map[0].length) % houses.length;
            for (int y = ground.getHeight() * (map[0].length - boundarySize); 
                    y <= ground.getHeight() * (map[0].length - boundarySize) + 
                            4 * houses[index].getHeight(); y += 2 * houses[index].getHeight()) {
                index = (i^2 + y^2 + map[0].length) % houses.length;
                if (withinRenderRange(cam, x, y)) {
                    batch.draw(houses[(index + 4 * i) % houses.length], x, y); 
                    batch.draw(houses[(index + 9 * i + 1) % houses.length], x + houses[index].getWidth(), 
                            y);
                }
            }
        }
        for (int j = boundarySize; j <= map[0].length - 1 - boundarySize; j++) {
            int index = (j^2) % houses.length;
            int y = j * ground.getHeight() + houses[index].getHeight();
            for (int x = boundarySize * ground.getWidth() - 5 * houses[index].getWidth();
                    x <= boundarySize * ground.getWidth() - houses[index].getWidth();
                    x += 2 * houses[index].getWidth()) {
                index = (j^2 + x^2) % houses.length;
                if (withinRenderRange(cam, x, y) ||
                        withinRenderRange(cam, x, y -
                                houses[index].getHeight())) {
                    batch.draw(houses[(index + 4 * j + 1) % houses.length], 
                            x, y - houses[index].getHeight());
                    batch.draw(houses[(index + 9 * j) % houses.length], x, y);
                }
            }
//            int x = boundarySize * ground.getWidth() - houses[index].getWidth();
            
            index = (j^2 + map.length) % houses.length;
            for (int x = (map.length - boundarySize) * ground.getWidth();
                    x <= (map.length - boundarySize) * ground.getWidth() + 
                            5 * ground.getWidth(); x += 2 * houses[index].getWidth()) {
                index = (j^2 + x^2 + map.length) % houses.length;
                if (withinRenderRange(cam, x, y) || 
                        withinRenderRange(cam, x, 
                                y - houses[index].getHeight())) {
                    batch.draw(houses[(index + 5 * j + 1) % houses.length], 
                            x, y - houses[index].getHeight());
                    batch.draw(houses[(index + 9 * j) % houses.length], x, y);
                }
            }
//            x = (map.length - boundarySize) * ground.getWidth();
        }
    }
}
