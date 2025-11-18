package com.comp2042.ui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventType;
import com.comp2042.events.EventSource;
import com.comp2042.managers.SettingsManager;
import com.comp2042.models.ViewData;
import com.comp2042.core.GameStateManager;

/**
 * Handles all keyboard input processing for the game.
 * Extracted from GameViewController for better maintainability.
 */
public class GameInputHandler {
    
    private InputEventListener eventListener;
    private GameStateManager gameStateManager;
    
    // Callback interfaces for actions that need to be performed
    public interface GameActionCallback {
        void newGame();
        void backToMenu();
        void togglePause();
        void moveDown(MoveEvent event);
        void hardDrop(MoveEvent event);
        ViewData refreshBrick(ViewData viewData);
    }
    
    private GameActionCallback gameActionCallback;
    
    public GameInputHandler(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
    }
    
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }
    
    public void setGameActionCallback(GameActionCallback callback) {
        this.gameActionCallback = callback;
    }
    
    /**
     * Handle key press events for game controls
     */
    public void handleKeyPressed(KeyEvent keyEvent) {
        SettingsManager settings = SettingsManager.getInstance();
        KeyCode keyCode = keyEvent.getCode();
        
        // Game over controls - check FIRST before normal game controls
        if (gameStateManager.isGameOver()) {
            if (keyCode == KeyCode.SPACE) {
                // Press Space to restart game
                gameActionCallback.newGame();
                keyEvent.consume();
                return;
            } else if (keyCode == settings.getPause()) {
                // Press pause key to return to main menu
                gameActionCallback.backToMenu();
                keyEvent.consume();
                return;
            }
        }
        
        // Pause key toggles pause (only when not game over)
        if (keyCode == settings.getPause() && !gameStateManager.isGameOver()) {
            gameActionCallback.togglePause();
            keyEvent.consume();
            return;
        }
        
        // Normal game controls (only when not paused and not game over)
        if (!gameStateManager.isPaused() && !gameStateManager.isGameOver()) {
            // Move left
            if (keyCode == settings.getMoveLeft() || keyCode == settings.getMoveLeftAlt()) {
                gameActionCallback.refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                keyEvent.consume();
            }
            // Move right
            if (keyCode == settings.getMoveRight() || keyCode == settings.getMoveRightAlt()) {
                gameActionCallback.refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                keyEvent.consume();
            }
            // Rotate
            if (keyCode == settings.getRotate() || keyCode == settings.getRotateAlt()) {
                gameActionCallback.refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                keyEvent.consume();
            }
            // Move down
            if (keyCode == settings.getMoveDown() || keyCode == settings.getMoveDownAlt()) {
                gameActionCallback.moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                keyEvent.consume();
            }
            // Hard drop
            if (keyCode == settings.getHardDrop()) {
                gameActionCallback.hardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
                keyEvent.consume();
            }
            // Hold piece
            if (keyCode == settings.getHold() || keyCode == settings.getHoldAlt()) {
                ViewData newViewData = eventListener.onHoldEvent();
                gameActionCallback.refreshBrick(newViewData);
                keyEvent.consume();
            }
        }
        
        // Restart key (for quick restart during gameplay)
        if (keyCode == settings.getRestart() && !gameStateManager.isGameOver()) {
            gameActionCallback.newGame();
            keyEvent.consume();
        }
    }
}
