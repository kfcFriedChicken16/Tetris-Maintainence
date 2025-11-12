package com.comp2042;

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
}
