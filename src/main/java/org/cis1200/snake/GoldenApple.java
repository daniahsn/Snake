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
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (Point p : getGameObjects()) {
            // Draw golden apple image directly
            if (goldenAppleImg != null) {
                g2d.drawImage(goldenAppleImg, p.x, p.y, SIZE, SIZE, null);
            } else {
                // Fallback to colored circle with sparkle effect
                g2d.setColor(new Color(241, 196, 15));
                g2d.fillOval(p.x, p.y, SIZE, SIZE);
                
                // Add sparkle highlights
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(p.x + 8, p.y + 8, SIZE/4, SIZE/4);
                g2d.fillOval(p.x + 20, p.y + 15, SIZE/6, SIZE/6);
            }
        }
        
        g2d.dispose();
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
        // Avoid spawning golden apples in the scoreboard area (left side, top area)
        int minX = 200; // Leave space for scoreboard
        int minY = 100; // Leave space for top area
        
        int x = minX + (int)(Math.random() * (getMaxX() - minX));
        int y = minY + (int)(Math.random() * (getMaxY() - minY));
        
        getGameObjects().add(new Point(x, y));
    }
}
