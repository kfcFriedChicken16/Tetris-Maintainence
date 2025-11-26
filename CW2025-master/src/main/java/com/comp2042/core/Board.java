package com.comp2042.core;

import com.comp2042.models.ViewData;
import com.comp2042.models.ClearRow;
import com.comp2042.models.Score;

/**
 * Interface defining the core game board operations for Tetris.
 * Provides methods for piece movement, rotation, line clearing, and game state management.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public interface Board {

    /**
     * Moves the current piece down one row.
     * 
     * @return true if the piece was successfully moved down, false if it cannot move further
     */
    boolean moveBrickDown();

    /**
     * Moves the current piece one column to the left.
     * 
     * @return true if the piece was successfully moved left, false if blocked
     */
    boolean moveBrickLeft();

    /**
     * Moves the current piece one column to the right.
     * 
     * @return true if the piece was successfully moved right, false if blocked
     */
    boolean moveBrickRight();

    /**
     * Rotates the current piece counter-clockwise (left rotation).
     * 
     * @return true if the rotation was successful, false if blocked
     */
    boolean rotateLeftBrick();

    /**
     * Instantly drops the current piece to the bottom of the board.
     * 
     * @return The number of rows the piece was dropped
     */
    int hardDropBrick();

    /**
     * Creates a new piece at the top of the board.
     * 
     * @return true if the new piece cannot be placed (game over), false otherwise
     */
    boolean createNewBrick();

    /**
     * Gets the current game board matrix.
     * 
     * @return A 2D array representing the board state where 0 is empty and other values represent block colors
     */
    int[][] getBoardMatrix();

    /**
     * Gets the current view data including the active piece, next piece, and board state.
     * 
     * @return ViewData object containing all information needed to render the game view
     */
    ViewData getViewData();

    /**
     * Merges the current piece into the background board matrix.
     * Called when a piece can no longer move down.
     */
    void mergeBrickToBackground();

    /**
     * Clears completed rows and returns information about the cleared lines.
     * 
     * @return ClearRow object containing the number of lines removed and score bonus
     */
    ClearRow clearRows();

    /**
     * Gets the score object for tracking game score.
     * 
     * @return The Score object associated with this board
     */
    Score getScore();

    /**
     * Resets the game board to start a new game.
     * Clears the board matrix and resets the score.
     */
    void newGame();
    
    /**
     * Hold the current piece and swap with held piece (if any)
     * @return true if hold was successful, false if hold is not allowed (already held this turn)
     */
    boolean holdBrick();
    
    /**
     * Get the total number of lines cleared (useful for Sprint mode)
     * @return total lines cleared since game start
     */
    int getTotalLinesCleared();
    
    /**
     * Clear all blocks that match the given color value and collapse columns.
     * @param colorValue tile value to remove
     * @return number of blocks removed
     */
    int clearColorBlocks(int colorValue);
    
    /**
     * Convert all existing blocks to the specified color.
     * @param colorValue color index to set
     * @return number of blocks that were changed
     */
    int convertAllBlocksToColor(int colorValue);
    
    /**
     * Spawn random garbage blocks at the bottom of the board.
     * Used in RPG mode to increase difficulty as levels progress.
     * @param numBlocks number of garbage blocks to spawn
     * @param level current RPG level (affects spawn pattern)
     * @return number of blocks actually spawned
     */
    int spawnGarbageBlocks(int numBlocks, int level);
}
