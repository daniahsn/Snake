package org.cis1200.snake;

import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

public class SnakeTest {

    @Test
    public void testSnakeMove() {
        Snake snake = new Snake(400,400);
        snake.grow(1);
        snake.setVx(3);
        snake.setVy(3);
        Point first = snake.getGameObjects().get(0).getLocation();
        snake.move();
        assertEquals(snake.getGameObjects().get(1).getLocation(), first);
        assertEquals(snake.getGameObjects().get(0).getLocation(), new Point(23, 23));
    }

    @Test
    public void testSnakeGrow() {
        Snake snake = new Snake(400,400);
        assertEquals(snake.getGameObjects().size(),1);
        snake.grow(3);
        assertEquals(snake.getGameObjects().size(),4);
    }

    @Test
    public void testAppleAdd() {
        Apple apple = new Apple(400,400);
        assertEquals(apple.getGameObjects().size(), 1);
        apple.add();
        assertEquals(apple.getGameObjects().size(), 2);
    }

    @Test
    public void testAppleRemove() {
        Apple apple = new Apple(400,400);
        assertEquals(apple.getGameObjects().size(), 1);
        apple.remove(0);
        assertEquals(apple.getGameObjects().size(), 0);
    }

    @Test
    public void testAppleIntersects() {
        Snake snake = new Snake(400,400);
        Apple apple = new Apple(400,400);
        Point p = apple.getGameObjects().get(0).getLocation();
        snake.setPx(p.x);
        snake.setPy(p.y);
        assertTrue(apple.intersects(snake));
    }

    @Test
    public void testApplePowerUp() {
        Snake snake = new Snake(400,400);
        Apple apple = new Apple(400,400);
        apple.updateSnake(snake);
        assertEquals(snake.getGameObjects().size(), 4);
    }

    @Test
    public void testGoldenAppleAdd() {
        GoldenApple goldenApple = new GoldenApple(400,400);
        assertEquals(goldenApple.getGameObjects().size(), 1);
        goldenApple.add();
        assertEquals(goldenApple.getGameObjects().size(), 2);
    }

    @Test
    public void testGoldenAppleRemove() {
        GoldenApple goldenApple = new GoldenApple(400,400);
        assertEquals(goldenApple.getGameObjects().size(), 1);
        goldenApple.remove(0);
        assertEquals(goldenApple.getGameObjects().size(), 0);
    }

    @Test
    public void testGoldenAppleIntersects() {
        Snake snake = new Snake(400,400);
        GoldenApple goldenApple = new GoldenApple(400,400);
        Point p = goldenApple.getGameObjects().get(0).getLocation();
        snake.setPx(p.x);
        snake.setPy(p.y);
        assertTrue(goldenApple.intersects(snake));
    }

    @Test
    public void testGoldenApplePowerUp() {
        Snake snake = new Snake(400,400);
        GoldenApple goldenApple = new GoldenApple(400,400);
        goldenApple.updateSnake(snake);
        assertEquals(snake.getSnakeVX(), 9);
        assertEquals(snake.getSnakeVY(), 9);
    }

    @Test
    public void testSnakeHasHitWall() {
        Snake snake = new Snake(400,400);
        snake.setVx(100);
        snake.setVy(0);
        snake.setPx(500);
        assertEquals(snake.hitWall(), Direction.RIGHT);
        assertTrue(snake.hasHitWall());

        snake.setVx(0);
        snake.setVy(100);
        snake.setPx(20);
        snake.setPy(500);
        assertEquals(snake.hitWall(), Direction.DOWN);
        assertTrue(snake.hasHitWall());

        snake.setVx(-100);
        snake.setVy(0);
        snake.setPx(-500);
        snake.setPy(20);
        assertEquals(snake.hitWall(), Direction.LEFT);
        assertTrue(snake.hasHitWall());

        snake.setVx(0);
        snake.setVy(-100);
        snake.setPx(20);
        snake.setPy(-500);
        assertEquals(snake.hitWall(), Direction.UP);
        assertTrue(snake.hasHitWall());
    }

    @Test
    public void testSnakeHasHitItself() {
        Snake snake = new Snake(400,400);
        //snake's initial position will be at (20, 20)
        snake.getGameObjects().add(new Point(30, 30));
        snake.setVx(10);
        snake.setVy(10);
        assertTrue(snake.hasHitItself());
    }

    @Test
    public void testAppleConstructor() {
        Apple apple = new Apple(400, 400);
        assertNotNull(apple);
        assertEquals(1, apple.getGameObjects().size());
    }

    @Test
    public void testGoldenAppleConstructor() {
        GoldenApple goldenApple = new GoldenApple(400, 400);
        assertNotNull(goldenApple);
        assertEquals(1, goldenApple.getGameObjects().size());
    }

    @Test
    public void testAppleUpdateSnake() {
        Snake snake = new Snake(400, 400);
        Apple apple = new Apple(400, 400);
        apple.updateSnake(snake);
        assertEquals(4, snake.getGameObjects().size());
    }

    @Test
    public void testGoldenAppleUpdateSnake() {
        Snake snake = new Snake(400, 400);
        GoldenApple goldenApple = new GoldenApple(400, 400);
        goldenApple.updateSnake(snake);
        assertEquals(1, snake.getGameObjects().size());
        assertEquals(9, snake.getSnakeVX());
        assertEquals(9, snake.getSnakeVY());
    }

    @Test
    public void testGameCourtInitialization() {
        JLabel status = new JLabel();
        GameCourt court = new GameCourt(status);
        assertNotNull(court);
        assertTrue(court.getPreferredSize().getWidth() > 0);
        assertTrue(court.getPreferredSize().getHeight() > 0);
    }

    @Test
    public void testGameCourtReset() {
        JLabel status = new JLabel();
        GameCourt court = new GameCourt(status);
        court.reset();
        assertEquals(1, court.getSnake().getGameObjects().size());
        assertEquals(1, court.getApple().getGameObjects().size());
        assertEquals(1, court.getGoldenApple().getGameObjects().size());
        assertEquals(3, court.getSnake().getSnakeVX());
        assertEquals(3, court.getSnake().getSnakeVY());
        assertEquals(0, court.getScore());
    }

    @Test
    public void testGameCourtInstructions() {
        JLabel status = new JLabel();
        GameCourt court = new GameCourt(status);
        court.instructions();
        assertFalse(court.isPlaying());
    }




}