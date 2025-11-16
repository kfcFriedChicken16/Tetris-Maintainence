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
}
