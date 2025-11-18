package com.comp2042.menu;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 * Manages all menu animations and visual effects.
 * Extracted from MenuController for better maintainability.
 */
public class MenuAnimationManager {
    
    private javafx.scene.control.Label instructionLabel;
    private FadeTransition instructionBlink;
    
    public MenuAnimationManager(javafx.scene.control.Label instructionLabel) {
        this.instructionLabel = instructionLabel;
    }
    
    /**
     * Setup blinking animation for instruction text
     */
    public void setupInstructionBlink() {
        if (instructionLabel != null) {
            instructionBlink = new FadeTransition(Duration.seconds(1.5), instructionLabel);
            instructionBlink.setFromValue(1.0);
            instructionBlink.setToValue(0.3);
            instructionBlink.setCycleCount(Animation.INDEFINITE);
            instructionBlink.setAutoReverse(true);
            instructionBlink.play();
            System.out.println("✓ Instruction blink animation started");
        }
    }
    
    /**
     * Stop instruction blinking animation
     */
    public void stopInstructionBlink() {
        if (instructionBlink != null) {
            instructionBlink.stop();
            if (instructionLabel != null) {
                instructionLabel.setOpacity(1.0); // Ensure full opacity
            }
        }
    }
    
    /**
     * Add hover animations to buttons
     */
    public void addButtonAnimations() {
        // This method would add hover effects to buttons
        // Implementation would be added here if needed
    }
    
    /**
     * Create slide-in animation for menu buttons
     */
    public ParallelTransition createMenuSlideInAnimation(javafx.scene.layout.VBox menuButtonsContainer) {
        if (menuButtonsContainer == null) return null;
        
        // Create slide-in animation for menu buttons
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(800), menuButtonsContainer);
        slideIn.setFromY(-100); // Start above the screen
        slideIn.setToY(0); // End at normal position
        
        // Create fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), menuButtonsContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Combine animations
        ParallelTransition slideInAnimation = new ParallelTransition(slideIn, fadeIn);
        return slideInAnimation;
    }
    
    /**
     * Create slide-in animation for mode selection buttons
     */
    public ParallelTransition createModeSlideInAnimation(javafx.scene.layout.VBox modeButtonsContainer) {
        if (modeButtonsContainer == null) return null;
        
        // Create slide-in animation for mode buttons
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), modeButtonsContainer);
        slideIn.setFromX(100); // Start from right side
        slideIn.setToX(0); // End at normal position
        
        // Create fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), modeButtonsContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Combine animations
        ParallelTransition slideInAnimation = new ParallelTransition(slideIn, fadeIn);
        return slideInAnimation;
    }
    
    /**
     * Create button hover effect
     */
    public void setupButtonHoverEffect(Button button) {
        if (button == null) return;
        
        button.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);
            scaleUp.play();
        });
        
        button.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
        });
    }
}
