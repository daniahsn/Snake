package org.cis1200.snake;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

// The Apple class represents the apple object in the Snake game
public class Apple extends GameObj implements Food {
    // Constants for the size and initial positions of the apple
    public static final int SIZE = 30;
    public static final int INIT_POS_X = (int)(Math.random() * 560);
    public static final int INIT_POS_Y = (int)(Math.random() * 360);
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;

    private BufferedImage appleImg; // Image for the apple

    // Constructor for creating an Apple object with random initial position
    public Apple(int boardWidth, int boardHeight) {
        super(INIT_VEL_X, INIT_VEL_Y, INIT_POS_X, INIT_POS_Y, SIZE, SIZE, boardWidth, boardHeight);

        // Load the apple image from a file
        try {
            appleImg = ImageIO.read(new File("files/apple.png"));
        } catch (IOException e) {
            System.out.println("Error loading apple image");
        }
    }

    // Constructor for creating an Apple object with specified position
    public Apple(int positionX, int positionY, int boardWidth,
                 int boardHeight, LinkedList<Point> objs) {
        super(INIT_VEL_X, INIT_VEL_Y, positionX, positionY,
                SIZE, SIZE, boardWidth, boardHeight, objs);

        // Load the apple image from a file
        try {
            appleImg = ImageIO.read(new File("files/apple.png"));
        } catch (IOException e) {
            System.out.println("Error loading apple image");
        }
    }

    // Method to update the snake when it eats the apple
    public void updateSnake(Snake snake) {
        snake.grow(3);
    }

    // Draw method to display the apple on the game board
    @Override
    public void draw(Graphics g) {
        for (Point p : getGameObjects()) {
            g.drawImage(appleImg, p.x, p.y, SIZE, SIZE, null);
        }
    }

    // Method to check if the apple intersects with another game object
    @Override
    public boolean intersects(GameObj that) {
        for (int i = 0; i < getGameObjects().size(); i++) {
            Point p = getGameObjects().get(i);
            // Check for intersection based on the coordinates of the objects
            if (p.x + getWidth() >= that.getPx()
                    && p.y + getHeight() >= that.getPy()
                    && that.getPx() + that.getWidth() >= p.x
                    && that.getPy() + that.getHeight() >= p.y) {
                remove(i); // Remove the eaten apple
                return true;
            }
        }
        return false;
    }
}
