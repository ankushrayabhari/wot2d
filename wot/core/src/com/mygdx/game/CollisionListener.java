package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

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
        if ((contact.getFixtureA().getBody().getUserData().equals("playerhull")) 
                || (contact.getFixtureB().getBody().getUserData().equals("playerhull"))) {
//            System.out.println(contact.getFixtureA().getBody().getUserData() + " " + contact.getFixtureB().getBody().getUserData());
        }
    }

}
