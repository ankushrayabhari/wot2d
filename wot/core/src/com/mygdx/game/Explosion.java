package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion extends TextureRegion {
    
    private TextureRegion[] textures;
    private int counter = 0;
    private float x, y;
    
    public Explosion(TextureRegion[][] textureRegion, float x, float y) {
        textures = textureRegion[0];
        this.x = x;
        this.y = y;
    }
    
    public TextureRegion getTextureRegion() {
        counter += 5;
        return textures[((counter - 5) / 5) % textures.length];
    }
    
    public int getCounter() {
        return counter;
    }
    
    public void draw(Batch batch) {
        TextureRegion texture = getTextureRegion();
        batch.draw(texture, x - texture.getRegionWidth() / 2, y - 
                texture.getRegionHeight() / 2);
    }
}
