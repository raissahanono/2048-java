package org.cis1200.twentyfortyeight;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private TwentyFortyEight tfe; // model for the game
    private JLabel status; // current status text at bottom
    private JButton scoreL; // score display
    private JButton highestScoreL; // high score button number
    private static final String SAVE_FILE = "save.txt"; //filename to save and load from (file i/o)

    // game constants
    public static final int SIZE = 4; //4x4 board
    public static final int TILE_PIXELS = 100; // tiles are 100x100
    public static final int BOARD_WIDTH = 600;
    public static final int BOARD_HEIGHT = 600;

    /**
     * Initializes the game board.
     */
    public GameBoard(JLabel statusInit, JButton scoreInit, JButton highestScoreInit) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        tfe = new TwentyFortyEight(); // initializes model for the game
        status = statusInit; // initializes the status JLabel
        highestScoreL = highestScoreInit; // initializes highest score
        scoreL = scoreInit; // initializes score

        load(); //calls load method below

        /*
         * Listens for changes in keyboard presses (UP DOWN LEFT RIGHT)
         * Updates the model, then updates the game
         * board based off of the updated model.
         */
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode(); // code for key pressed
                boolean changed = false; // checks if board changed
                if (code == KeyEvent.VK_LEFT) {
                    changed = tfe.move(TwentyFortyEight.LEFT);
                } else if (code == KeyEvent.VK_RIGHT) {
                    changed = tfe.move(TwentyFortyEight.RIGHT);
                } else if (code == KeyEvent.VK_UP) {
                    changed = tfe.move(TwentyFortyEight.UP);
                } else if (code == KeyEvent.VK_DOWN) {
                    changed = tfe.move(TwentyFortyEight.DOWN);
                }
                if (changed) {
                    updateStatus(); // updates the status JLabel
                    repaint(); // repaints the game board
                    requestFocusInWindow();
                }
            }
        });
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        tfe.reset();
        updateStatus();
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    public void undo() {
        boolean undo = tfe.pressUndo();
        if (undo) {
            updateStatus();
            repaint();
            requestFocusInWindow();
        }
    }

    /**
     * Instruction Message
     */
    public void instructions() {
        JOptionPane.showMessageDialog(
                this,
                "Welcome to 2048!\n" +
                "The goal of the game is to get a tile with the number 2048 " +
                        "on it. \nYou play by moving the " +
                "arrow keys up, down, right, and left to move the tiles in " +
                "the same direction.\n When you press a key so that two " +
                        "tiles with the same value touch " +
                "they will merge and a new tile will be randomly added." +
                        "\nFor example, When you swipe so that two " +
                        "tiles with 2s on them touch, the two " +
                "2s disappear and are replaced with a 4 on one of the tiles." +
                        "\nYou can work your way up―2, 4, 8, 16, " +
                "32, 64, 128, 256, 512, 1024, and, finally, 2048 You win by reaching 2048!" +
                        "\nIf the grid becomes " +
                        "completely full with numbers on all tiles and no two " +
                        "adjacent tiles have the same number, " +
                        "the game automatically ends.\n\n" +
                        "Try to reach 2048!\n" +
                        "Press Reset to start over.",
                "Instructions",
                JOptionPane.INFORMATION_MESSAGE
        );
        requestFocusInWindow();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        int s = tfe.getScore();
        scoreL.setText("Score: " + s);
        highestScoreL.setText("Highest Score: " + tfe.getHighestScore());
        if (tfe.hasWon()) {
            status.setText("You win!     Score: " + tfe.getScore());
        } else if (tfe.isGameOver()) {
            status.setText("Game over :(     Score: " + tfe.getScore());
        } else {
            status.setText("Score: " + s);
        }
    }

    public void save() {
        tfe.saveGame(SAVE_FILE);
        requestFocusInWindow();
    }

    public void load() {
        tfe.loadGame(SAVE_FILE);
        updateStatus();
        repaint();
        requestFocusInWindow();
    }

    /**
     * Draws the game board.
     * <p>
     * There are many ways to draw a game board. This approach
     * will not be sufficient for most games, because it is not
     * modular. All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper
     * methods. Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();
        int gridW = SIZE * TILE_PIXELS;
        int gridH = SIZE * TILE_PIXELS;
        int xOffset = (w - gridW) / 2;
        int yOffset = (h - gridH) / 2;

        // draw background color
        g.setColor(new Color(0xFAF8EF));
        g.fillRect(0, 0, w, h);

        // board background
        g.setColor(new Color(0xBBADA0));
        g.fillRoundRect(
                xOffset - 12, yOffset - 12, gridW + 24, gridH + 24, 16, 16
        );

        // draw tiles
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int state = tfe.getCell(r, c);
                drawTile(g, r, c, state, xOffset, yOffset); // using helper method to draw a tile
            }
        }
    }

    private void drawTile(Graphics g, int r, int c, int state, int xOffset, int yOffset) {
        int x = xOffset + c * TILE_PIXELS; // horizontal pixel start, columns
        int y = yOffset + r * TILE_PIXELS; // vertical start, rows

        if (state == 0) {
            g.setColor(new Color(0xCDC1B4));
        } else {
            g.setColor(tileColor(state));
        }

        // draw rectangle, use padding so rectangles don't touch
        g.fillRoundRect(
                x + 8, y + 8, TILE_PIXELS - 16, TILE_PIXELS - 16, 16, 16
        );

        if (state != 0) { // if tile not empty draw the number
            g.setColor(Color.BLACK); // color
            g.setFont(new Font("Arial", Font.BOLD, 28)); // font

            String s = String.valueOf(state); // number turns into a string to display

            // center the digits, depending on length of number
            int digits = s.length();
            int textX;
            if (digits == 1) {
                textX = x + 45;
            } else if (digits == 2) {
                textX = x + 38;
            } else if (digits == 3) {
                textX = x + 32;
            } else {
                textX = x + 22;
            }
            int textY = y + 60;

            g.drawString(s, textX, textY);
        }
    }

    // helper to set colors to each number
    private Color tileColor(int value) {
        if (value == 0) {
            return new Color(0xCDC1B4);
        } else if (value == 2) {
            return new Color(0xEEE4DA);
        } else if (value == 4) {
            return new Color(0xEDE0C8);
        } else if (value == 8) {
            return new Color(0xf2b179);
        } else if (value == 16) {
            return new Color(0xf59563);
        } else if (value == 32) {
            return new Color(0xf67c5f);
        } else if (value == 64) {
            return new Color(0xf65e3b);
        } else if (value == 128) {
            return new Color(0xedcf72);
        } else if (value == 256) {
            return new Color(0xedcc61);
        } else if (value == 512) {
            return new Color(0xedc850);
        } else if (value == 1024) {
            return new Color(0xedc53f);
        } else if (value == 2048) {
            return new Color(0xedc22e);
        } else {
            return new Color(0x3C3A32);
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
