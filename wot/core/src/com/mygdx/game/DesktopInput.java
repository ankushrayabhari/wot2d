package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DesktopInput extends InputAdapter {

    private Viewport view;
    private WorldOfTanks tanks;
    private boolean lockOrientation = false;

    private static final int[] MOVEMENT = new int[] { Input.Keys.W, 
            Input.Keys.A, Input.Keys.S, Input.Keys.D };

    private static final int[] ROTATION = new int[] { Input.Keys.Q, Input.Keys.E };

    private static final int[] SHOOT = new int[] { Input.Buttons.LEFT };

    private boolean isKeyPressed[] = new boolean[7];

    public DesktopInput(WorldOfTanks t) {
        view = t.getViewport();
        tanks = t;
    }

    public Force getForce() {
        float xDir = 0, yDir = 0;

        if (isKeyPressed[0]) {
            yDir += 1;
        }
        if (isKeyPressed[1]) {
            xDir -= 1;
        }
        if (isKeyPressed[2]) {
            yDir -= 1;
        }
        if (isKeyPressed[3]) {
            xDir += 1;
        }
        if (isKeyPressed[0] && isKeyPressed[1]) {
            xDir = (float) (xDir / Math.sqrt(2));
            yDir = (float) (yDir / Math.sqrt(2));
        } else if (isKeyPressed[0] && isKeyPressed[3]) {
            xDir = (float) (xDir / Math.sqrt(2));
            yDir = (float) (yDir / Math.sqrt(2));
        } else if (isKeyPressed[2] && isKeyPressed[1]) {
            xDir = (float) (xDir / Math.sqrt(2));
            yDir = (float) (yDir / Math.sqrt(2));
        } else if (isKeyPressed[2] && isKeyPressed[3]) {
            xDir = (float) (xDir / Math.sqrt(2));
            yDir = (float) (yDir / Math.sqrt(2));
        }
//        System.out.println(xDir + " " + yDir);
        return new Force(xDir, yDir);
    }

    @Override
    public boolean keyDown(int keycode) {
        for (int i = 0; i < MOVEMENT.length; i++) {
            if (MOVEMENT[i] == keycode) {
                isKeyPressed[i] = true;
                return true;
            }
        }
        return false;
    }

    /** @see com.badlogic.gdx.InputProcessor#keyUp(int) */
    @Override
    public boolean keyUp(int keycode) {
        for (int i = 0; i < MOVEMENT.length; i++) {
            if (MOVEMENT[i] == keycode) {
                isKeyPressed[i] = false;
                return false;
            }
        }
        return true;
    }
}
