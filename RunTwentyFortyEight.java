package org.cis1200.twentyfortyeight;

import javax.swing.*;
import java.awt.*;

public class RunTwentyFortyEight implements Runnable {
    public void run() {
        // NOTE: the 'final' keyword denotes immutability even for local variables.

        // Top-level frame in which game components live
        final JFrame frame = new JFrame("2048");
        frame.setLocation(300, 300);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Score: 0");
        status_panel.add(status);

        // Reset button
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        final JButton instructions = new JButton("Instructions");
        // final JButton save = new JButton("Save");
        // final JButton load = new JButton("Load");

        final JButton score = new JButton("Score : 0");
        score.setEnabled(false);

        final JButton highestScore = new JButton("Highest Score: 0");
        highestScore.setEnabled(false);

        final JButton undo = new JButton("Undo");

        control_panel.add(reset);
        control_panel.add(instructions);
        // control_panel.add(save);
        // control_panel.add(load);
        control_panel.add(score);
        control_panel.add(highestScore);
        control_panel.add(undo);
        control_panel.setPreferredSize(new Dimension(420, control_panel.getPreferredSize().height));

        final GameBoard board = new GameBoard(status, score, highestScore);
        frame.add(board, BorderLayout.CENTER);

        // action listeners
        reset.addActionListener(e -> {
            board.reset();
            board.requestFocusInWindow();
        });
        instructions.addActionListener(e -> {
            board.instructions();
            board.requestFocusInWindow();
        });
        // save.addActionListener(e -> board.save());
        // load.addActionListener(e -> board.load());
        undo.addActionListener(e -> {
            board.undo();
            board.requestFocusInWindow(); //
        });

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start the game
        board.requestFocusInWindow();
    }
}
