package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DesktopInput extends InputAdapter {

    private Viewport view;
    private WorldOfTanks tanks;
    private boolean lockOrientation = false;

    private static final int[] MOVEMENT = new int[] { Input.Keys.W, Input.Keys.A, Input.Keys.S, Input.Keys.D };

    private static final int[] ROTATION = new int[] { Input.Keys.Q, Input.Keys.E };

    private static final int[] SHOOT = new int[] { Input.Buttons.LEFT };

    private boolean isKeyPressed[] = new boolean[7];

    public DesktopInput(WorldOfTanks t) {
        view = t.getViewport();
        tanks = t;
    }
    
    public Duple getDirection() {
        float xDirection = 0, yDirection = 0;

        if (isKeyPressed[0]) {
            yDirection += 1;
        }
        if (isKeyPressed[1]) {
            xDirection -= 1;
        }
        if (isKeyPressed[2]) {
            yDirection -= 1;
        }
        if (isKeyPressed[3]) {
            xDirection += 1;
        }
        return new Duple(xDirection, yDirection);
    }

    public Duple getForceDirection() {
        Duple duple = getDirection();
        if (isKeyPressed[0] && isKeyPressed[1]) {
            duple.setX((float) (duple.getX() / Math.sqrt(2)));
            duple.setY((float) (duple.getY() / Math.sqrt(2)));
        } else if (isKeyPressed[0] && isKeyPressed[3]) {
            duple.setX((float) (duple.getX() / Math.sqrt(2)));
            duple.setY((float) (duple.getY() / Math.sqrt(2)));
        } else if (isKeyPressed[2] && isKeyPressed[1]) {
            duple.setX((float) (duple.getX() / Math.sqrt(2)));
            duple.setY((float) (duple.getY() / Math.sqrt(2)));
        } else if (isKeyPressed[2] && isKeyPressed[3]) {
            duple.setX((float) (duple.getX() / Math.sqrt(2)));
            duple.setY((float) (duple.getY() / Math.sqrt(2)));
        }
        // System.out.println(xDir + " " + yDir);
        return duple;
    }
    
    public boolean closeAngle(float angleInDegrees) {
        Duple duple = getDirection();
        float angle;
        if (duple.getX() == 0 && duple.getY() == 1) {
            angle = Math.abs(angleInDegrees - 90);
        } else if (duple.getX() == 0 && duple.getY() == -1) {
            angle = Math.abs(angleInDegrees - 270);
        } else {
            angle = (float) Math.abs(angleInDegrees - 
                    Math.toDegrees(Math.atan2(duple.getY(), duple.getX())));
        }
        if (angle < 35) {
            return true;
        }
        return false;
    }

    public float getTorqueDirection(float angleInDegrees) {
        float currentAngle = (angleInDegrees + 360) % 360;
        Duple duple = getDirection();
        float angle;
        if (duple.getX() == 0 && duple.getY() == 1) {
            angle = Math.abs(angleInDegrees - 90);
        } else if (duple.getX() == 0 && duple.getY() == -1) {
            angle = Math.abs(angleInDegrees - 270);
        } else {
            angle = (float) Math.abs(angleInDegrees - 
                    Math.toDegrees(Math.atan2(duple.getY(), duple.getX())));
        }
//        System.out.println(angleInDegrees + " " + angle);
        //TODO rare directional bug
        if (duple.getX() != 0) {
            if (Math.abs(currentAngle - 
                    Math.toDegrees(Math.atan2(duple.getY(), 
                            duple.getX()))) < 2) {
                return 0;
            } else if (duple.getX() == 1 && duple.getY() == 0) {
                if (currentAngle < 180) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (duple.getX() == -1 && duple.getY() == 0) {
                if (currentAngle > 180) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (duple.getX() == 1 && duple.getY() == 1) {
                if (currentAngle > 45 && currentAngle < 225) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (duple.getX() == 1 && duple.getY() == -1) {
                if (currentAngle > 135 && currentAngle < 315) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (duple.getX() == -1 && duple.getY() == 1) {
                if (currentAngle > 135 && currentAngle < 315) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (duple.getX() == -1 && duple.getY() == -1) {
                if (currentAngle > 45 && currentAngle < 225) {
                    return 1;
                } else {
                    return -1;
                }
            }
        } else {
            if (duple.getX() == 0 && duple.getY() == 1) {
                if (Math.abs(currentAngle - 90) < 2) {
                    return 0;
                } else if (currentAngle > 270 || currentAngle < 90) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (duple.getX() == 0 && duple.getY() == -1) {
                if (Math.abs(currentAngle - 270) < 2) {
                    return 0;
                } else if (currentAngle > 270 || currentAngle < 90) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        return 0;
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
