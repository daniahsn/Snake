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
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (Point p : getGameObjects()) {
            if (appleImg != null) {
                // Draw apple image directly without shadow to avoid silhouette
                g2d.drawImage(appleImg, p.x, p.y, SIZE, SIZE, null);
            } else {
                // Fallback to colored circle if image fails to load
                g2d.setColor(new Color(231, 76, 60));
                g2d.fillOval(p.x, p.y, SIZE, SIZE);
                
                // Add highlight
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillOval(p.x + 5, p.y + 5, SIZE/3, SIZE/3);
            }
        }
        
        g2d.dispose();
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
    
    /**
     * Updates the board size for responsive gameplay
     */
    public void updateBoardSize(int newWidth, int newHeight) {
        // Update the maxX and maxY in the parent GameObj class
        // This will be handled by the parent class's constructor logic
    }
    
    /**
     * Override add method to avoid scoreboard area
     */
    @Override
    public void add() {
        // Avoid spawning apples in the scoreboard area (left side, top area)
        int minX = 200; // Leave space for scoreboard
        int minY = 100; // Leave space for top area
        
        int x = minX + (int)(Math.random() * (getMaxX() - minX));
        int y = minY + (int)(Math.random() * (getMaxY() - minY));
        
        getGameObjects().add(new Point(x, y));
    }
}
