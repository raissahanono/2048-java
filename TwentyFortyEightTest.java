package org.cis1200.twentyfortyeight;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TwentyFortyEightTest {

    // tests

    /**
     * CHECKPOINT 1:
     * twentyfortyeight model
     */

    // helpers
    private int countNonZero(TwentyFortyEight tfe) {
        int count = 0;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (tfe.getCell(r, c) != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isPower(int v) {
        if (v <= 0) {
            return false;
        }
        while (v % 2 == 0) {
            v /= 2;
        }
        return v == 1;
    }

    // Valid board
    @Test
    public void testBoardAfterReset() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.reset();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int value = tfe.getCell(r, c);
                if (value == 0) {
                    assertEquals(0, value);
                } else {
                    assertTrue(isPower(value));
                }
            }
        }
    }

    // Reset places two tiles
    @Test
    public void testResetTiles() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.reset();
        assertEquals(2, countNonZero(tfe));
    }

    // Reset/start tiles are only 2 or 4
    @Test
    public void testResetCells() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.reset();

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int value = tfe.getCell(r, c);
                if (value != 0) {
                    if (value == 2) {
                        assertEquals(2, value);
                    } else {
                        assertEquals(4, value);
                    }
                }
            }
        }
    }

    // Score resets to 0
    @Test
    public void testResetCellsEmpty() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.move(TwentyFortyEight.LEFT);
        tfe.move(TwentyFortyEight.RIGHT);
        tfe.reset();
        assertEquals(0, tfe.getScore());
    }

    // game does not close after reset
    @Test
    public void testGameNotCloseReset() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.reset();
        assertFalse(tfe.isGameOver());
        assertFalse(tfe.hasWon());
    }

    /**
     * CHECKPOINT 2:
     * slide to left
     */

    // compresses the values [0,0,2,4] -> [2,4,0,0]
    @Test
    public void testCompressLeft() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        // row 0: [0,0,2,4]
        tfe.cellForTest(0, 0, 0);
        tfe.cellForTest(0, 1, 0);
        tfe.cellForTest(0, 2, 2);
        tfe.cellForTest(0, 3, 4);

        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        assertTrue(changed);

        assertEquals(2, tfe.getCell(0, 0));
        assertEquals(4, tfe.getCell(0, 1));
        assertEquals(0, tfe.getCell(0, 2));
        assertEquals(0, tfe.getCell(0, 3));
    }

    // does nothing if already compressed
    @Test
    public void testAlreadyCompressed() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        tfe.cellForTest(1, 0, 2);
        tfe.cellForTest(1, 1, 4);
        tfe.cellForTest(1, 2, 0);
        tfe.cellForTest(1, 3, 0);

        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        assertFalse(changed);
        assertEquals(2, tfe.getCell(1, 0));
        assertEquals(4, tfe.getCell(1, 1));
        assertEquals(0, tfe.getCell(1, 2));
        assertEquals(0, tfe.getCell(1, 3));

    }

    // slide left on empty stays empty
    @Test
    public void testCompressEmpty() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0, tfe.getCell(i, j));
            }
        }
    }

    // slide is independent for each row
    @Test
    public void testSlideIndependent() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        // row 1
        tfe.cellForTest(0, 2, 2);
        tfe.cellForTest(0, 3, 4);
        // row 2
        tfe.cellForTest(1, 1, 4);
        tfe.cellForTest(1, 3, 2);
        // row 3
        tfe.cellForTest(2, 0, 16);
        // row 4:
        tfe.cellForTest(3, 3, 8);

        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        assertTrue(changed);

        // check r1
        assertEquals(2, tfe.getCell(0, 0));
        assertEquals(4, tfe.getCell(0, 1));
        assertEquals(0, tfe.getCell(0, 2));
        assertEquals(0, tfe.getCell(0, 3));
        // check r2
        assertEquals(4, tfe.getCell(1, 0));
        assertEquals(2, tfe.getCell(1, 1));
        assertEquals(0, tfe.getCell(1, 2));
        assertEquals(0, tfe.getCell(1, 3));
        // check r3
        assertEquals(16, tfe.getCell(2, 0));
        assertEquals(0, tfe.getCell(2, 1));
        assertEquals(0, tfe.getCell(2, 2));
        assertEquals(0, tfe.getCell(2, 3));
        // check r4
        assertEquals(8, tfe.getCell(3, 0));
        assertEquals(0, tfe.getCell(3, 1));
        assertEquals(0, tfe.getCell(3, 2));
        assertEquals(0, tfe.getCell(3, 3));
    }

    /**
     * CHECKPOINT 3
     * merging the tiles
     */
    // Simple merge [2, 2, 0, 0] -> [4, 0, 0, 0]
    @Test
    public void testSimpleMerge() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        tfe.cellForTest(0, 0, 2);
        tfe.cellForTest(0, 1, 2);

        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        assertTrue(changed);

        assertEquals(4, tfe.getCell(0, 0));
        assertEquals(0, tfe.getCell(0, 1));
        assertEquals(0, tfe.getCell(0, 2));
        assertEquals(0, tfe.getCell(0, 3));
    }

    // merge and slide [0, 2, 0, 2] -> [4,0,0,0]
    @Test
    public void testMergeAndSlide() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        tfe.cellForTest(0, 0, 2);
        tfe.cellForTest(0, 1, 0);
        tfe.cellForTest(0, 2, 2);
        tfe.cellForTest(0, 3, 0);

        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        assertTrue(changed);
        assertEquals(4, tfe.getCell(0, 0));
        assertEquals(0, tfe.getCell(0, 1));
        assertEquals(0, tfe.getCell(0, 2));
        assertEquals(0, tfe.getCell(0, 3));
    }

    // different, no merge [4, 2, 4, 2]
    @Test
    public void testDifferentNoMerge() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        tfe.cellForTest(0, 0, 4);
        tfe.cellForTest(0, 1, 2);
        tfe.cellForTest(0, 2, 4);
        tfe.cellForTest(0, 3, 2);

        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        assertFalse(changed);
        assertEquals(4, tfe.getCell(0, 0));
        assertEquals(2, tfe.getCell(0, 1));
        assertEquals(4, tfe.getCell(0, 2));
        assertEquals(2, tfe.getCell(0, 3));
    }

    // merges to leftmost [2, 2, 2, 0] -> [4, 2, 0, 0]
    @Test
    public void testMergeLeftmost() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        tfe.cellForTest(0, 0, 2);
        tfe.cellForTest(0, 1, 2);
        tfe.cellForTest(0, 2, 2);
        tfe.cellForTest(0, 3, 0);

        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        assertTrue(changed);
        assertEquals(4, tfe.getCell(0, 0));
        assertEquals(2, tfe.getCell(0, 1));
        assertEquals(0, tfe.getCell(0, 2));
        assertEquals(0, tfe.getCell(0, 3));
    }

    // no doubles [2, 2, 4, 4] -> [4, 8, 0, 0]
    @Test
    public void testMergeDoubles() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);
        tfe.cellForTest(0, 0, 2);
        tfe.cellForTest(0, 1, 2);
        tfe.cellForTest(0, 2, 4);
        tfe.cellForTest(0, 3, 4);

        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        assertTrue(changed);
        assertEquals(4, tfe.getCell(0, 0));
        assertEquals(8, tfe.getCell(0, 1));
        assertEquals(0, tfe.getCell(0, 2));
        assertEquals(0, tfe.getCell(0, 3));
    }

    // score updates on merges
    @Test
    public void testScoreUpdate() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        assertEquals(0, tfe.getScore());
        tfe.cellForTest(0, 0, 2);
        tfe.cellForTest(0, 1, 2);
        tfe.move(TwentyFortyEight.LEFT);
        assertEquals(4, tfe.getScore());
    }

    /**
     * CHECKPOINT 4
     * moves in all directions
     */
    // check for up
    @Test
    public void testMoveUp() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        // columns: [0, 0, 2, 4] -> [2, 4, 0, 0]
        tfe.cellForTest(2, 2, 2);
        tfe.cellForTest(3, 2, 4);

        boolean changed = tfe.move(TwentyFortyEight.UP);
        assertTrue(changed);

        assertEquals(2, tfe.getCell(0, 2));
        assertEquals(4, tfe.getCell(1, 2));
        assertEquals(0, tfe.getCell(2, 2));
        assertEquals(0, tfe.getCell(3, 2));
    }

    // down
    @Test
    public void testMoveDown() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        // columns): [2, 4, 0, 0] -> [0, 0, 2, 4]
        tfe.cellForTest(0, 2, 2);
        tfe.cellForTest(1, 2, 4);

        boolean changed = tfe.move(TwentyFortyEight.DOWN);
        assertTrue(changed);

        assertEquals(0, tfe.getCell(0, 2));
        assertEquals(0, tfe.getCell(1, 2));
        assertEquals(2, tfe.getCell(2, 2));
        assertEquals(4, tfe.getCell(3, 2));
    }

    // right
    @Test
    public void testMoveRight() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        // row: [2, 4, 0, 0] -> [0, 0, 2, 4]
        tfe.cellForTest(1, 0, 2);
        tfe.cellForTest(1, 1, 4);

        boolean changed = tfe.move(TwentyFortyEight.RIGHT);
        assertTrue(changed);

        assertEquals(0, tfe.getCell(1, 0));
        assertEquals(0, tfe.getCell(1, 1));
        assertEquals(2, tfe.getCell(1, 2));
        assertEquals(4, tfe.getCell(1, 3));
    }

    /**
     * CHECKPOINT 5
     * adding a random tile after move
     */
    // helper
    private int countValue(TwentyFortyEight tfe, int value) {
        int count = 0;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (tfe.getCell(r, c) == value) {
                    count++;
                }
            }
        }
        return count;
    }

    // non-zero tiles +1
    @Test
    public void testAddOneRandom() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();

        // [0,0,0,2]
        tfe.cellForTest(0, 3, 2);
        int before = countNonZero(tfe);
        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        int after = countNonZero(tfe);

        assertTrue(changed);
        assertEquals(before + 1, after);
    }

    // value is always 2 or 4
    @Test
    public void testNewTile2or4() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();

        tfe.cellForTest(0, 3, 2);
        int twoBefore = countValue(tfe, 2);
        int fourBefore = countValue(tfe, 4);

        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        assertTrue(changed);

        int twoAfter = countValue(tfe, 2);
        int fourAfter = countValue(tfe, 4);

        assertEquals(1, (twoAfter - twoBefore) + (fourAfter - fourBefore));
    }

    // if row is full, doesnt add any tiles
    @Test
    public void testFullDoesNotAddTile() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        tfe.cellForTest(0, 0, 2);
        tfe.cellForTest(0, 1, 4);

        int before = countNonZero(tfe);
        boolean changed = tfe.move(TwentyFortyEight.LEFT);
        int after = countNonZero(tfe);

        assertFalse(changed);
        assertEquals(before, after);
    }

    /**
     * Checkpoint 6
     * game logic
     */
    // helper
    private void fillRow(TwentyFortyEight tfe, int r, int[] vals) {
        for (int c = 0; c < 4; c++) {
            tfe.cellForTest(r, c, vals[c]);
        }
    }

    // hasWon true when 2048
    @Test
    public void testHasWonTrue() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        tfe.cellForTest(0, 0, 2048);
        tfe.cellForTest(1, 3, 2);
        assertTrue(tfe.move(TwentyFortyEight.LEFT));
        assertTrue(tfe.hasWon());
    }

    // hasWon false when no 2048
    @Test
    public void testHasWonFalse() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        tfe.cellForTest(1, 3, 2);
        assertTrue(tfe.move(TwentyFortyEight.LEFT));
        assertFalse(tfe.hasWon());
    }

    // canMove true if empty cell exists
    @Test
    public void testCanMoveTrue() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        tfe.cellForTest(0, 3, 2);
        assertTrue(tfe.move(TwentyFortyEight.LEFT));
        assertFalse(tfe.isGameOver());
    }

    // canMove true if board is full, but there is a merge
    @Test
    public void testCanMoveFull() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        fillRow(tfe, 0, new int[] { 2, 2, 4, 8 });
        fillRow(tfe, 1, new int[] { 16, 32, 64, 128 });
        fillRow(tfe, 2, new int[] { 2, 4, 8, 16 });
        fillRow(tfe, 3, new int[] { 32, 64, 128, 256 });

        assertTrue(tfe.move(TwentyFortyEight.LEFT));
        assertFalse(tfe.isGameOver());
    }

    // canMove false with no merges available
    @Test
    public void testCanMoveFalse() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        fillRow(tfe, 0, new int[] { 2, 4, 2, 4 });
        fillRow(tfe, 1, new int[] { 4, 2, 4, 2 });
        fillRow(tfe, 2, new int[] { 2, 4, 2, 4 });
        fillRow(tfe, 3, new int[] { 4, 2, 4, 2 });

        assertFalse(tfe.move(TwentyFortyEight.LEFT));
        assertFalse(tfe.move(TwentyFortyEight.RIGHT));
        assertFalse(tfe.move(TwentyFortyEight.UP));
        assertFalse(tfe.move(TwentyFortyEight.DOWN));
    }

    /**
     * CHECKPOINT 7
     * implementing undo
     */
    // undo after first move, starting board
    @Test
    public void testUndoAfterFirstMove() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);
        // starting board
        tfe.cellForTest(0, 3, 2);
        tfe.cellForTest(1, 3, 4);
        // save starting
        int start00 = tfe.getCell(0, 0), start03 = tfe.getCell(0, 3);
        int start13 = tfe.getCell(1, 3);
        int startScore = tfe.getScore();
        // make move and undo
        assertTrue(tfe.move(TwentyFortyEight.LEFT));
        assertTrue(tfe.pressUndo());
        // check with original
        assertEquals(start00, tfe.getCell(0, 0));
        assertEquals(start03, tfe.getCell(0, 3));
        assertEquals(start13, tfe.getCell(1, 3));

        assertEquals(startScore, tfe.getScore());
    }

    // multiple undos
    @Test
    public void testMultipleUndo() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        // starting board
        tfe.cellForTest(0, 3, 2);
        assertTrue(tfe.move(TwentyFortyEight.LEFT)); // Move 1
        assertTrue(tfe.move(TwentyFortyEight.RIGHT)); // Move 2

        // Undo twice
        assertTrue(tfe.pressUndo());
        assertTrue(tfe.pressUndo());

        // check with start
        assertEquals(2, tfe.getCell(0, 3));
        assertEquals(0, tfe.getCell(0, 0));
    }

    // undo when history empty returns false, still playable
    @Test
    public void testUndoStillPlayable() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        tfe.cellForTest(0, 3, 2);

        // no moves, history should be empty
        assertFalse(tfe.pressUndo());

        // Should still be able to move
        assertTrue(tfe.move(TwentyFortyEight.LEFT));
    }

    // undo before any move, can still play
    @Test
    public void testUndoBeforeMoves() {
        TwentyFortyEight tfe = new TwentyFortyEight();
        tfe.clearBoardTest();
        tfe.randomTileTest(false);

        // Start board
        tfe.cellForTest(1, 3, 2);

        // Undo many times before any move
        assertFalse(tfe.pressUndo());
        assertFalse(tfe.pressUndo());
        assertFalse(tfe.pressUndo());

        // Still can move
        assertTrue(tfe.move(TwentyFortyEight.LEFT));
    }
}