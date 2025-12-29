package org.cis1200.twentyfortyeight;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.JOptionPane;
import java.io.File;

public class TwentyFortyEight {
    private static final int SIZE = 4; //4x4 board
    // labels for directions, used when calling move (int)
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    private final String filepath = "save.txt"; //saveGame and loadGame rewrite this file

    private final int[][] board; // tile values
    private int score; // stores current score
    private int highestScore;
    private boolean gameOver; // is game over boolean
    private boolean won; // if user won when reached 2048
    private boolean getRandom = true; // for testing
    // last in, first out: stack
    private final Deque<GameState> history = new ArrayDeque<>(); //undo collection, game state stores copy of board

    // constructor
    public TwentyFortyEight() {
        board = new int[SIZE][SIZE];
        score = 0;
        gameOver = false;
        loadGame(filepath);
        if (isBoardEmpty()) {
            reset();
            history.clear();
        }
    }

    // get methods
    public int getCell(int r, int c) {
        return board[r][c];
    }

    public int getScore() {
        return score;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean hasWon() {
        return won;
    }

    // used for tests
    void cellForTest(int r, int c, int v) {
        board[r][c] = v;
    }

    void clearBoardTest() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }
        score = 0;
        won = false;
        gameOver = false;
        history.clear();
    }

    void randomTileTest(boolean tile) {
        this.getRandom = tile;
    }

    // methods
    /**
     * Make moves in the arrow directions
     *
     * @param direction to either LEFT/RIGHT/UP/DOWN
     * @return true if board changed, false ow
     */

    public boolean move(int direction) {
        pushUndoState(); // save state before undo

        boolean changed;
        if (direction == LEFT) {
            changed = moveLeft();
        } else if (direction == RIGHT) {
            changed = moveRight();
        } else if (direction == UP) {
            changed = moveUp();
        } else if (direction == DOWN) {
            changed = moveDown();
        } else {
            history.pop();
            return false;
        }

        if (!changed) {
            history.pop();
            return false;
        }
        if (getRandom) {
            randomTile();
        }

        won = has2048();
        gameOver = won || !canMove();
        saveGame(filepath);
        return true;
    }

    // used for undo (collections)
    private static class GameState {
        final int[][] boardCopy; // creates copy of board at moment
        final int scoreCopy; // creates copy of score at moment

        //constructor
        GameState(int[][] boardCopy, int scoreCopy) {
            this.boardCopy = boardCopy;
            this.scoreCopy = scoreCopy;
        }
    }

    public boolean pressUndo() {
        // check for previous moves
        if (history.isEmpty()) {
            return false;
        }
        // get previous state of program
        GameState previousState = history.pop(); // pop() removes and returns most recent gameState

        // copy old board into display
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(previousState.boardCopy[i], 0, board[i], 0, SIZE);
        }
        score = previousState.scoreCopy;
        won = has2048();
        gameOver = won || !canMove();

        saveGame(filepath);
        return true;
    }

    /**
     * Reset method: sets the score to 0 and the game becomes active (gameOver =
     * false)
     * sets all tiles to 0, generates two random tiles using randomTile() method
     */
    public void reset() {
        history.clear();
        won = false;
        score = 0; // set score to 0
        gameOver = false; // game is active
        for (int i = 0; i < SIZE; i++) { // iterate through rows
            for (int j = 0; j < SIZE; j++) { // iterate through columns
                board[i][j] = 0; // set everything to 0
            }
        }
        randomTile(); // generate random tile 1
        randomTile(); // generate random tile 2

        saveGame(filepath);
        history.clear();
    }

    // FILE I/O
    public void saveGame(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            // write highscore
            bw.write(Integer.toString(highestScore));
            bw.newLine();
            // write score
            bw.write(Integer.toString(score));
            bw.newLine();
            // write gameover state
            bw.write(Boolean.toString(gameOver));
            bw.newLine();
            // write board
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    bw.write(Integer.toString(board[r][c]));
                    if (c < SIZE - 1) {
                        bw.write(" ");
                    }
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Could not save: " + e.getMessage());
        }
    }

    public void loadGame(String fileName) {
        File f = new File(fileName);
        // check if file exists
        if (!f.exists()) {
            reset();
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {

            String line = br.readLine();
            if (line == null) {
                throw new IOException("Save file incomplete.");
            }
            // read highscore
            highestScore = Integer.parseInt(line.trim());

            line = br.readLine();
            if (line == null) {
                throw new IOException("Save file incomplete.");
            }
            // read score
            score = Integer.parseInt(line.trim());

            line = br.readLine();
            if (line == null) {
                throw new IOException("Save file incomplete.");
            }
            // read gameover state
            gameOver = Boolean.parseBoolean(line.trim());
            // read the board
            for (int r = 0; r < SIZE; r++) {
                line = br.readLine();
                if (line == null) {
                    return;
                }

                String[] parts = line.trim().split(" ");
                // if doesnt have 4 values, error
                if (parts.length != SIZE) {
                    throw new IOException("Bad save formatting.");
                }
                // rewrites board
                for (int c = 0; c < SIZE; c++) {
                    board[r][c] = Integer.parseInt(parts[c]);
                }
            }

            won = has2048();
            gameOver = won || !canMove();

            history.clear();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null, "Could not load saved file. Starting a new game...",
                    "Load Error", JOptionPane.ERROR_MESSAGE
            );
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not load saved file. Starting a new game...",
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
            reset();
        }
    }

    /**
     * Checks is there is another move left, if game continues
     *
     * @return true if game is not over (can still move), false if game over
     */
    private boolean canMove() {
        // iterate to check for empty cell
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                // if empty found, return true
                if (board[r][c] == 0) {
                    return true;
                }
            }
        }
        // check to see if there are cells to merge
        // can check only down and left because we iterate, avoid duplicates
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                // stores value to check
                int check = board[r][c];
                // if tile below has the same value, then true
                if (r + 1 < SIZE && board[r + 1][c] == check) {
                    return true;
                }
                // if right tile has the same value, then true
                if (c + 1 < SIZE && board[r][c + 1] == check) {
                    return true;
                }
            }
        }
        // no empties or merge, return false
        return false;
    }

    /**
     * checks if there is a tile with value 2048
     *
     * @return true if 2048 exists, false otherwise
     */
    private boolean has2048() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 2048) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates a new tile on an empty cell on board (90% of time is a 2, 10% is a 4)
     * If there are no empty cells left, it just returns
     */
    private void randomTile() {
        int empty = 0;
        // count number of empty cells
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    empty++;
                }
            }
        }
        // if there are no empty cells, return
        if (empty == 0) {
            return;
        }

        // randomly choose empty cell
        int target = (int) (Math.random() * empty);
        // iterate over board until we reach empty cell
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    if (target == 0) {
                        // 90% gives you a 2, 10% of time gives a 4 (2048 behavior)
                        if (Math.random() < 0.9) {
                            board[i][j] = 2;
                        } else {
                            board[i][j] = 4;
                        }
                        return;
                    }
                    target--; // decrease target by 1
                }
            }
        }
    }

    /**
     * Iterates through each row when the user presses the left key
     * Slides the tiles to the left
     * Merges tiles that are the same
     *
     * @param r
     * @return true if anything changed or merged, false if row stayed the same
     */
    private boolean rowLeft(int r) {
        int[] oldRow = new int[SIZE]; // save old row to compare for changes
        for (int c = 0; c < SIZE; c++) { // for each column in r
            oldRow[c] = board[r][c];
        }
        // slide left and remove empties
        int[] tempSlide = new int[SIZE]; // row values after sliding left, before merge
        int nextEmpty = 0; // index of next empty slot (leftmost)
        for (int c = 0; c < SIZE; c++) { // go through columns in row to find empty
            if (oldRow[c] != 0) { // if tile is not empty, place it in nextEmpty
                tempSlide[nextEmpty] = oldRow[c];
                nextEmpty++;
            }
        }
        // check for neighbors to merge
        for (int c = 0; c < SIZE - 1; c++) { // go through columns
            if (tempSlide[c] != 0 && tempSlide[c] == tempSlide[c + 1]) { // if tile is equal to left
                                                                         // tile
                tempSlide[c] *= 2; // add them
                score += tempSlide[c]; // add value to score

                if (score > highestScore) {
                    highestScore = score;
                }
                tempSlide[c + 1] = 0; // set right tile to 0
                c++; // skip next
            }
        }
        // slide tiles again because we may have new 0
        int[] newRow = new int[SIZE];
        nextEmpty = 0;
        for (int c = 0; c < SIZE; c++) {
            if (tempSlide[c] != 0) {
                newRow[nextEmpty] = tempSlide[c];
                nextEmpty++;
            }
        }
        // write new row into board
        boolean changed = false; // boolean to see if board changed
        for (int c = 0; c < SIZE; c++) {
            if (board[r][c] != newRow[c]) { // compare to see if row changed
                changed = true;
            }
            board[r][c] = newRow[c]; // update board with new row
        }
        return changed;
    }

    /**
     * Helper to reverse the row
     */
    private void reverseRow(int r) {
        for (int c = 0; c < SIZE / 2; c++) {
            int old = board[r][c];
            board[r][c] = board[r][SIZE - 1 - c];
            board[r][SIZE - 1 - c] = old;
        }
    }

    /**
     * Helper to iterate over columns, using row logic
     * columns become rows, rows become columns
     */

    private void switchColRow() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = r + 1; c < SIZE; c++) {
                int old = board[r][c]; // store old
                board[r][c] = board[c][r]; // flip row and column
                board[c][r] = old;
            }
        }
    }
    // create a deep copy, avoid pointing to same place in stack
    private int[][] deepCopyBoard() {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }
    //saves the current state into your undo history stack.
    private void pushUndoState() {
        if (isBoardEmpty()) {
            return; // avoids grid empty
        }
        history.push(new GameState(deepCopyBoard(), score));
    }

    private boolean isBoardEmpty() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * changes tile positions depending on direction of key press
     *
     * @return true if board changes
     */
    private boolean moveLeft() {
        boolean changed = false;
        for (int r = 0; r < SIZE; r++) {
            if (rowLeft(r)) {
                changed = true;
            }
        }
        return changed;
    }
    // reverse + LEFT + reverse back
    private boolean moveRight() {
        boolean changed = false;
        for (int r = 0; r < SIZE; r++) {
            reverseRow(r); // flip row
            if (rowLeft(r)) {
                changed = true; // use logic for left key press
            }
            reverseRow(r); // flip row back
        }
        return changed;
    }
    // switch row col + LEFT + switch back
    private boolean moveUp() {
        switchColRow(); // switch row and col
        boolean changed = moveLeft(); // use logic for left key press
        switchColRow(); // switch back
        return changed;
    }
    //switch row col + RIGHT + switch back
    private boolean moveDown() {
        switchColRow(); // switch row and col
        boolean changed = moveRight(); // use logic for right key press
        switchColRow(); // switch back
        return changed;
    }
}