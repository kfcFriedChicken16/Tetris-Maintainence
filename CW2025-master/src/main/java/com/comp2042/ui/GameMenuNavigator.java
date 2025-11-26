package com.comp2042.ui;

import com.comp2042.managers.AudioManager;
import com.comp2042.managers.VideoManager;
import com.comp2042.menu.MenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Handles navigation back to main menu from game.
 * Extracted from GameViewController for better maintainability.
 */
public class GameMenuNavigator {
    
    private AudioManager audioManager;
    private VideoManager videoManager;
    
    public GameMenuNavigator(AudioManager audioManager, VideoManager videoManager) {
        this.audioManager = audioManager;
        this.videoManager = videoManager;
    }
    
    /**
     * Handle Back to Menu button click - Return to the main menu
     * Can be called from button click or keyboard (actionEvent may be null)
     */
    public void backToMenu(Stage stage) {
        try {
            // Load the main menu
            URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent menuRoot = fxmlLoader.load();
            
            // Get the menu controller
            MenuController menuController = fxmlLoader.getController();
            
            if (stage == null) {
                System.out.println("✗ Cannot get stage - cannot return to menu");
                return;
            }
            Scene menuScene = new Scene(menuRoot, 1200, 800); // Larger size for full screen
            stage.setScene(menuScene);
            stage.setTitle("Tetris - Enhanced Edition");
            
            // Maintain full screen mode
            stage.setFullScreen(true);
            stage.setResizable(false); // Prevent window manipulation
            stage.setFullScreenExitKeyCombination(null);
            stage.setFullScreenExitHint("");
            
            // Add fullscreen enforcement listener
            enforceFullscreenMode(stage);
            
            // Stop background video
            if (videoManager != null) {
                videoManager.dispose();
            }
            
            // Stop and dispose audio resources
            if (audioManager != null) {
                audioManager.dispose();
            }
            
            // Set the primary stage reference for the menu controller
            menuController.setPrimaryStage(stage);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error returning to menu: " + e.getMessage());
        }
    }
    
    /**
     * Enforce fullscreen mode with listener to prevent exits
     */
    private void enforceFullscreenMode(Stage stage) {
        if (stage != null) {
            // Add a listener to prevent any attempts to exit fullscreen
            stage.fullScreenProperty().addListener((obs, wasFullScreen, isNowFullScreen) -> {
                if (!isNowFullScreen) {
                    // If someone tries to exit fullscreen, immediately re-enable it
                    stage.setFullScreen(true);
                    stage.setFullScreenExitKeyCombination(null);
                    stage.setFullScreenExitHint("");
                }
            });
        }
    }
}

