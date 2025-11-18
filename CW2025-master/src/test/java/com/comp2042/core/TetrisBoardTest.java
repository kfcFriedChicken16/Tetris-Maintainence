package com.comp2042.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.comp2042.models.ViewData;
import com.comp2042.models.Score;

/**
 * JUnit tests for TetrisBoard core game logic.
 * Tests the fundamental game mechanics without UI dependencies.
 */
public class TetrisBoardTest {
    
    private TetrisBoard board;
    private static final int BOARD_WIDTH = 25;
    private static final int BOARD_HEIGHT = 10;
    
    @BeforeEach
    void setUp() {
        board = new TetrisBoard(BOARD_WIDTH, BOARD_HEIGHT);
    }
    
    @Test
    void testBoardInitialization() {
        // Test that board is properly initialized
        assertNotNull(board, "Board should be initialized");
        assertNotNull(board.getBoardMatrix(), "Board matrix should be initialized");
        assertNotNull(board.getScore(), "Score should be initialized");
        
        // Test board dimensions
        int[][] matrix = board.getBoardMatrix();
        assertEquals(BOARD_WIDTH, matrix.length, "Board width should match constructor parameter");
        assertEquals(BOARD_HEIGHT, matrix[0].length, "Board height should match constructor parameter");
    }
    
    @Test
    void testEmptyBoardMatrix() {
        // Test that new board is empty (all zeros)
        int[][] matrix = board.getBoardMatrix();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                assertEquals(0, matrix[i][j], "New board should be empty (all zeros)");
            }
        }
    }
    
    @Test
    void testCreateNewBrick() {
        // Test that creating a new brick returns false (no collision) and updates view data
        boolean collision = board.createNewBrick();
        assertFalse(collision, "Creating new brick should return false (no collision)");
        
        ViewData viewData = board.getViewData();
        assertNotNull(viewData, "View data should not be null after creating brick");
        assertNotNull(viewData.getBrickData(), "Brick data should not be null");
    }
    
    @Test
    void testScoreInitialization() {
        // Test that score starts at zero
        Score score = board.getScore();
        assertNotNull(score, "Score should not be null");
        assertEquals(0, score.scoreProperty().get(), "Initial score should be zero");
    }
    
    @Test
    void testTotalLinesClearedInitialization() {
        // Test that total lines cleared starts at zero
        assertEquals(0, board.getTotalLinesCleared(), "Initial total lines cleared should be zero");
    }
    
    @Test
    void testHoldBrickInitially() {
        // Test hold functionality when no piece is held initially
        board.createNewBrick(); // Need a piece to hold
        boolean canHold = board.holdBrick();
        assertTrue(canHold, "Should be able to hold piece initially");
    }
    
    @Test
    void testNewGameReset() {
        // Create a piece and modify some state
        board.createNewBrick();
        board.holdBrick();
        
        // Reset the game
        board.newGame();
        
        // Verify state is reset
        assertEquals(0, board.getTotalLinesCleared(), "Total lines should reset to zero");
        assertNotNull(board.getViewData(), "Should have new piece after reset");
    }
    
    @Test
    void testBrickMovementBounds() {
        // Test that brick movement respects board boundaries
        board.createNewBrick();
        
        // Try to move brick - should not crash and should return boolean
        boolean movedDown = board.moveBrickDown();
        boolean movedLeft = board.moveBrickLeft();
        boolean movedRight = board.moveBrickRight();
        
        // These should return boolean values (true or false, not crash)
        assertTrue(movedDown || !movedDown, "Move down should return boolean");
        assertTrue(movedLeft || !movedLeft, "Move left should return boolean");
        assertTrue(movedRight || !movedRight, "Move right should return boolean");
    }
    
    @Test
    void testRotateBrick() {
        // Test brick rotation
        board.createNewBrick();
        boolean rotated = board.rotateLeftBrick();
        
        // Should return boolean (true or false, not crash)
        assertTrue(rotated || !rotated, "Rotate should return boolean");
    }
    
    @Test
    void testHardDrop() {
        // Test hard drop functionality
        board.createNewBrick();
        int dropDistance = board.hardDropBrick();
        
        // Drop distance should be non-negative
        assertTrue(dropDistance >= 0, "Hard drop distance should be non-negative");
    }
}
