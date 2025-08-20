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
import java.util.Random;

public class GameCourt extends JPanel {
    // Logger for error handling
    private static final Logger LOGGER = Logger.getLogger(GameCourt.class.getName());

    // Modern Color Scheme - Cohesive palette
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color BORDER_COLOR = new Color(52, 73, 94);
    private static final Color SNAKE_HEAD_COLOR = new Color(46, 204, 113);
    private static final Color SNAKE_BODY_COLOR = new Color(52, 152, 219);
    private static final Color APPLE_COLOR = new Color(231, 76, 60);
    private static final Color GOLDEN_APPLE_COLOR = new Color(241, 196, 15);
    private static final Color POISON_APPLE_COLOR = new Color(155, 89, 182);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BUTTON_BG_COLOR = new Color(52, 152, 219);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Color BUTTON_HOVER_COLOR = new Color(41, 128, 185);

    // Game constants - these will be dynamic based on window size
    private int boardWidth = 600;
    private int boardHeight = 400;
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

    // Animation and Visual Effects
    private static final int FADE_DURATION = 1000; // milliseconds
    private static final int SCREEN_SHAKE_DURATION = 300; // milliseconds
    private static final int PARTICLE_COUNT = 15;
    private static final int PARTICLE_LIFETIME = 60; // frames
    
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

    // Animation state variables
    private long gameOverStartTime = 0;
    private long screenShakeStartTime = 0;
    private int screenShakeIntensity = 0;
    private LinkedList<Particle> particles = new LinkedList<>();
    private float fadeAlpha = 1.0f;
    private boolean isFading = false;

    // Game images
    private BufferedImage gameOverImg;
    private BufferedImage instructionsImg;

    // Timer for game loop
    private final javax.swing.Timer timer;
    private javax.swing.Timer poisonAppleTimer;
    private javax.swing.Timer animationTimer;

    // Random generator for effects
    private Random random = new Random();

    // Particle class for visual effects
    private static class Particle {
        int x, y;
        int vx, vy;
        int life;
        Color color;
        int size;
        
        Particle(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.life = PARTICLE_LIFETIME;
            Random rand = new Random();
            this.size = rand.nextInt(4) + 2;
            this.vx = rand.nextInt(8) - 4;
            this.vy = rand.nextInt(8) - 4;
        }
        
        void update() {
            x += vx;
            y += vy;
            life--;
            size = Math.max(1, size - 1);
        }
        
        boolean isDead() {
            return life <= 0;
        }
    }

    /**
     * Initializes the game board.
     */
    public GameCourt(JLabel statusInit) {
        // Creates border around the court area with enhanced styling
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 4),
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1)
        ));
        setBackground(BACKGROUND_COLOR);

        loadGameImages();
        loadBestScore();

        // Start the timer, start the game
        ActionListener start = e -> begin();
        timer = new javax.swing.Timer(TIMER_INTERVAL, start);
        timer.start();
        
        // Start animation timer for smooth effects
        ActionListener animate = e -> updateAnimations();
        animationTimer = new javax.swing.Timer(16, animate); // 60 FPS
        animationTimer.start();
        
        setFocusable(true);
        setupKeyboardControls();

        status = statusInit; // initializes the status JLabel
        poisonApple = new PoisonApple(boardWidth, boardHeight);
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
        snake = new Snake(boardWidth, boardHeight);
        apple = new Apple(boardWidth, boardHeight);
        goldenApple = new GoldenApple(boardWidth, boardHeight);
        poisonApple = new PoisonApple(boardWidth, boardHeight);
        
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
     * Updates animations and visual effects
     */
    private void updateAnimations() {
        // Update particles
        particles.removeIf(Particle::isDead);
        particles.forEach(Particle::update);
        
        // Update fade effect
        if (isFading) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - gameOverStartTime;
            if (elapsed < FADE_DURATION) {
                fadeAlpha = 1.0f - ((float) elapsed / FADE_DURATION);
            } else {
                fadeAlpha = 0.0f;
                isFading = false;
            }
        }
        
        // Update screen shake
        if (screenShakeIntensity > 0) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - screenShakeStartTime;
            if (elapsed < SCREEN_SHAKE_DURATION) {
                screenShakeIntensity = (int) (10 * (1.0 - (double) elapsed / SCREEN_SHAKE_DURATION));
            } else {
                screenShakeIntensity = 0;
            }
        }
        
        repaint();
    }

    /**
     * Creates particle effects at the specified location
     */
    private void createParticleEffect(int x, int y, Color color) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            // Create particles in a circular pattern around the snake head
            double angle = (2 * Math.PI * i) / PARTICLE_COUNT;
            double radius = random.nextDouble() * 25 + 5; // Random radius between 5-30
            
            int particleX = x + 10 + (int) (Math.cos(angle) * radius);
            int particleY = y + 10 + (int) (Math.sin(angle) * radius);
            
            particles.add(new Particle(particleX, particleY, color));
        }
    }

    /**
     * Triggers screen shake effect
     */
    private void triggerScreenShake() {
        screenShakeStartTime = System.currentTimeMillis();
        screenShakeIntensity = 10;
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
                gameOverStartTime = System.currentTimeMillis();
                isFading = true;
                triggerScreenShake();
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
            // Get the snake's head position for better particle placement
            Point snakeHead = snake.getGameObjects().getFirst();
            apple.updateSnake(snake);
            
            // Create particle effects at snake head position for better visual effect
            createParticleEffect(snakeHead.x, snakeHead.y, APPLE_COLOR);
            
            // Increase score by 1 point
            score += 1;
            updateScoreAndGenerateFood();
        }

        // If snake hits a golden apple, increase the velocity of snake
        if (goldenApple.intersects(snake)) {
            // Get the snake's head position for better particle placement
            Point snakeHead = snake.getGameObjects().getFirst();
            goldenApple.updateSnake(snake);
            
            // Create golden particle effects at snake head position
            createParticleEffect(snakeHead.x, snakeHead.y, GOLDEN_APPLE_COLOR);
            
            // Increase score by 5 points
            score += 5;
            updateScoreAndGenerateFood();
        }

        // Poison apple: shrink to half, -5 points, clear timer
        if (poisonApple.intersects(snake)) {
            // Get the snake's head position for better particle placement
            Point snakeHead = snake.getGameObjects().getFirst();
            int half = Math.max(1, snake.getGameObjects().size() / 2);
            while (snake.getGameObjects().size() > half) {
                snake.getGameObjects().removeLast();
            }
            score = Math.max(0, score - 5);
            stopPoisonAppleTimer();
            
            // Create poison particle effects and screen shake at snake head position
            createParticleEffect(snakeHead.x, snakeHead.y, POISON_APPLE_COLOR);
            triggerScreenShake();
            
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
            boardWidth, boardHeight, snakeObjs
        );
        
        apple = new Apple(
            gameState.applePx, gameState.applePy, 
            boardWidth, boardHeight, appleObjs
        );
        
        goldenApple = new GoldenApple(
            gameState.goldenPx, gameState.goldenPy, 
            boardWidth, boardHeight, goldenAppleObjs
        );

        // Reset poison apple and its timer
        poisonApple = new PoisonApple(boardWidth, boardHeight);
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
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Apply screen shake effect
        if (screenShakeIntensity > 0) {
            int shakeX = random.nextInt(screenShakeIntensity * 2) - screenShakeIntensity;
            int shakeY = random.nextInt(screenShakeIntensity * 2) - screenShakeIntensity;
            g2d.translate(shakeX, shakeY);
        }

        // Draw enhanced background with subtle pattern
        drawEnhancedBackground(g2d);

        if (instructionsClicked) {
            // Draw instructions image with enhanced styling
            drawInstructionsWithStyle(g2d);
        } else if (!playing) {
            // If not playing draw game over image with enhanced fade effect
            if (fadeAlpha > 0) {
                drawGameOverWithStyle(g2d);
            }
        } else {
            // If playing or reload/save clicked, draw snake and apples
            snake.draw(g2d);
            apple.draw(g2d);
            goldenApple.draw(g2d);
            poisonApple.draw(g2d);
        }
        
        // Draw enhanced particles
        drawEnhancedParticles(g2d);
        
        // Draw UI overlays
        drawUIOverlays(g2d);
        
        // Reset translation for screen shake
        if (screenShakeIntensity > 0) {
            g2d.translate(-g2d.getClipBounds().x, -g2d.getClipBounds().y);
        }
        
        g2d.dispose();
    }

    /**
     * Draws enhanced background with subtle pattern
     */
    private void drawEnhancedBackground(Graphics2D g2d) {
        // Create subtle grid pattern
        g2d.setColor(new Color(255, 255, 255, 10));
        g2d.setStroke(new BasicStroke(1));
        
        for (int x = 0; x < boardWidth; x += 40) {
            g2d.drawLine(x, 0, x, boardHeight);
        }
        for (int y = 0; y < boardHeight; y += 40) {
            g2d.drawLine(0, y, boardWidth, y);
        }
        
        // Add subtle corner highlights
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fillOval(0, 0, 60, 60);
        g2d.fillOval(boardWidth - 60, 0, 60, 60);
        g2d.fillOval(0, boardHeight - 60, 60, 60);
        g2d.fillOval(boardWidth - 60, boardHeight - 60, 60, 60);
    }
    
    /**
     * Draws instructions with enhanced styling
     */
    private void drawInstructionsWithStyle(Graphics2D g2d) {
        // Draw background panel
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(40, 80, boardWidth - 80, boardHeight - 160, 20, 20);
        
        // Draw instructions image
        g2d.drawImage(instructionsImg, 60, 100, boardWidth - 120, boardHeight - 200, null);
        
        // Add title
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
        // g2d.drawString("📖 Game Instructions", 80, 120);
    }
    
    /**
     * Draws game over with enhanced styling
     */
    private void drawGameOverWithStyle(Graphics2D g2d) {
        // Draw background panel
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(40, 80, boardWidth - 80, boardHeight - 160, 20, 20);
        
        // Draw game over image
        g2d.drawImage(gameOverImg, 60, 100, boardWidth - 120, boardHeight - 200, null);
        
        // Add score display
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2d.drawString("Final Score: " + score, 80, 120);
        g2d.drawString("Best Score: " + bestScore, 80, 150);
    }
    
    /**
     * Draws enhanced particle effects
     */
    private void drawEnhancedParticles(Graphics2D g2d) {
        for (Particle particle : particles) {
            float alpha = (float) particle.life / PARTICLE_LIFETIME;
            Color particleColor = new Color(
                particle.color.getRed(),
                particle.color.getGreen(),
                particle.color.getBlue(),
                (int) (255 * alpha)
            );
            
            // Draw particle with glow effect
            g2d.setColor(new Color(particle.color.getRed(), particle.color.getGreen(), 
                                  particle.color.getBlue(), (int) (100 * alpha)));
            g2d.fillOval(particle.x - 2, particle.y - 2, particle.size + 4, particle.size + 4);
            
            g2d.setColor(particleColor);
            g2d.fillOval(particle.x, particle.y, particle.size, particle.size);
        }
    }
    
    /**
     * Draws UI overlays
     */
    private void drawUIOverlays(Graphics2D g2d) {
        if (playing) {
            // Draw enhanced score overlay with better positioning and style
            int scoreX = 20; // Position on left side to avoid apple overlap
            int scoreY = 20;
            int scoreWidth = 180;
            int scoreHeight = 60; // Reduced height since we removed level
            
            // Main score panel with gradient-like effect
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRoundRect(scoreX, scoreY, scoreWidth, scoreHeight, 15, 15);
            
            // Add subtle border
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(scoreX, scoreY, scoreWidth, scoreHeight, 15, 15);
            
            // Add inner highlight
            g2d.setColor(new Color(255, 255, 255, 20));
            g2d.fillRoundRect(scoreX + 2, scoreY + 2, scoreWidth - 4, 20, 13, 13);
            
            // Score text with enhanced styling
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2d.drawString("🎯 SCORE: " + score, scoreX + 15, scoreY + 25);
            
            // Best score with different styling
            g2d.setColor(new Color(255, 215, 0)); // Gold color for best score
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2d.drawString("🏆 BEST: " + bestScore, scoreX + 15, scoreY + 50);
            
            // Add small decorative elements
            g2d.setColor(new Color(255, 255, 255, 40));
            g2d.fillOval(scoreX + scoreWidth - 25, scoreY + 10, 8, 8);
            g2d.fillOval(scoreX + scoreWidth - 15, scoreY + 20, 6, 6);
        }
    }
    
    /**
     * Draws particle effects (legacy method)
     */
    private void drawParticles(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (Particle particle : particles) {
            float alpha = (float) particle.life / PARTICLE_LIFETIME;
            Color particleColor = new Color(
                particle.color.getRed(),
                particle.color.getGreen(),
                particle.color.getBlue(),
                (int) (255 * alpha)
            );
            g2d.setColor(particleColor);
            g2d.fillOval(particle.x, particle.y, particle.size, particle.size);
        }
        
        g2d.dispose();
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(boardWidth, boardHeight);
    }
    
    /**
     * Makes the game board responsive to window resizing
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(400, 300);
    }
    
    /**
     * Handles window resize events for responsive layout
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Adjust game elements for new size if needed
        repaint();
    }
    
    /**
     * Updates the board size for responsive gameplay
     */
    public void updateBoardSize(int newWidth, int newHeight) {
        boardWidth = newWidth;
        boardHeight = newHeight;
        
        // Update game objects with new boundaries
        if (snake != null) {
            snake.updateBoardSize(boardWidth, boardHeight);
        }
        if (apple != null) {
            apple.updateBoardSize(boardWidth, boardHeight);
        }
        if (goldenApple != null) {
            goldenApple.updateBoardSize(boardWidth, boardHeight);
        }
        if (poisonApple != null) {
            poisonApple.updateBoardSize(boardWidth, boardHeight);
        }
        
        repaint();
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