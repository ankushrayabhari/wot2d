package com.mygdx.game;

public class Duple {

    private float Fx, Fy;

    public Duple(float x, float y) {
        Fx = x;
        Fy = y;
    }

    public float getX() {
        return Fx;
    }

    public float getY() {
        return Fy;
    }
    
    public void setX(float x) {
        Fx = x;
    }
    
    public void setY(float y) {
        Fy = y;
    }
}
