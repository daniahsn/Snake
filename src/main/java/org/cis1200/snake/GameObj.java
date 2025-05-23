package org.cis1200.snake;

import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

/**
 * An object in the game.
 *
 * Game objects exist in the game court. They have a position, velocity, size
 * and bounds. Their velocity controls how they move; their position should
 * always be within their bounds.
 */

public abstract class GameObj {

    /*
     * Current position of the object
     * Coordinates are given by the upper-left hand corner of the object.
     * This position should always be within bounds: 0 <= px <= maxX 0 <= py <= maxY
     */
    private int px;
    private int py;

    /* Size of object, in pixels. */
    private final int width;
    private final int height;

    /* Velocity: number of pixels to move every time move() is called. */
    private int vx;
    private int vy;

    /*
     * Upper bounds of the area in which the object can be positioned. Maximum
     * permissible x, y positions for the upper-left hand corner of the object.
     */
    private final int maxX;
    private final int maxY;

    /* Stores a type of game object in a linked list of points that are their
     * coordinates on the Snake game board
     */
    private LinkedList<Point> gameObjects;

    /**
     * Constructor
     */
    public GameObj(
            int vx, int vy, int px, int py, int width, int height, int courtWidth,
            int courtHeight
    ) {
        this.vx = vx;
        this.vy = vy;
        this.px = px;
        this.py = py;
        this.width = width;
        this.height = height;

        // take the width and height into account when setting the bounds for
        // the upper left corner of the object.
        this.maxX = courtWidth - width;
        this.maxY = courtHeight - height;

        gameObjects = new LinkedList<>();
        gameObjects.addFirst(new Point(px, py));

    }

    public GameObj(
            int vx, int vy, int px, int py, int width, int height, int courtWidth,
            int courtHeight, LinkedList<Point> objs
    ) {
        this.vx = vx;
        this.vy = vy;
        this.px = px;
        this.py = py;
        this.width = width;
        this.height = height;

        // take the width and height into account when setting the bounds for
        // the upper left corner of the object.
        this.maxX = courtWidth - width;
        this.maxY = courtHeight - height;

        gameObjects = objs;

    }

    /**************************************************************************
     * GETTERS
     **************************************************************************/
    public int getPx() {
        return this.px;
    }

    public int getPy() {
        return this.py;
    }

    public int getVx() {
        return this.vx;
    }

    public int getVy() {
        return this.vy;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public LinkedList<Point> getGameObjects() {
        return gameObjects;
    }

    /**************************************************************************
     * SETTERS
     **************************************************************************/
    public void setPx(int px) {
        this.px = px;
        clip();
    }

    public void setPy(int py) {
        this.py = py;
        clip();
    }

    public void setVx(int vx) {
        this.vx = vx;
    }

    public void setVy(int vy) {
        this.vy = vy;
    }

    public void setGameObjects(LinkedList<Point> objs) {
        gameObjects = objs;
    }

    /**************************************************************************
     * UPDATES AND OTHER METHODS
     **************************************************************************/

    /**
     * Prevents the object from going outside the bounds of the area
     * designated for the object (i.e. Object cannot go outside the active
     * area the user defines for it).
     */
    public void clip() {
        this.px = Math.min(Math.max(this.px, 0), this.maxX);
        this.py = Math.min(Math.max(this.py, 0), this.maxY);
    }

    /**
     * Moves the object by its velocity. Ensures that the object does not go
     * outside its bounds by clipping.
     */
    public void move() {
        this.px += this.vx;
        this.py += this.vy;

        clip();
    }

    /**
     * Determine whether this game object is currently intersecting another
     * object.
     *
     * Intersection is determined by comparing bounding boxes. If the bounding
     * boxes overlap, then an intersection is considered to occur.
     *
     * @param that The other object
     * @return Whether this object intersects the other object.
     */
    public boolean intersects(GameObj that) {
        for (int i = 0; i < getGameObjects().size(); i++) {
            Point p = getGameObjects().get(i);
            if (p.x + getWidth() >= that.getPx()
                    && p.y + getHeight() >= that.getPy()
                    && that.getPx() + that.getWidth() >= p.x
                    && that.getPy() + that.getHeight() >= p.y) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether the game object will hit a wall in the next time step.
     * If so, return the direction of the wall in relation to this game object.
     *
     * @return Direction of impending wall, null if all clear.
     */
    public Direction hitWall() {
        if (this.px + this.vx < 0) {
            return Direction.LEFT;
        } else if (this.px + this.vx > this.maxX) {
            return Direction.RIGHT;
        }

        if (this.py + this.vy < 0) {
            return Direction.UP;
        } else if (this.py + this.vy > this.maxY) {
            return Direction.DOWN;
        } else {
            return null;
        }
    }

    /**
     * Default draw method that provides how the object should be drawn
     * in the GUI. This method does not draw anything. Subclass should
     * override this method based on how their object should appear.
     *
     * @param g
     *  The <code>Graphics</code> context used for drawing the object.
     *  Remember graphics contexts that we used in OCaml, it gives the
     *  context in which the object should be drawn (a canvas, a frame,
     *  etc.)
     */
    public void draw(Graphics g) {
    }

    /**
     * Checks if an object has hit a wall of the board.
     * @return true if the object has hit a wall, false if not
     */

    public boolean hasHitWall() {
        return (px + vx < 0) || (px + vx > maxX)
                || (py + vy < 0) || (py + vy > maxY);
    }

    public void add() {
        gameObjects.add(new Point(((int)(Math.random() * maxX)), ((int)(Math.random() * maxY))));
    }

    public void remove(int index) {
        gameObjects.remove(index);
    }
}
