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
        final JFrame frame = new JFrame("🎮 Ultimate Snake Adventure");
        frame.setLocation(300, 200);
        
        // Modern styling will be applied through custom colors and fonts

        // Create main container with gradient background
        JPanel mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Create beautiful gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185),
                    getWidth(), getHeight(), new Color(52, 73, 94)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainContainer.setLayout(new BorderLayout(0, 0));
        frame.setContentPane(mainContainer);

        // Enhanced status panel with modern styling
        final JPanel status_panel = createEnhancedStatusPanel();
        mainContainer.add(status_panel, BorderLayout.SOUTH);
        
        final JLabel status = createEnhancedStatusLabel(bestScore);
        status_panel.add(status);

        // Main playing area
        final GameCourt court = new GameCourt(status);
        mainContainer.add(court, BorderLayout.CENTER);

        // Create enhanced button panel
        JPanel buttonPanel = createEnhancedButtonPanel(court);
        status_panel.add(buttonPanel);

        // Add component listener for responsive resizing
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                court.updateBoardSize(frame.getWidth() - 50, frame.getHeight() - 150);
            }
        });

        // Make frame responsive with better sizing
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setPreferredSize(new Dimension(900, 700));
        frame.setResizable(true);
        
        // Add window decorations and styling
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);

        // Start game
        court.reset();
    }
    
    /**
     * Creates an enhanced status panel with modern styling
     */
    private JPanel createEnhancedStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 10));
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(41, 128, 185)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Add subtle shadow effect
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 2, 0),
            panel.getBorder()
        ));
        
        return panel;
    }
    
    /**
     * Creates an enhanced status label with better typography
     */
    private JLabel createEnhancedStatusLabel(int bestScore) {
        JLabel status = new JLabel("SCORE: 0 / BEST: " + bestScore);
        status.setFont(new Font("Segoe UI", Font.BOLD, 18));
        status.setForeground(Color.WHITE);
        status.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        
        // Add text shadow effect
        status.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 0, 20),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        return status;
    }
    
    /**
     * Creates an enhanced button panel with better layout
     */
    private JPanel createEnhancedButtonPanel(GameCourt court) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        // Create enhanced buttons with better styling
        final JButton instructions = createEnhancedButton("📖 INSTRUCTIONS", new Color(52, 152, 219));
        instructions.addActionListener(e -> court.instructions());

        final JButton reset = createEnhancedButton("🔄 RESET", new Color(46, 204, 113));
        reset.addActionListener(e -> court.reset());

        final JButton save = createEnhancedButton("💾 SAVE", new Color(155, 89, 182));
        save.addActionListener(e -> {
            court.save();
        });

        final JButton reload = createEnhancedButton("📂 RELOAD", new Color(241, 196, 15));
        reload.addActionListener(e -> court.reload());

        buttonPanel.add(instructions);
        buttonPanel.add(reset);
        buttonPanel.add(save);
        buttonPanel.add(reload);
        
        return buttonPanel;
    }
    
    /**
     * Creates an enhanced button with modern styling and effects
     */
    private JButton createEnhancedButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    // Pressed state - darker
                    g2d.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    // Hover state - lighter
                    g2d.setColor(baseColor.brighter());
                } else {
                    // Normal state
                    g2d.setColor(baseColor);
                }
                
                // Draw rounded rectangle with shadow
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                
                // Add inner highlight
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRoundRect(3, 3, getWidth() - 6, (getHeight() - 6) / 2, 10, 10);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 45));
        
        // Add smooth hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2, true),
                    BorderFactory.createEmptyBorder(10, 18, 10, 18)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
            }
        });
        
        return button;
    }
}