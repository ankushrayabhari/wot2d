package com.mygdx.game;

public class Force {

    private float Fx, Fy;

    public Force(float x, float y) {
        Fx = x;
        Fy = y;
    }

    public float getX() {
        return Fx;
    }

    public float getY() {
        return Fy;
    }
}
