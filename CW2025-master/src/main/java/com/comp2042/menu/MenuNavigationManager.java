package com.comp2042.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import com.comp2042.modes.GameMode;
import com.comp2042.ui.GameViewController;
import com.comp2042.core.GameController;

import java.net.URL;

/**
 * Manages navigation between different menu screens and game launching.
 * Extracted from MenuController for better maintainability.
 */
public class MenuNavigationManager {
    
    private Stage primaryStage;
    private Button classicBtn; // Reference button for getting stage
    
    // Callback interface for media management
    public interface MediaCallback {
        void stopAllMedia();
    }
    
    private MediaCallback mediaCallback;
    
    public MenuNavigationManager(Stage primaryStage, Button classicBtn) {
        this.primaryStage = primaryStage;
        this.classicBtn = classicBtn;
    }
    
    public void setMediaCallback(MediaCallback callback) {
        this.mediaCallback = callback;
    }
    
    /**
     * Navigate to game with selected mode
     */
    public void startGame(GameMode mode) {
        try {
            // Stop background video and music
            if (mediaCallback != null) {
                mediaCallback.stopAllMedia();
            }

            // Load the game layout
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent gameRoot = fxmlLoader.load();
            
            // Get the GuiController and start the game
            GameViewController guiController = fxmlLoader.getController();
            
            // Get current stage and switch scene
            Stage stage = (Stage) classicBtn.getScene().getWindow();
            Scene gameScene = new Scene(gameRoot, 1200, 800);
            stage.setScene(gameScene);
            stage.setTitle("Tetris - " + mode.getDisplayName());
            
            // Lock in full screen mode - cannot be exited
            stage.setResizable(false); // Prevent window manipulation
            stage.setFullScreen(true);
            stage.setFullScreenExitKeyCombination(null);
            stage.setFullScreenExitHint("");
            
            // Add fullscreen enforcement listener
            enforceFullscreenMode(stage);
            
            // Initialize the game controller with selected mode
            new GameController(guiController, mode);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error starting game: " + e.getMessage());
        }
    }
    
    /**
     * Navigate to settings screen
     */
    public void openSettings() {
        try {
            // Stop background video and music
            if (mediaCallback != null) {
                mediaCallback.stopAllMedia();
            }
            
            // Load the settings layout
            URL location = getClass().getClassLoader().getResource("settings.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent settingsRoot = fxmlLoader.load();
            
            // Get current stage and switch scene
            Stage stage = (Stage) classicBtn.getScene().getWindow();
            Scene settingsScene = new Scene(settingsRoot, 1200, 800);
            stage.setScene(settingsScene);
            stage.setTitle("Tetris - Settings");
            
            // Maintain full screen mode
            stage.setFullScreen(true);
            stage.setResizable(false);
            stage.setFullScreenExitKeyCombination(null);
            stage.setFullScreenExitHint("");
            
            // Add fullscreen enforcement listener
            enforceFullscreenMode(stage);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error opening settings: " + e.getMessage());
            
            // Show error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot Open Settings");
            alert.setContentText("Failed to load settings screen: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    /**
     * Navigate to mode selection screen
     */
    public void openModeSelection() {
        try {
            // Stop background video and music
            if (mediaCallback != null) {
                mediaCallback.stopAllMedia();
            }
            
            // Load the mode selection layout
            URL location = getClass().getClassLoader().getResource("modeSelection.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent modeRoot = fxmlLoader.load();
            
            // Get current stage and switch scene
            Stage stage = (Stage) classicBtn.getScene().getWindow();
            Scene modeScene = new Scene(modeRoot, 1200, 800);
            stage.setScene(modeScene);
            stage.setTitle("Tetris - Mode Selection");
            
            // Maintain full screen mode
            stage.setFullScreen(true);
            stage.setResizable(false);
            stage.setFullScreenExitKeyCombination(null);
            stage.setFullScreenExitHint("");
            
            // Add fullscreen enforcement listener
            enforceFullscreenMode(stage);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error opening mode selection: " + e.getMessage());
            
            // Show error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot Open Mode Selection");
            alert.setContentText("Failed to load mode selection screen: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    /**
     * Exit the application
     */
    public void exitApplication() {
        // Stop background video and music
        if (mediaCallback != null) {
            mediaCallback.stopAllMedia();
        }
        
        // Close the application
        if (primaryStage != null) {
            primaryStage.close();
        } else {
            System.exit(0);
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
    
    // Game mode selection methods
    public void selectClassic(ActionEvent event) {
        startGame(GameMode.CLASSIC);
    }
    
    public void selectSprint(ActionEvent event) {
        startGame(GameMode.SPRINT);
    }
    
    public void selectUltra(ActionEvent event) {
        startGame(GameMode.ULTRA);
    }
    
    public void selectSurvival(ActionEvent event) {
        startGame(GameMode.SURVIVAL);
    }
    
    public void selectRPGMode(ActionEvent event) {
        startGame(GameMode.RPG);
    }
    
    public void openSettings(ActionEvent event) {
        openSettings();
    }
    
    public void exitApplication(ActionEvent event) {
        exitApplication();
    }
}
