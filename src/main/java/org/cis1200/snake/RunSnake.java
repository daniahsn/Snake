package org.cis1200.snake;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RunSnake implements Runnable {
    private int bestScore;

    public void run() {

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

        // Top-level frame in which game components live
        final JFrame frame = new JFrame("Snake Game");
        frame.setLocation(300, 200);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("SCORE: 0 / BEST: " + bestScore);
        status_panel.add(status);

        // Main playing area
        final GameCourt court = new GameCourt(status);
        frame.add(court, BorderLayout.CENTER);

        // Instructions button
        final JButton instructions = new JButton("INSTRUCTIONS");
        instructions.addActionListener(e -> court.instructions());

        // Reset button
        final JButton reset = new JButton("RESET");
        reset.addActionListener(e -> court.reset());

        // Save button
        final JButton save = new JButton("SAVE");
        save.addActionListener(e -> {
            court.save();

        });

        // Reload button
        final JButton reload = new JButton("RELOAD");
        reload.setFont(new Font("Andale Mono", Font.BOLD,12));
        reload.addActionListener(e -> court.reload());
        reload.addActionListener(e -> {
            court.reload();
        });

        status_panel.add(instructions);
        status_panel.add(reset);
        status_panel.add(save);
        status_panel.add(reload);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start game
        court.reset();
    }
}