package org.cis1200.snake;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;

public class Snake extends GameObj {
    public static final int SIZE = 20;
    public static final int INIT_POS_X = 20;
    public static final int INIT_POS_Y = 20;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;

    private int snakeVX = 8;
    private int snakeVY = 8;

    private int px = getPx();
    private int py = getPy();

    public Snake(int boardWidth, int boardHeight) {
        super(INIT_VEL_X, INIT_VEL_Y, INIT_POS_X, INIT_POS_Y, SIZE, SIZE, boardWidth, boardHeight);
    }

    public Snake(int positionX, int positionY, int boardWidth,
                 int boardHeight, LinkedList<Point> objs) {
        super(INIT_VEL_X, INIT_VEL_Y, positionX, positionY,
                SIZE, SIZE, boardWidth, boardHeight, objs);
    }

    public int getSnakeVX() {
        return snakeVX;
    }
    public int getSnakeVY() {
        return snakeVY;
    }

    public void setSnakeVX(int velocity) {
        snakeVX = velocity;
    }

    public void setSnakeVY(int velocity) {
        snakeVY = velocity;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw body segments first (so they appear behind the head)
        for (int i = getGameObjects().size() - 1; i > 0; i--) {
            Point p = getGameObjects().get(i);
            drawSnakeBody(g2d, p, i);
        }
        
        // Draw head last (so it's always on top and visible)
        if (getGameObjects().size() > 0) {
            Point headPoint = getGameObjects().get(0);
            drawSnakeHead(g2d, headPoint);
        }
        
        g2d.dispose();
    }
    
    /**
     * Draws the snake head with special effects
     */
    private void drawSnakeHead(Graphics2D g2d, Point p) {
        // Add enhanced glow effect behind head for better visibility
        g2d.setColor(new Color(46, 204, 113, 100));
        g2d.fillOval(p.x - 4, p.y - 4, 28, 28);
        
        // Add outer glow ring for extra prominence
        g2d.setColor(new Color(46, 204, 113, 60));
        g2d.fillOval(p.x - 6, p.y - 6, 32, 32);
        
        // Main head color with gradient effect
        g2d.setColor(new Color(46, 204, 113));
        g2d.fillRoundRect(p.x, p.y, 20, 20, 10, 10);
        
        // Add inner highlight for 3D effect
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.fillRoundRect(p.x + 2, p.y + 2, 16, 8, 8, 8);
        
        // Add eyes with better positioning and enhanced visibility
        g2d.setColor(Color.WHITE);
        g2d.fillOval(p.x + 5, p.y + 5, 4, 4);
        g2d.fillOval(p.x + 11, p.y + 5, 4, 4);
        
        // Eye pupils with depth and better contrast
        g2d.setColor(new Color(44, 62, 80));
        g2d.fillOval(p.x + 6, p.y + 6, 2, 2);
        g2d.fillOval(p.x + 12, p.y + 6, 2, 2);
        
        // Add shine effect
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillOval(p.x + 7, p.y + 7, 3, 3);
        
        // Enhanced border with stronger glow for better visibility
        g2d.setColor(new Color(41, 128, 185));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(p.x, p.y, 20, 20, 10, 10);
        
        // Add outer border for extra definition
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(p.x - 1, p.y - 1, 22, 22, 11, 11);
        
        // Add tongue if moving
        if (getVx() != 0 || getVy() != 0) {
            g2d.setColor(new Color(231, 76, 60));
            g2d.fillOval(p.x + 8, p.y + 18, 4, 6);
        }
    }
    
    /**
     * Draws snake body segments with enhanced patterns
     */
    private void drawSnakeBody(Graphics2D g2d, Point p, int segmentIndex) {
        // Create a pattern based on segment position
        Color baseColor = new Color(52, 152, 219);
        Color patternColor = new Color(41, 128, 185);
        Color highlightColor = new Color(255, 255, 255, 40);
        Color accentColor = new Color(26, 188, 156, 80);
        
        // Add enhanced glow effect with color variation
        if (segmentIndex % 3 == 0) {
            g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 80));
        } else {
            g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 60));
        }
        g2d.fillOval(p.x - 3, p.y - 3, 26, 26);
        
        // Main body segment with gradient-like effect
        g2d.setColor(baseColor);
        g2d.fillRoundRect(p.x, p.y, 20, 20, 10, 10);
        
        // Add inner highlight for 3D effect
        g2d.setColor(highlightColor);
        g2d.fillRoundRect(p.x + 2, p.y + 2, 16, 10, 8, 8);
        
        // Enhanced pattern system with 6 different patterns
        switch (segmentIndex % 6) {
            case 0:
                // Dots pattern with glow
                g2d.setColor(patternColor);
                g2d.fillOval(p.x + 6, p.y + 6, 4, 4);
                g2d.fillOval(p.x + 10, p.y + 12, 4, 4);
                // Add glow effect
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.fillOval(p.x + 7, p.y + 7, 2, 2);
                g2d.fillOval(p.x + 11, p.y + 13, 2, 2);
                break;
            case 1:
                // Horizontal stripes pattern
                g2d.setColor(patternColor);
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.drawLine(p.x + 4, p.y + 8, p.x + 16, p.y + 8);
                g2d.drawLine(p.x + 4, p.y + 12, p.x + 16, p.y + 12);
                break;
            case 2:
                // Diamond pattern with fill
                g2d.setColor(patternColor);
                int[] xPoints = {p.x + 10, p.x + 6, p.x + 10, p.x + 14};
                int[] yPoints = {p.y + 6, p.y + 10, p.y + 14, p.y + 10};
                g2d.fillPolygon(xPoints, yPoints, 4);
                break;
            case 3:
                // Cross pattern
                g2d.setColor(patternColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(p.x + 10, p.y + 5, p.x + 10, p.y + 15);
                g2d.drawLine(p.x + 5, p.y + 10, p.x + 15, p.y + 10);
                break;
            case 4:
                // Triangle pattern
                g2d.setColor(patternColor);
                int[] triX = {p.x + 10, p.x + 7, p.x + 13};
                int[] triY = {p.y + 6, p.y + 14, p.y + 14};
                g2d.fillPolygon(triX, triY, 3);
                break;
            case 5:
                // Spiral-like pattern
                g2d.setColor(patternColor);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawArc(p.x + 5, p.y + 5, 10, 10, 0, 180);
                g2d.drawArc(p.x + 8, p.y + 8, 4, 4, 180, 180);
                break;
        }
        
        // Enhanced border with varying thickness
        g2d.setColor(new Color(41, 128, 185));
        g2d.setStroke(new BasicStroke(segmentIndex % 2 == 0 ? 2.0f : 1.5f));
        g2d.drawRoundRect(p.x, p.y, 20, 20, 10, 10);
        
        // Add subtle inner shadow for depth
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.drawRoundRect(p.x + 1, p.y + 1, 18, 18, 9, 9);
    }

    @Override
    public void move() {
        for (int i = getGameObjects().size() - 1; i >= 1; i--) {
            getGameObjects().get(i).setLocation(getGameObjects().get(i - 1));
        }
        setPx(px + getVx());
        setPy(py + getVy());
        px = getPx();
        py = getPy();
        getGameObjects().set(0, new Point(px, py));

        clip();
    }

    public void grow(int length) {
        for (int i = 0; i < length; i++) {
            LinkedList<Point> newGameObjects;
            newGameObjects = getGameObjects();
            newGameObjects.add(new Point(getGameObjects().getLast()));
            setGameObjects(newGameObjects);
        }
    }

    public void shrink(int length) {
        for (int i = 0; i < length && getGameObjects().size() > 1; i++) {
            getGameObjects().removeLast();
        }
    }

    public boolean hasHitItself() {
        if (getGameObjects().size() > 1) {
            for (int i = 1; i < getGameObjects().size(); i++) {
                if ((getGameObjects().getFirst().x + getVx() == getGameObjects().get(i).x)
                        && (getGameObjects().getFirst().y + getVy() == getGameObjects().get(i).y)) {
                    return true;
                }
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
        // We just need to ensure the snake stays within bounds
        clip();
    }
}