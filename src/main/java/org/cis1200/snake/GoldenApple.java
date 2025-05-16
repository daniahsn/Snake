package org.cis1200.snake;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
/**
 * Represents a golden apple, a special type of food item in the Snake game.
 * Inherits characteristics from the GameObj class and implements the Food interface.
 */
public class GoldenApple extends GameObj implements Food {

    // Constants
    public static final int SIZE = 35;
    public static final int INIT_POS_X = (int) (Math.random() * 560);
    public static final int INIT_POS_Y = (int) (Math.random() * 360);
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;

    // Image representing the golden apple
    private BufferedImage goldenAppleImg;

    // Constructors

    public GoldenApple(int boardWidth, int boardHeight) {
        super(INIT_VEL_X, INIT_VEL_Y, INIT_POS_X, INIT_POS_Y, SIZE, SIZE, boardWidth, boardHeight);
        try {
            goldenAppleImg = ImageIO.read(new File("files/GoldenApple.png"));
        } catch (IOException e) {
            System.out.println("Error loading golden apple image.");
        }
    }


    public GoldenApple(int positionX, int positionY, int boardWidth,
                       int boardHeight, LinkedList<Point> objs) {
        super(INIT_VEL_X, INIT_VEL_Y, positionX, positionY, SIZE,
                SIZE, boardWidth, boardHeight, objs);
        try {
            goldenAppleImg = ImageIO.read(new File("files/GoldenApple.png"));
        } catch (IOException e) {
            System.out.println("Error loading golden apple image.");
        }
    }

    /**
     * Updates the snake's velocity when it consumes the golden apple.
     *
     * @param snake The snake object to be updated.
     */
    public void updateSnake(Snake snake) {
        snake.setSnakeVX(snake.getSnakeVX() + 1);
        snake.setSnakeVY(snake.getSnakeVY() + 1);
    }

    /**
     * Draws the golden apple on the graphics context.
     *
     * @param g The graphics context used for drawing.
     */
    @Override
    public void draw(Graphics g) {
        for (Point p : getGameObjects()) {
            g.drawImage(goldenAppleImg, p.x, p.y, SIZE, SIZE, null);
        }
    }

    /**
     * Checks if the golden apple intersects with another game object.
     *
     * @param that The other game object.
     * @return True if the golden apple intersects with the other object, false otherwise.
     */
    @Override
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
}
