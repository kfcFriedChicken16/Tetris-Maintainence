package com.comp2042.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventType;
import com.comp2042.events.EventSource;
import com.comp2042.modes.GameMode;

/**
 * Manages all game animations and timeline operations.
 * Extracted from GameViewController for better maintainability.
 */
public class GameAnimationManager {
    
    private Timeline timeLine;
    
    // Callback interface for move down action
    public interface MoveDownCallback {
        void moveDown(MoveEvent event);
    }
    
    private MoveDownCallback moveDownCallback;
    
    public GameAnimationManager() {
    }
    
    public void setMoveDownCallback(MoveDownCallback callback) {
        this.moveDownCallback = callback;
    }
    
    /**
     * Get the initial game speed (in milliseconds) based on current game mode
     */
    public long getInitialSpeedForMode(GameMode currentGameMode, long currentSpeedInterval, long survivalSpeedInterval) {
        if (currentGameMode == GameMode.SPRINT) {
            return 400; // Sprint mode: fixed 400ms
        } else if (currentGameMode == GameMode.ULTRA) {
            return currentSpeedInterval; // Ultra mode: starts at 400ms
        } else if (currentGameMode == GameMode.SURVIVAL) {
            return survivalSpeedInterval; // Survival mode: starts at 400ms, increases with score
        } else if (currentGameMode == GameMode.RPG) {
            return 400; // RPG mode: starts at 400ms, increases with level
        }
        return 400; // Default for Classic mode
    }
    
    /**
     * Create and start the game timeline with the specified speed
     */
    public void createAndStartGameTimeline(long speed) {
        if (timeLine != null) {
            timeLine.stop();
        }
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(speed),
                ae -> moveDownCallback.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }
    
    /**
     * Stop the game timeline
     */
    public void stopTimeline() {
        if (timeLine != null) {
            timeLine.stop();
        }
    }
    
    /**
     * Pause the game timeline
     */
    public void pauseTimeline() {
        if (timeLine != null) {
            timeLine.pause();
        }
    }
    
    /**
     * Resume the game timeline
     */
    public void playTimeline() {
        if (timeLine != null) {
            timeLine.play();
        }
    }
    
    /**
     * Check if timeline exists
     */
    public boolean hasTimeline() {
        return timeLine != null;
    }
}
