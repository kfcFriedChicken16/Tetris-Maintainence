package com.comp2042.menu;

import javafx.animation.ParallelTransition;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Manages menu state transitions and UI visibility.
 * Extracted from MenuController for better maintainability.
 */
public class MenuStateManager {
    
    private javafx.scene.control.Label instructionLabel;
    private javafx.scene.layout.VBox menuButtonsContainer;
    private javafx.scene.control.Label titleLabel;
    private javafx.scene.layout.VBox modeButtonsContainer;
    
    // Callback interfaces
    public interface AnimationCallback {
        void stopInstructionBlink();
        ParallelTransition createMenuSlideInAnimation(javafx.scene.layout.VBox container);
        ParallelTransition createModeSlideInAnimation(javafx.scene.layout.VBox container);
    }
    
    private AnimationCallback animationCallback;
    
    public MenuStateManager(javafx.scene.control.Label instructionLabel,
                           javafx.scene.layout.VBox menuButtonsContainer,
                           javafx.scene.control.Label titleLabel,
                           javafx.scene.layout.VBox modeButtonsContainer) {
        this.instructionLabel = instructionLabel;
        this.menuButtonsContainer = menuButtonsContainer;
        this.titleLabel = titleLabel;
        this.modeButtonsContainer = modeButtonsContainer;
    }
    
    public void setAnimationCallback(AnimationCallback callback) {
        this.animationCallback = callback;
    }
    
    /**
     * Setup initial menu state - show title and instruction, hide menu options
     */
    public void setupInitialMenuState() {
        if (instructionLabel != null) {
            instructionLabel.setVisible(true);
            instructionLabel.setManaged(true);
        }
        if (menuButtonsContainer != null) {
            menuButtonsContainer.setVisible(false);
            menuButtonsContainer.setManaged(false);
        }
        if (modeButtonsContainer != null) {
            modeButtonsContainer.setVisible(false);
            modeButtonsContainer.setManaged(false);
        }
    }
    
    /**
     * Setup click and keyboard handlers for starting menu
     */
    public void setupInteractionHandlers() {
        // Add click handler to the entire scene (when it becomes available)
        // This will be set up in the main controller
        
        // Add keyboard handler for SPACE key
        // This will be set up in the main controller
    }
    
    /**
     * Handle click to start menu (show main menu buttons)
     */
    public void handleStartMenuClick() {
        System.out.println("=== Starting Menu Transition ===");
        
        // Stop instruction blinking
        if (animationCallback != null) {
            animationCallback.stopInstructionBlink();
        }
        
        // Hide instruction label
        if (instructionLabel != null) {
            instructionLabel.setVisible(false);
            instructionLabel.setManaged(false);
        }
        
        // Show menu buttons container
        if (menuButtonsContainer != null) {
            menuButtonsContainer.setVisible(true);
            menuButtonsContainer.setManaged(true);
            
            // Create and play slide-in animation
            if (animationCallback != null) {
                ParallelTransition slideInAnimation = animationCallback.createMenuSlideInAnimation(menuButtonsContainer);
                if (slideInAnimation != null) {
                    slideInAnimation.play();
                }
            }
        }
        
        System.out.println("✓ Menu transition complete");
    }
    
    /**
     * Handle single player button click (show mode selection)
     */
    public void handleSinglePlayerClick() {
        System.out.println("=== Single Player Selected ===");
        
        // Hide main menu buttons
        if (menuButtonsContainer != null) {
            menuButtonsContainer.setVisible(false);
            menuButtonsContainer.setManaged(false);
        }
        
        // Change title to "SELECT MODE"
        if (titleLabel != null) {
            titleLabel.setText("SELECT MODE");
            titleLabel.setVisible(true);
            titleLabel.setManaged(true);
        }
        
        // Show mode selection buttons
        if (modeButtonsContainer != null) {
            modeButtonsContainer.setVisible(true);
            modeButtonsContainer.setManaged(true);
            
            // Create and play slide-in animation
            if (animationCallback != null) {
                ParallelTransition slideInAnimation = animationCallback.createModeSlideInAnimation(modeButtonsContainer);
                if (slideInAnimation != null) {
                    slideInAnimation.play();
                }
            }
        }
        
        System.out.println("✓ Mode selection displayed");
    }
    
    /**
     * Handle back to menu button click
     */
    public void handleBackToMenuClick() {
        System.out.println("=== Back to Main Menu ===");
        
        // Hide mode selection buttons
        if (modeButtonsContainer != null) {
            modeButtonsContainer.setVisible(false);
            modeButtonsContainer.setManaged(false);
        }
        
        // Change title back to original
        if (titleLabel != null) {
            titleLabel.setText("TETRIS");
            titleLabel.setVisible(true);
            titleLabel.setManaged(true);
        }
        
        // Show main menu buttons
        if (menuButtonsContainer != null) {
            menuButtonsContainer.setVisible(true);
            menuButtonsContainer.setManaged(true);
        }
        
        System.out.println("✓ Returned to main menu");
    }
    
    /**
     * Handle keyboard input for menu navigation
     */
    public void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case SPACE:
            case ENTER:
                // If instruction is visible, start menu
                if (instructionLabel != null && instructionLabel.isVisible()) {
                    handleStartMenuClick();
                    event.consume();
                }
                break;
            case ESCAPE:
                // If mode selection is visible, go back to main menu
                if (modeButtonsContainer != null && modeButtonsContainer.isVisible()) {
                    handleBackToMenuClick();
                    event.consume();
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * Handle mouse click for menu navigation
     */
    public void handleMouseClicked(MouseEvent event) {
        // If instruction is visible, start menu
        if (instructionLabel != null && instructionLabel.isVisible()) {
            handleStartMenuClick();
            event.consume();
        }
    }
    
    /**
     * Check if we're in the initial state (showing instruction)
     */
    public boolean isInInitialState() {
        return instructionLabel != null && instructionLabel.isVisible();
    }
    
    /**
     * Check if we're showing main menu
     */
    public boolean isShowingMainMenu() {
        return menuButtonsContainer != null && menuButtonsContainer.isVisible();
    }
    
    /**
     * Check if we're showing mode selection
     */
    public boolean isShowingModeSelection() {
        return modeButtonsContainer != null && modeButtonsContainer.isVisible();
    }
}
