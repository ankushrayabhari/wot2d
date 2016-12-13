package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import worldobject.Shell;
import worldobject.TankObject;

public class CollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        // TODO Auto-generated method stub

    }

    @Override
    public void endContact(Contact contact) {
        // TODO Auto-generated method stub

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        short customBitA = contact.getFixtureA().getFilterData().categoryBits;
        short customBitB = contact.getFixtureB().getFilterData().categoryBits;
        if (customBitA == 0x0002 && customBitB == 0x0001) {
             Shell shell = (Shell) contact.getFixtureA().getUserData();
             TankObject tank = (TankObject) contact.getFixtureB().getUserData();
             if (!shell.getOriginalHull().equals(tank.getHull())) {
                 shell.hitObject();
                 tank.loseHealth();
             }
        } else if (customBitB == 0x0002 && customBitA == 0x0001) {
            Shell shell = (Shell) contact.getFixtureB().getUserData();
            TankObject tank = (TankObject) contact.getFixtureA().getUserData();
            if (!shell.getOriginalHull().equals(tank.getHull())) {
                shell.hitObject();
                tank.loseHealth();
            }
        } else if (customBitA == 0x0002) {
            Shell shell = (Shell) contact.getFixtureA().getUserData();
            shell.hitObject();
        } else if (customBitB == 0x0002) {
            Shell shell = (Shell) contact.getFixtureB().getUserData();
            shell.hitObject();
        } else if (customBitA == 0x0002 && customBitB == 0x0002) {
            Shell shellA = (Shell) contact.getFixtureA().getUserData();
            shellA.hitObject();
            Shell shellB = (Shell) contact.getFixtureB().getUserData();
            shellB.hitObject();
        }
    }

}
