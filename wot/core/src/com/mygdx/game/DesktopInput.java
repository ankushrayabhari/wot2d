package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class DesktopInput extends InputAdapter {

    private static final int[] CONTROLS = new int[] { Input.Keys.W, 
            Input.Keys.A, Input.Keys.S, Input.Keys.D, Input.Buttons.LEFT };

    private boolean isKeyPressed[] = new boolean[CONTROLS.length];

    public DesktopInput(WorldOfTanks t) {
    }
    
    public boolean isReversing() {
        return isKeyPressed[2];
    }
    
    public boolean isAccelerating() {
        return isKeyPressed[0];
    }
    
    public boolean isShooting() {
        return isKeyPressed[4];
    }
    
    public float getMouseAngleInRad() {
        float dX = Gdx.input.getX() - Gdx.graphics.getWidth() / 2;
        float dY = Gdx.graphics.getHeight() / 2 - Gdx.input.getY();
        if (dX == 0) {
            if (dY < 0) {
                return (float) (Math.PI * 3f / 2);
            } else if (dY > 0) {
                return (float) (Math.PI / 2);
            } else {
                return -1;
            }
        }
        float angle = (float) (Math.atan2(dY, dX));
        while (angle < 0) {
            angle += (float) Math.PI * 2;
        }
        return angle %= (float) (Math.PI * 2);
    }

    public float getWasdTorqueDirection() {
        if ((isKeyPressed[1] && !isKeyPressed[2]) || (isKeyPressed[3] && 
                isKeyPressed[2])) {
            return 1;
        } else if ((isKeyPressed[3] && !isKeyPressed[2]) || (isKeyPressed[1] &&
                isKeyPressed[2])) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        for (int i = 0; i < CONTROLS.length; i++) {
            if (CONTROLS[i] == keycode) {
                isKeyPressed[i] = true;
                return true;
            }
        }
        return false;
    }

    /** @see com.badlogic.gdx.InputProcessor#keyUp(int) */
    @Override
    public boolean keyUp(int keycode) {
        for (int i = 0; i < CONTROLS.length; i++) {
            if (CONTROLS[i] == keycode) {
                isKeyPressed[i] = false;
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        isKeyPressed[4] = true;
        return true;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isKeyPressed[4] = false;
        return true;
    }
}
