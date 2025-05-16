package org.cis1200.snake;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameCourt extends JPanel {
    // Logger for error handling
    private static final Logger LOGGER = Logger.getLogger(GameCourt.class.getName());

    // Game constants
    public static final int BOARD_WIDTH = 600;
    public static final int BOARD_HEIGHT = 400;
    private static final int TIMER_INTERVAL = 30;
    private static final String BEST_SCORE_PATH = "files/bestScore.txt";
    private static final String GAME_STATE_PATH = "files/gameState.txt";
    private static final String SNAKE_OBJS_PATH = "files/snakeObjs.txt";
    private static final String APPLE_OBJS_PATH = "files/appleObjs.txt";
    private static final String GOLDEN_APPLE_OBJS_PATH = "files/goldenAppleObjs.txt";
    private static final double GOLDEN_APPLE_SPAWN_CHANCE = 0.2;
    private static final int MAX_APPLES = 4;
    private static final int MAX_GOLDEN_APPLES = 2;
    private static final double POISON_APPLE_SPAWN_CHANCE = 0.05;
    private static final int MAX_POISON_APPLES = 1;

    // Game components
    private Snake snake;
    private Apple apple;
    private GoldenApple goldenApple;
    private PoisonApple poisonApple;
    private final JLabel status;

    // Game state variables
    private int score = 0;
    private int bestScore = 0;
    private boolean playing = false;
    private boolean instructionsClicked;
    private boolean reloadClicked;
    private boolean saveClicked;

    // Game images
    private BufferedImage gameOverImg;
    private BufferedImage instructionsImg;

    // Timer for game loop
    private final javax.swing.Timer timer;
    private javax.swing.Timer poisonAppleTimer;

    /**
     * Initializes the game board.
     */
    public GameCourt(JLabel statusInit) {
        // Creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(Color.WHITE);

        loadGameImages();
        loadBestScore();

        // Start the timer, start the game
        ActionListener start = e -> begin();
        timer = new javax.swing.Timer(TIMER_INTERVAL, start);
        timer.start();
        
        setFocusable(true);
        setupKeyboardControls();

        status = statusInit; // initializes the status JLabel
        poisonApple = new PoisonApple(BOARD_WIDTH, BOARD_HEIGHT);
    }

    /**
     * Loads game images from files.
     */
    private void loadGameImages() {
        try {
            gameOverImg = ImageIO.read(new File("files/GameOver.png"));
            instructionsImg = ImageIO.read(new File("files/instructions.jpg"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load game images", e);
        }
    }

    /**
     * Loads the best score from file.
     */
    private void loadBestScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BEST_SCORE_PATH))) {
            bestScore = Integer.parseInt(reader.readLine().trim());
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.INFO, "Could not load best score, using default", e);
            // If the file doesn't exist or has invalid content, bestScore remains 0
        }
    }

    /**
     * Sets up keyboard controls for the snake.
     */
    private void setupKeyboardControls() {
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!timer.isRunning()) {
                    timer.start();
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        snake.setVy(0);
                        snake.setVx(-snake.getSnakeVX());
                        break;
                    case KeyEvent.VK_RIGHT:
                        snake.setVy(0);
                        snake.setVx(snake.getSnakeVX());
                        break;
                    case KeyEvent.VK_DOWN:
                        snake.setVx(0);
                        snake.setVy(snake.getSnakeVY());
                        break;
                    case KeyEvent.VK_UP:
                        snake.setVx(0);
                        snake.setVy(-snake.getSnakeVY());
                        break;
                    default:
                        // Ignore other keys
                        break;
                }
            }
        });
    }

    // GETTERS FOR TESTING
    public boolean isPlaying() {
        return playing;
    }

    public Apple getApple() {
        return apple;
    }

    public int getScore() {
        return score;
    }

    public GoldenApple getGoldenApple() {
        return goldenApple;
    }

    public Snake getSnake() {
        return snake;
    }

    /**
     * (Re-)set the game to its initial state.
     */
    public void reset() {
        if (!timer.isRunning()) {
            timer.start();
        }
        
        // Reset game components and variables
        snake = new Snake(BOARD_WIDTH, BOARD_HEIGHT);
        apple = new Apple(BOARD_WIDTH, BOARD_HEIGHT);
        goldenApple = new GoldenApple(BOARD_WIDTH, BOARD_HEIGHT);
        poisonApple = new PoisonApple(BOARD_WIDTH, BOARD_HEIGHT);
        
        // Stop the poison apple timer if it's running
        stopPoisonAppleTimer();
        
        snake.setSnakeVX(3);
        snake.setSnakeVY(3);
        score = 0;
        
        updateScoreDisplay();
        
        instructionsClicked = false;
        playing = true;
        
        repaint();
        requestFocusInWindow();
    }

    /**
     * Helper method to stop the poison apple timer if it exists and is running
     */
    private void stopPoisonAppleTimer() {
        if (poisonAppleTimer != null && poisonAppleTimer.isRunning()) {
            poisonAppleTimer.stop();
        }
    }

    /**
     * Updates the score display.
     */
    private void updateScoreDisplay() {
        status.setText("SCORE: " + score + " / BEST: " + bestScore);
    }

    /**
     * Helper method to generate apples, with golden apples appearing less frequently.
     */
    public void generateApples() {
        if (apple.getGameObjects().size() < MAX_APPLES) {
            apple.add();
        }
        
        if (Math.random() <= GOLDEN_APPLE_SPAWN_CHANCE && 
            goldenApple.getGameObjects().size() < MAX_GOLDEN_APPLES) {
            goldenApple.add();
        }
       
    }

    /**
     * This method is called every time the timer defined in the constructor triggers.
     * It handles the main game logic.
     */
    public void begin() {
        if (playing) {
            snake.move();

            // Check collisions with food
            handleFoodCollisions();
            
            // Check for game over conditions
            if (snake.hasHitItself() || snake.hasHitWall()) {
                playing = false;
            }
            
            // Update the display
            repaint();
        }
    }

    /**
     * Handles collisions between the snake and food items.
     */
    private void handleFoodCollisions() {
        // If snake hits an apple, increase length of snake
        if (apple.intersects(snake)) {
            apple.updateSnake(snake);
            
            // Increase score by 1 point
            score += 1;
            updateScoreAndGenerateFood();
        }

        // If snake hits a golden apple, increase the velocity of snake
        if (goldenApple.intersects(snake)) {
            goldenApple.updateSnake(snake);
            
            // Increase score by 5 points
            score += 5;
            updateScoreAndGenerateFood();
        }

        // Poison apple: shrink to half, -5 points, clear timer
        if (poisonApple.intersects(snake)) {
            int half = Math.max(1, snake.getGameObjects().size() / 2);
            while (snake.getGameObjects().size() > half) {
                snake.getGameObjects().removeLast();
            }
            score = Math.max(0, score - 5);
            stopPoisonAppleTimer();
            updateScoreAndGenerateFood();
        }
    }

    /**
     * Updates the score display and generates new food items.
     */
    private void updateScoreAndGenerateFood() {
        // Update best score if needed
        if (score > bestScore) {
            bestScore = score;
            saveBestScore();
        }
        
        updateScoreDisplay();
        generateApples();
    }

    /**
     * Saves the best score to file.
     */
    private void saveBestScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BEST_SCORE_PATH))) {
            writer.write(Integer.toString(bestScore));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save best score", e);
        }
    }

    /**
     * Shows the instructions screen.
     */
    public void instructions() {
        instructionsClicked = true;
        playing = false;
        repaint();
        requestFocusInWindow();
    }

    /**
     * Helper function to write game objects to file.
     */
    private void writeGameObjectsToFile(String filePath, LinkedList<Point> gameObjects) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Point gameObject : gameObjects) {
                writer.write(Integer.toString(gameObject.x));
                writer.write(",");
                writer.write(Integer.toString(gameObject.y));
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to write game objects to " + filePath, e);
        }
    }

    /**
     * Saves the current game state to files.
     */
    public void save() {
        // Save game state
        try (BufferedWriter stateWriter = new BufferedWriter(new FileWriter(GAME_STATE_PATH, false))) {
            // Write snake information
            stateWriter.write("snake," + snake.getPx() + "," + snake.getPy());
            stateWriter.newLine();
            
            // Write apple information
            stateWriter.write("apple," + apple.getPx() + "," + apple.getPy());
            stateWriter.newLine();
            
            // Write golden apple information
            stateWriter.write("golden," + goldenApple.getPx() + "," + goldenApple.getPy());
            stateWriter.newLine();
            
            // Write snake velocity
            stateWriter.write(snake.getSnakeVX() + "," + snake.getSnakeVY());
            stateWriter.newLine();
            
            // Write current score
            stateWriter.write(Integer.toString(score));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save game state", e);
        }

        // Save game objects
        writeGameObjectsToFile(SNAKE_OBJS_PATH, snake.getGameObjects());
        writeGameObjectsToFile(APPLE_OBJS_PATH, apple.getGameObjects());
        writeGameObjectsToFile(GOLDEN_APPLE_OBJS_PATH, goldenApple.getGameObjects());

        // Update button states
        saveClicked = true;
        instructionsClicked = false;
        reloadClicked = false;
        
        timer.stop();
        playing = true;

        repaint();
        requestFocusInWindow();
    }

    /**
     * Helper method to read game objects from a file.
     */
    private LinkedList<Point> readGameObjectsFromFile(String fileName) {
        LinkedList<Point> gameObjects = new LinkedList<>();
        
        try {
            FileLineIterator iterator = new FileLineIterator("files/" + fileName);
            
            while (iterator.hasNext()) {
                String[] coords = iterator.next().split(",");
                gameObjects.add(new Point(
                    Integer.parseInt(coords[0]), 
                    Integer.parseInt(coords[1])
                ));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error reading game objects from " + fileName, e);
        }
        
        return gameObjects;
    }

    /**
     * Reloads a saved game state.
     */
    public void reload() {
        GameState gameState = loadGameState();
        
        if (gameState == null) {
            reset();
            return;
        }
        
        // Load the saved game objects
        LinkedList<Point> snakeObjs = readGameObjectsFromFile("snakeObjs.txt");
        LinkedList<Point> appleObjs = readGameObjectsFromFile("appleObjs.txt");
        LinkedList<Point> goldenAppleObjs = readGameObjectsFromFile("goldenAppleObjs.txt");

        // Recreate game objects from saved state
        snake = new Snake(
            gameState.snakePx, gameState.snakePy, 
            BOARD_WIDTH, BOARD_HEIGHT, snakeObjs
        );
        
        apple = new Apple(
            gameState.applePx, gameState.applePy, 
            BOARD_WIDTH, BOARD_HEIGHT, appleObjs
        );
        
        goldenApple = new GoldenApple(
            gameState.goldenPx, gameState.goldenPy, 
            BOARD_WIDTH, BOARD_HEIGHT, goldenAppleObjs
        );

        // Reset poison apple and its timer
        poisonApple = new PoisonApple(BOARD_WIDTH, BOARD_HEIGHT);
        stopPoisonAppleTimer();

        // Set snake velocity from saved state
        snake.setSnakeVX(gameState.snakeVX);
        snake.setSnakeVY(gameState.snakeVY);
        
        // Set score from saved state
        score = gameState.score;
        updateScoreDisplay();

        // Update button states
        reloadClicked = true;
        saveClicked = false;
        instructionsClicked = false;
        
        timer.stop();
        playing = true;

        repaint();
        requestFocusInWindow();
    }

    /**
     * Loads the game state from file.
     */
    private GameState loadGameState() {
        GameState state = new GameState();
        
        try (BufferedReader stateReader = new BufferedReader(new FileReader(GAME_STATE_PATH))) {
            // Read snake position
            String snakePosLine = stateReader.readLine();
            if (snakePosLine == null) {
                return null;
            }
            
            String[] snakePosition = snakePosLine.trim().split(",");
            state.snakePx = Integer.parseInt(snakePosition[1]);
            state.snakePy = Integer.parseInt(snakePosition[2]);

            // Read apple position
            String applePosLine = stateReader.readLine();
            if (applePosLine != null) {
                String[] applePosition = applePosLine.trim().split(",");
                state.applePx = Integer.parseInt(applePosition[1]);
                state.applePy = Integer.parseInt(applePosition[2]);
            }

            // Read golden apple position
            String goldenPosLine = stateReader.readLine();
            if (goldenPosLine != null) {
                String[] goldenPosition = goldenPosLine.trim().split(",");
                state.goldenPx = Integer.parseInt(goldenPosition[1]);
                state.goldenPy = Integer.parseInt(goldenPosition[2]);
            }

            // Read snake velocity
            String velocityLine = stateReader.readLine();
            if (velocityLine != null) {
                String[] velocity = velocityLine.trim().split(",");
                state.snakeVX = Integer.parseInt(velocity[0]);
                state.snakeVY = Integer.parseInt(velocity[1]);
            }

            // Read score
            String scoreLine = stateReader.readLine();
            if (scoreLine != null) {
                state.score = Integer.parseInt(scoreLine.trim());
            }
            
            return state;
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.INFO, "No saved game state found", e);
            return null;
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Error loading game state", e);
            return null;
        }
    }

    /**
     * Paints the game components on the screen.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (instructionsClicked) {
            // Draw instructions image if instruction button pressed
            g.drawImage(instructionsImg, 50, 100, 500, 200, null);
        } else if (!playing) {
            // If not playing draw game over image
            g.drawImage(gameOverImg, 50, 100, 500, 200, null);
        } else {
            // If playing or reload/save clicked, draw snake and apples
            snake.draw(g);
            apple.draw(g);
            goldenApple.draw(g);
            poisonApple.draw(g);
        }
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
    
    /**
     * Inner class to hold game state data when loading/saving.
     */
    private static class GameState {
        int snakePx = 0;
        int snakePy = 0; 
        int applePx = 0;
        int applePy = 0;
        int goldenPx = 0;
        int goldenPy = 0;
        int snakeVX = 0;
        int snakeVY = 0;
        int score = 0;
    }
}