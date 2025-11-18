package com.comp2042.menu;

import javafx.animation.ParallelTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the main menu screen.
 * Handles navigation between different game modes and menu options.
 */
public class MenuController implements Initializable {

    @FXML private Button singlePlayerBtn;
    @FXML private Button settingsBtn;
    @FXML private Button exitBtn;
    @FXML private javafx.scene.control.Label instructionLabel;
    @FXML private javafx.scene.layout.VBox menuButtonsContainer;
    @FXML private javafx.scene.control.Label titleLabel;
    @FXML private MediaView backgroundVideo;
    
    // Mode selection buttons
    @FXML private javafx.scene.layout.VBox modeButtonsContainer;
    @FXML private Button classicBtn;
    @FXML private Button sprintBtn;
    @FXML private Button ultraBtn;
    @FXML private Button survivalBtn;
    @FXML private Button backToMenuBtn;

    private Stage primaryStage;

    // Extracted components
    private MenuAnimationManager animationManager;
    private MenuMediaManager mediaManager;
    private MenuNavigationManager navigationManager;
    private MenuStateManager stateManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== MenuController initializing ===");
        
        // Check if MediaView is properly injected
        if (backgroundVideo != null) {
            System.out.println("✓ MediaView found and ready");
        } else {
            System.out.println("✗ ERROR: MediaView is null - check FXML fx:id");
        }
        
        // Initialize extracted components
        animationManager = new MenuAnimationManager(instructionLabel);
        
        mediaManager = new MenuMediaManager(backgroundVideo);
        
        // Initialize navigation manager with null stage initially - will be set later via setPrimaryStage()
        navigationManager = new MenuNavigationManager(null, classicBtn);
        navigationManager.setMediaCallback(new MenuNavigationManager.MediaCallback() {
            @Override
            public void stopAllMedia() {
                mediaManager.stopAllMedia();
            }
        });
        
        stateManager = new MenuStateManager(instructionLabel, menuButtonsContainer, titleLabel, 
                                          modeButtonsContainer);
        stateManager.setAnimationCallback(new MenuStateManager.AnimationCallback() {
            @Override
            public void stopInstructionBlink() {
                animationManager.stopInstructionBlink();
            }
            
            @Override
            public ParallelTransition createMenuSlideInAnimation(javafx.scene.layout.VBox container) {
                return animationManager.createMenuSlideInAnimation(container);
            }
            
            @Override
            public ParallelTransition createModeSlideInAnimation(javafx.scene.layout.VBox container) {
                return animationManager.createModeSlideInAnimation(container);
            }
        });
        
        // List all files in audio directory for debugging
        mediaManager.listAudioFiles();
        
        // Initialize background video
        System.out.println("--- Initializing Video ---");
        mediaManager.initializeBackgroundVideo();
        
        // Initialize background music
        System.out.println("--- Initializing Music ---");
        mediaManager.initializeBackgroundMusic();
        
        // Setup initial menu state - show title and instruction, hide menu buttons
        stateManager.setupInitialMenuState();
        
        // Setup click and keyboard handlers
        setupInteractionHandlers();
        
        // Setup blinking animation for instruction text
        animationManager.setupInstructionBlink();
        
        // Add button animations (for when menu appears)
        animationManager.addButtonAnimations();
        
        System.out.println("=== MenuController initialization complete ===");
    }
    
    /**
     * Setup click and keyboard handlers for starting menu
     */
    private void setupInteractionHandlers() {
        // Add click handler to the entire scene (when it becomes available)
        if (instructionLabel != null && instructionLabel.getScene() != null) {
            instructionLabel.getScene().setOnMouseClicked(this::handleMouseClicked);
            instructionLabel.getScene().setOnKeyPressed(this::handleKeyPressed);
            
            // Ensure the scene can receive focus for keyboard events
            instructionLabel.getScene().getRoot().setFocusTraversable(true);
            instructionLabel.getScene().getRoot().requestFocus();
        } else {
            // Scene not available yet, defer setup using Platform.runLater
            javafx.application.Platform.runLater(() -> {
                if (instructionLabel != null && instructionLabel.getScene() != null) {
                    instructionLabel.getScene().setOnMouseClicked(this::handleMouseClicked);
                    instructionLabel.getScene().setOnKeyPressed(this::handleKeyPressed);
                    instructionLabel.getScene().getRoot().setFocusTraversable(true);
                    instructionLabel.getScene().getRoot().requestFocus();
                }
            });
        }
    }
    
    /**
     * Handle mouse click events
     */
    private void handleMouseClicked(MouseEvent event) {
        stateManager.handleMouseClicked(event);
    }
    
    /**
     * Handle keyboard events
     */
    private void handleKeyPressed(KeyEvent event) {
        stateManager.handleKeyPressed(event);
    }
    
    /**
     * Set the primary stage reference (called from Main or other controllers)
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        // Update navigation manager with new stage reference
        navigationManager = new MenuNavigationManager(primaryStage, classicBtn);
        navigationManager.setMediaCallback(new MenuNavigationManager.MediaCallback() {
            @Override
            public void stopAllMedia() {
                mediaManager.stopAllMedia();
            }
        });
    }

    /**
     * Handle Single Player button click - Show mode selection
     */
    @FXML
    private void selectSinglePlayer(ActionEvent event) {
        stateManager.handleSinglePlayerClick();
    }

    /**
     * Handle Back to Menu button click - Return to main menu
     */
    @FXML
    private void backToMainMenu(ActionEvent event) {
        stateManager.handleBackToMenuClick();
    }
    
    @FXML
    private void selectClassic(ActionEvent event) {
        navigationManager.selectClassic(event);
    }
    
    @FXML
    private void selectSprint(ActionEvent event) {
        navigationManager.selectSprint(event);
    }
    
    @FXML
    private void selectUltra(ActionEvent event) {
        navigationManager.selectUltra(event);
    }
    
    @FXML
    private void selectSurvival(ActionEvent event) {
        navigationManager.selectSurvival(event);
    }

    /**
     * Handle Settings button click - Open settings menu
     */
    @FXML
    private void openSettings(ActionEvent event) {
        navigationManager.openSettings(event);
    }

    /**
     * Handle Exit button click - Close application
     */
    @FXML
    private void exitApplication(ActionEvent event) {
        navigationManager.exitApplication(event);
    }
    
    /**
     * Cleanup method for proper resource disposal
     */
    public void cleanup() {
        if (mediaManager != null) {
            mediaManager.dispose();
        }
    }
}