package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DesktopInput extends InputAdapter {

    private Viewport view;
    private WorldOfTanks tanks;
    private boolean lockOrientation = false;

    private static final int[] CONTROLS = new int[] { Input.Keys.W, 
            Input.Keys.A, Input.Keys.S, Input.Keys.D, Input.Keys.R, Input.Buttons.LEFT };

    private boolean isKeyPressed[] = new boolean[CONTROLS.length];

    public DesktopInput(WorldOfTanks t) {
        view = t.getViewport();
        tanks = t;
    }
    
    public Vector2 getWasdDirection() {
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
        return new Vector2(xDirection, yDirection);
    }
    
    public boolean isReversing() {
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
    
    public boolean sameQuadrant(Body body) {
        Vector2 dir = getWasdDirection();
        float angle, bodyAngle = (float) Math.toDegrees(body.getAngle());
        while (bodyAngle < 0) {
            bodyAngle += 360;
        }
        bodyAngle %= 360;
        body.setTransform(body.getPosition(), (float) Math.toRadians(bodyAngle));
        if (dir.x == 0 && dir.y == 0) {
            return false;
        } else if (dir.x == 0 && dir.y == 1) {
            angle = 90;
        } else if (dir.x == 0 && dir.y == -1) {
            angle = 270;
        } else if (dir.x == 1 && dir.y == 0) {
            angle = 0;
        } else if (dir.x == -1 && dir.y == 0) {
            angle = 180;
        } else {
            angle = ((float)Math.toDegrees(Math.atan2(dir.y, dir.x))
                    + 360) % 360;
        }
        if (bodyAngle >= 0 && bodyAngle <= 90 && 
                angle >= 0 && angle <= 90) {
            return true;
        } else if (bodyAngle >= 90 && bodyAngle <= 180 && 
                angle >= 90 && angle <= 180) {
            return true;
        } else if (bodyAngle >= 180 && bodyAngle <= 270 && 
                angle >= 180 && angle <= 270) {
            return true;
        } else if (bodyAngle >= 270 && bodyAngle <= 360 && 
                ((angle >= 270 && angle <= 360) || angle == 0)) {
            return true;
        }
        return false;
    }

    public Vector2 getForceDirection() {
        return getWasdDirection().nor();
    }
    
    public boolean closeAngle(float angleInDegrees, boolean isWasd) {
        float angle;
        if (isWasd) {
            Vector2 dir = getWasdDirection();
            if (dir.x == 0 && dir.y == 1) {
                angle = Math.abs(angleInDegrees - 90);
            } else if (dir.x == 0 && dir.y == -1) {
                angle = Math.abs(angleInDegrees - 270);
            } else {
                angle = (float) Math.abs(angleInDegrees - 
                        Math.toDegrees(Math.atan2(dir.y, dir.x)));
            }
            if (angle < 35) {
                return true;
            }
            return false;
        } else {
            angle = getMouseAngleInRad();
            if (Math.abs(Math.toRadians(angle - angleInDegrees)) < 35) {
                return true;
            }
            return false;
        }
    }

    public float getWasdTorqueDirection(float angleInDegrees) {
        while (angleInDegrees < 0) {
            angleInDegrees += 360;
        }
        float currentAngle = angleInDegrees % 360;
        Vector2 dir = getWasdDirection();
        // TODO does the bug still exist
//        float angle;
//        if (duple.x == 0 && duple.y == 1) {
//            angle = Math.abs(angleInDegrees - 90);
//        } else if (duple.x == 0 && duple.y == -1) {
//            angle = Math.abs(angleInDegrees - 270);
//        } else {
//            angle = (float) Math.abs(angleInDegrees - 
//                    Math.toDegrees(Math.atan2(duple.y, duple.x)));
//        }
//        System.out.println(currentAngle + " " + angle);
        if (dir.x != 0) {
            if (Math.abs(currentAngle - 
                    Math.toDegrees(Math.atan2(dir.y, 
                            dir.x))) < 2) {
                return 0;
            } else if (dir.x == 1 && dir.y == 0) {
                if (currentAngle < 180) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (dir.x == -1 && dir.y == 0) {
                if (currentAngle > 180) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (dir.x == 1 && dir.y == 1) {
                if (currentAngle > 45 && currentAngle < 225) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (dir.x == 1 && dir.y == -1) {
                if (currentAngle > 135 && currentAngle < 315) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (dir.x == -1 && dir.y == 1) {
                if (currentAngle > 135 && currentAngle < 315) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (dir.x == -1 && dir.y == -1) {
                if (currentAngle > 45 && currentAngle < 225) {
                    return 1;
                } else {
                    return -1;
                }
            }
        } else {
            if (dir.x == 0 && dir.y == 1) {
                if (Math.abs(currentAngle - 90) < 2) {
                    return 0;
                } else if (currentAngle > 270 || currentAngle < 90) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (dir.x == 0 && dir.y == -1) {
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
}
