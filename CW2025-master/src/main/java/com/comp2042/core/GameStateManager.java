package com.comp2042.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Manages game state (pause and game over).
 * This class simplifies state management by centralizing pause/game over logic.
 */
public class GameStateManager {
    
    private final BooleanProperty isPause;
    private final BooleanProperty isGameOver;
    
    public GameStateManager() {
        this.isPause = new SimpleBooleanProperty(false);
        this.isGameOver = new SimpleBooleanProperty(false);
    }
    
    /**
     * Get the pause property
     */
    public BooleanProperty isPauseProperty() {
        return isPause;
    }
    
    /**
     * Check if game is paused
     */
    public boolean isPaused() {
        return isPause.get();
    }
    
    /**
     * Set pause state
     */
    public void setPaused(boolean paused) {
        isPause.set(paused);
    }
    
    /**
     * Get the game over property
     */
    public BooleanProperty isGameOverProperty() {
        return isGameOver;
    }
    
    /**
     * Check if game is over
     */
    public boolean isGameOver() {
        return isGameOver.get();
    }
    
    /**
     * Set game over state
     */
    public void setGameOver(boolean gameOver) {
        isGameOver.set(gameOver);
    }
    
    /**
     * Reset all state for new game
     */
    public void reset() {
        isPause.set(false);
        isGameOver.set(false);
    }
}

