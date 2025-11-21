package com.comp2042.core;

import com.comp2042.models.ViewData;
import com.comp2042.models.ClearRow;
import com.comp2042.models.Score;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    int hardDropBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

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
