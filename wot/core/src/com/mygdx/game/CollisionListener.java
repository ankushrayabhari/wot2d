package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import screens.GameScreen;
import worldobject.Shell;
import worldobject.TankObject;

public class CollisionListener implements ContactListener {
    
    private GameScreen screen;
    private TextureRegion[][] explosionTextures;
    private float pixels_per_meter;
    
    public CollisionListener(GameScreen screen, float pixels_per_meter) {
        this.screen = screen;
        this.pixels_per_meter = pixels_per_meter;
        explosionTextures = TextureRegion.split(new Texture("data/explosions.png"), 134, 134);
    }

    @Override
    public void beginContact(Contact contact) {
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        short customBitA = contact.getFixtureA().getFilterData().categoryBits;
        short customBitB = contact.getFixtureB().getFilterData().categoryBits;
        if (customBitA == 0x0002 && customBitB == 0x0001) {
             Shell shell = (Shell) contact.getFixtureA().getUserData();
             TankObject tank = (TankObject) contact.getFixtureB().getUserData();
             if (!shell.getOriginalHull().equals(tank.getHull())) {
                 if (!shell.hasHitObject()) {
                     tank.loseHealth();
                 }
                 shell.hitObject();
                 screen.addExplosion(new Explosion(explosionTextures, 
                         contact.getWorldManifold().getPoints()[0].x * 
                         pixels_per_meter, contact.getWorldManifold().getPoints()[0].y * 
                         pixels_per_meter));
             }
        } else if (customBitB == 0x0002 && customBitA == 0x0001) {
            Shell shell = (Shell) contact.getFixtureB().getUserData();
            TankObject tank = (TankObject) contact.getFixtureA().getUserData();
            if (!shell.getOriginalHull().equals(tank.getHull())) {
                if (!shell.hasHitObject()) {
                    tank.loseHealth();
                }
                shell.hitObject();
                screen.addExplosion(new Explosion(explosionTextures, 
                        contact.getWorldManifold().getPoints()[0].x * 
                        pixels_per_meter, contact.getWorldManifold().getPoints()[0].y * 
                        pixels_per_meter));
            }
        } else if (customBitA == 0x0002 && customBitB == 0x0002) {
            Shell shellA = (Shell) contact.getFixtureA().getUserData();
            shellA.hitObject();
            Shell shellB = (Shell) contact.getFixtureB().getUserData();
            shellB.hitObject();
        } else if (customBitA == 0x0002) {
            Shell shell = (Shell) contact.getFixtureA().getUserData();
            shell.hitObject();
        } else if (customBitB == 0x0002) {
            Shell shell = (Shell) contact.getFixtureB().getUserData();
            shell.hitObject();
        }
    }

}
