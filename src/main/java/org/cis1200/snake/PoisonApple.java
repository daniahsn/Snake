package org.cis1200.snake;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Represents a poison apple, which shortens the snake when eaten.
 */
public class PoisonApple extends GameObj implements Food {
    
    // Constants
    public static final int SIZE = 25;
    public static final int INIT_POS_X = (int) (Math.random() * 560);
    public static final int INIT_POS_Y = (int) (Math.random() * 360);
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;

    // Image representing the poison apple
    private BufferedImage poisonAppleImg;

    /**
     * Constructor with random position
     */
    public PoisonApple(int boardWidth, int boardHeight) {
        super(INIT_VEL_X, INIT_VEL_Y, INIT_POS_X, INIT_POS_Y, SIZE, SIZE, boardWidth, boardHeight);
        loadImage();
    }

    /**
     * Constructor with specified position
     */
    public PoisonApple(int positionX, int positionY, int boardWidth,
                    int boardHeight, LinkedList<Point> objs) {
        super(INIT_VEL_X, INIT_VEL_Y, positionX, positionY, SIZE,
                SIZE, boardWidth, boardHeight, objs);
        loadImage();
    }
    
    /**
     * Loads the poison apple image
     */
    private void loadImage() {
        try {
            poisonAppleImg = ImageIO.read(new File("files/poisonApple.png"));
        } catch (IOException e) {
            System.out.println("Error loading poison apple image.");
        }
    }

    /**
     * Updates the snake when it consumes the poison apple - shortens the snake.
     */
    @Override
    public void updateSnake(Snake snake) {
        // Shorten the snake by 2 segments (if possible)
        snake.shrink(2);
    }

    /**
     * Draws the poison apple on the graphics context.
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (Point p : getGameObjects()) {
            // Draw poison apple image directly
            if (poisonAppleImg != null) {
                g2d.drawImage(poisonAppleImg, p.x, p.y, SIZE, SIZE, null);
            } else {
                // Fallback to colored circle with toxic effect
                g2d.setColor(new Color(155, 89, 182));
                g2d.fillOval(p.x, p.y, SIZE, SIZE);
                
                // Add toxic warning pattern
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(p.x + 5, p.y + 5, p.x + SIZE - 5, p.y + SIZE - 5);
                g2d.drawLine(p.x + SIZE - 5, p.y + 5, p.x + 5, p.y + SIZE - 5);
            }
        }
        
        g2d.dispose();
    }

    /**
     * Checks if the poison apple intersects with another game object.
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
        // Avoid spawning poison apples in the scoreboard area (left side, top area)
        int minX = 200; // Leave space for scoreboard
        int minY = 100; // Leave space for top area
        
        int x = minX + (int)(Math.random() * (getMaxX() - minX));
        int y = minY + (int)(Math.random() * (getMaxY() - minY));
        
        getGameObjects().add(new Point(x, y));
    }
}