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

public class GameCourt extends JPanel {

    // Game components
    private Snake snake; // The snake, controlled by keyboard input
    private Apple apple; // Apple that doesn't move, repositions randomly after eaten
    private GoldenApple goldenApple; // Golden apple that doesn't move,
    // repositions randomly after eaten
    private final JLabel status; // Current status text (scoreboard)


    // Game state variables
    private int score = 0; // Keeping track of the local score
    private int bestScore = 0; // Keeping track of the best score

    // Button click tracking variables
    private boolean instructionsClicked;
    // Checking whether the instructions button has been clicked
    private boolean reloadClicked; // Checking whether the reload button has been clicked
    private boolean saveClicked; // Checking whether the save button has been clicked

    // Game images
    private static BufferedImage gameOverImg; // Game over image
    private static BufferedImage instructionsImg; // Instructions image

    // Game state
    private boolean playing = false; // Checking whether the game is currently playing

    // Timer for game loop
    private final Timer timer;

    // Game constants
    public static final int BOARD_WIDTH = 600;
    public static final int BOARD_HEIGHT = 400;



    /**
     * Initializes the game board.
     */
    public GameCourt(JLabel statusInit) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        setBackground(Color.WHITE);

        //try to read images
        try {
            gameOverImg = ImageIO.read(new File("files/GameOver.png"));
            instructionsImg = ImageIO.read(new File("files/instructions.jpg"));
        } catch (IOException e) {
            System.out.println("error");
        }

        //try to read best score file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("files/bestScore.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("error");
        }
        try {
            if (reader != null) {
                //set bestScore = best score written on file
                bestScore = Integer.parseInt(reader.readLine().trim());
            }
        } catch (IOException e) {
            System.out.println("error");
        }

        //start the timer, start the game
        ActionListener start = e -> begin();
        timer = new Timer(30, start);
        timer.start();
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!timer.isRunning()) {
                    timer.start();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    snake.setVy(0);
                    snake.setVx(-snake.getSnakeVX());
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    snake.setVy(0);
                    snake.setVx(snake.getSnakeVX());
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    snake.setVx(0);
                    snake.setVy(snake.getSnakeVY());
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    snake.setVx(0);
                    snake.setVy(-snake.getSnakeVY());
                }
            }
        });

        status = statusInit; // initializes the status JLabel
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
        snake.setSnakeVX(3);
        snake.setSnakeVY(3);
        score = 0;
        status.setText("SCORE: " + score + " / BEST: " + bestScore);
        instructionsClicked = false;
        playing = true;
        repaint();
        requestFocusInWindow();
    }

    //helper method to generate a new apple, golden apple 20% of the time

    public void generateApples() {
        if (apple.getGameObjects().size() < 4) {
            apple.add();
        }
        if (Math.random() <= 0.2 && goldenApple.getGameObjects().size() < 2) {
            goldenApple.add();
        }
    }


    /**
     * This method is called every time the timer defined in the constructor
     * triggers.
     */
    public void begin() {
        if (playing) {

            snake.move();








            //if snake hits an apple, increase length of snake
            if (apple.intersects(snake)) {
                apple.updateSnake(snake);

                //increase score by 1 point
                score += 1;
                status.setText("SCORE: " + score + " / BEST: " + bestScore);
                generateApples();


            }

            //if snake hits a golden apple, increase the velocity of snake
            if (goldenApple.intersects(snake)) {
                goldenApple.updateSnake(snake);

                //increase score by 5 points
                score += 5;
                status.setText("SCORE: " + score + " / BEST: " + bestScore);

                generateApples();

            }

            if (score > bestScore) {

                //set new best score
                bestScore = score;
                status.setText("SCORE: " + score + " / BEST: " + bestScore);

                //write new best score to file
                try {
                    BufferedWriter writer =
                            new BufferedWriter(new FileWriter("files/bestScore.txt"));
                    writer.write(Integer.toString(score));
                    writer.close();
                } catch (IOException e) {
                    System.out.println("error");
                }

            }

            //if snake hits itself or a wall, game is over
            if (snake.hasHitItself() || snake.hasHitWall()) {
                playing = false;
            }
            // update the display
            repaint();
        }
    }

    public void instructions() {
        instructionsClicked = true; //if instructions are clicked, stop the playing screen
        playing = false;
        repaint(); // update the display
        requestFocusInWindow();
    }

    //helper function to write game objects to file

    public void writeGameObjectsToFile(String filePath, LinkedList<Point> gameObjects) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false));

            for (Point gameObject : gameObjects) {
                writer.write(Integer.toString(gameObject.x));
                writer.write(",");
                writer.write(Integer.toString(gameObject.y));
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("error");
        }
    }


    // write game state to file
    // save snake and apple positions, snake velocity, and current score
    public void save() {
        try {
            BufferedWriter stateWriter = getBufferedWriter();
            stateWriter.close();
        } catch (IOException e) {
            System.out.println("error");
        }

        writeGameObjectsToFile("files/snakeObjs.txt", snake.getGameObjects());
        writeGameObjectsToFile("files/appleObjs.txt", apple.getGameObjects());
        writeGameObjectsToFile("files/goldenAppleObjs.txt", goldenApple.getGameObjects());

        // update buttons
        saveClicked = true;
        instructionsClicked = false;
        reloadClicked = false;
        timer.stop();
        playing = true;

        repaint(); // update the display
        requestFocusInWindow();
    }

    //Helper method to get a BufferedWriter for writing the game state to a file.


    private BufferedWriter getBufferedWriter() throws IOException {
        BufferedWriter stateWriter =
                new BufferedWriter(new FileWriter("files/gameState.txt",false));

        stateWriter.write("snake," + snake.getPx() + "," + snake.getPy());
        stateWriter.newLine();
        stateWriter.write("apple," + apple.getPx() + "," + apple.getPy());
        stateWriter.newLine();
        stateWriter.write("golden," + goldenApple.getPx() + "," + goldenApple.getPy());
        stateWriter.newLine();
        stateWriter.write(snake.getSnakeVX() + "," + snake.getSnakeVY());
        stateWriter.newLine();
        stateWriter.write(Integer.toString(score));
        return stateWriter;
    }

    // Helper method to read game objects from a file.

    private LinkedList<Point> readGameObjectsFromFile(String fileName) {
        LinkedList<Point> gameObjects = new LinkedList<>();
        FileLineIterator iterator = new FileLineIterator("files/" + fileName);
        String[] coords;

        while (iterator.hasNext()) {
            coords = iterator.next().split(",");
            gameObjects.add(new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
        }

        return gameObjects;
    }


    public void reload() {
        int snakePx = 0, snakePy = 0, applePx = 0, applePy = 0,
                goldenPx = 0, goldenPy = 0, snakeVX = 0, snakeVY = 0,
                localScore = 0;

        try {
            BufferedReader stateReader = new BufferedReader(new FileReader("files/gameState.txt"));

            //read in snake position
            String snakePosLine = null;
            try {
                try {
                    snakePosLine = stateReader.readLine().trim();
                } catch (NullPointerException e) {
                    reset();
                    return;
                }
            } catch (IOException e) {
                System.out.println("error");
            }
            String [] snakePosition;
            if (snakePosLine != null) {
                snakePosition = snakePosLine.split(",");
                snakePx = Integer.parseInt(snakePosition[1]);
                snakePy = Integer.parseInt(snakePosition[2]);
            }

            //read in apple position
            String applePosLine = null;
            try {
                applePosLine = stateReader.readLine().trim();
            } catch (IOException e) {
                System.out.println("error");
            }
            String [] applePosition;
            if (applePosLine != null) {
                applePosition = applePosLine.split(",");
                applePx = Integer.parseInt(applePosition[1]);
                applePy = Integer.parseInt(applePosition[2]);
            }

            //read in golden apple position
            String goldenPosLine = null;
            try {
                goldenPosLine = stateReader.readLine().trim();
            } catch (IOException e) {
                System.out.println("error");
            }
            String [] goldenPosition;
            if (goldenPosLine != null) {
                goldenPosition = goldenPosLine.split(",");
                goldenPx = Integer.parseInt(goldenPosition[1]);
                goldenPy = Integer.parseInt(goldenPosition[2]);
            }

            //read snakeVX and snakeVY
            String velocityLine = null;
            try {
                velocityLine = stateReader.readLine().trim();
            } catch (IOException e) {
                System.out.println("error");
            }
            String [] velocity;
            if (velocityLine != null) {
                velocity = velocityLine.split(",");
                snakeVX = Integer.parseInt(velocity[0]);
                snakeVY = Integer.parseInt(velocity[1]);
            }

            //read in local score at time of reloaded game
            String scoreLine = null;
            try {
                scoreLine = stateReader.readLine().trim();
            } catch (IOException e) {
                System.out.println("error");
            }
            if (scoreLine != null) {
                localScore = Integer.parseInt(scoreLine);
            }

        } catch (FileNotFoundException e) {
            reset();
        }

        LinkedList<Point> snakeObjs = readGameObjectsFromFile("snakeObjs.txt");
        LinkedList<Point> appleObjs = readGameObjectsFromFile("appleObjs.txt");
        LinkedList<Point> goldenAppleObjs = readGameObjectsFromFile("goldenAppleObjs.txt");


        snake = new Snake(snakePx, snakePy, BOARD_WIDTH, BOARD_HEIGHT, snakeObjs);
        apple = new Apple(applePx, applePy, BOARD_WIDTH, BOARD_HEIGHT, appleObjs);
        goldenApple = new GoldenApple(goldenPx, goldenPy, BOARD_WIDTH, BOARD_HEIGHT,
                goldenAppleObjs);

        snake.setSnakeVX(snakeVX);
        snake.setSnakeVY(snakeVY);
        score = localScore;
        status.setText("SCORE: " + score + " / BEST: " + bestScore);

        reloadClicked = true;
        saveClicked = false;
        instructionsClicked = false;
        timer.stop();
        playing = true;

        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (instructionsClicked) { //draw instructions image if instruction button pressed
            g.drawImage(instructionsImg, 50, 100, 500, 200, null);
        } else if ((!playing)) { //if not playing draw game over image
            g.drawImage(gameOverImg, 50, 100, 500, 200, null);
        } else { //if playing or reload/save clicked
            //draw snake and apples
            snake.draw(g);
            apple.draw(g);
            goldenApple.draw(g);
        }
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}