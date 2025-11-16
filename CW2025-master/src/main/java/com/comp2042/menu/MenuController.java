package com.comp2042.menu;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.comp2042.modes.GameMode;
import com.comp2042.ui.GuiController;
import com.comp2042.core.GameController;
import com.comp2042.managers.SettingsManager;

import java.io.File;
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

    private MediaPlayer backgroundVideoPlayer;
    private MediaPlayer backgroundMusic;
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== MenuController initializing ===");
        
        // Check if MediaView is properly injected
        if (backgroundVideo != null) {
            System.out.println("✓ MediaView found and ready");
        } else {
            System.out.println("✗ ERROR: MediaView is null - check FXML fx:id");
        }
        
        // List all files in audio directory for debugging
        listAudioFiles();
        
        // Initialize background video
        System.out.println("--- Initializing Video ---");
        initializeBackgroundVideo();
        
        // Initialize background music
        System.out.println("--- Initializing Music ---");
        initializeBackgroundMusic();
        
        // Setup initial menu state - show title and instruction, hide menu buttons
        setupInitialMenuState();
        
        // Setup click and keyboard handlers
        setupInteractionHandlers();
        
        // Setup blinking animation for instruction text
        setupInstructionBlink();
        
        // Add button animations (for when menu appears)
        addButtonAnimations();
        
        System.out.println("=== MenuController initialization complete ===");
    }
    
    /**
     * Setup initial menu state - show title and instruction, hide menu options
     */
    private void setupInitialMenuState() {
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
    private void setupInteractionHandlers() {
        // Use Platform.runLater to ensure scene is ready
        javafx.application.Platform.runLater(() -> {
            // Get the root StackPane from instructionLabel's scene
            if (instructionLabel != null && instructionLabel.getScene() != null) {
                javafx.scene.Node root = instructionLabel.getScene().getRoot();
                if (root instanceof javafx.scene.layout.StackPane) {
                    javafx.scene.layout.StackPane stackPane = (javafx.scene.layout.StackPane) root;
                    
                    // Mouse click handler
                    stackPane.setOnMouseClicked(e -> {
                        if (menuButtonsContainer != null && !menuButtonsContainer.isVisible()) {
                            revealMenu();
                        }
                    });
                    
                    // Keyboard handler (Space key)
                    stackPane.setOnKeyPressed(e -> {
                        if (e.getCode() == javafx.scene.input.KeyCode.SPACE && 
                            menuButtonsContainer != null && !menuButtonsContainer.isVisible()) {
                            revealMenu();
                            e.consume();
                        }
                    });
                    
                    // Make it focusable for keyboard input
                    stackPane.setFocusTraversable(true);
                    stackPane.requestFocus();
                }
            }
        });
    }
    
    /**
     * Setup blinking animation for instruction text
     */
    private void setupInstructionBlink() {
        if (instructionLabel == null) return;
        
        FadeTransition blinkAnimation = new FadeTransition(Duration.millis(1000), instructionLabel);
        blinkAnimation.setFromValue(1.0);
        blinkAnimation.setToValue(0.3);
        blinkAnimation.setCycleCount(Animation.INDEFINITE);
        blinkAnimation.setAutoReverse(true);
        blinkAnimation.play();
    }
    
    /**
     * Reveal menu options - called on click or space key
     */
    private void revealMenu() {
        // Hide instruction text with fade out
        if (instructionLabel != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), instructionLabel);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                instructionLabel.setVisible(false);
                instructionLabel.setManaged(false);
            });
            fadeOut.play();
        }
        
        // Show menu buttons with fade in and slide up
        if (menuButtonsContainer != null) {
            menuButtonsContainer.setManaged(true);
            menuButtonsContainer.setVisible(true);
            menuButtonsContainer.setOpacity(0);
            menuButtonsContainer.setTranslateY(30);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), menuButtonsContainer);
            fadeIn.setToValue(1.0);
            
            TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), menuButtonsContainer);
            slideUp.setToY(0);
            slideUp.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
            
            ParallelTransition reveal = new ParallelTransition(fadeIn, slideUp);
            reveal.play();
        }
    }
    
    /**
     * Debug method to list all files in audio directory
     */
    private void listAudioFiles() {
        try {
            System.out.println("--- Checking audio directory ---");
            
            // Check if we can access the audio directory via resources
            URL audioDir = getClass().getClassLoader().getResource("audio/");
            if (audioDir != null) {
                System.out.println("✓ Audio directory found in resources: " + audioDir);
            } else {
                System.out.println("✗ Audio directory not found in resources");
            }
            
            // Check direct file path
            File audioFolder = new File("src/main/resources/audio/");
            if (audioFolder.exists() && audioFolder.isDirectory()) {
                System.out.println("✓ Audio directory found at: " + audioFolder.getAbsolutePath());
                File[] files = audioFolder.listFiles();
                if (files != null && files.length > 0) {
                    System.out.println("Files in audio directory:");
                    for (File file : files) {
                        System.out.println("  - " + file.getName() + " (" + file.length() + " bytes)");
                    }
                } else {
                    System.out.println("✗ Audio directory is empty");
                }
            } else {
                System.out.println("✗ Audio directory not found at: " + audioFolder.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Error checking audio directory: " + e.getMessage());
        }
    }

    /**
     * Initialize background video for the menu.
     * Uses the MP4 video file as background with 40% darker overlay.
     */
    private void initializeBackgroundVideo() {
        try {
            System.out.println("Looking for video file: menu_background.mp4");
            
            // Check if MediaView is available
            if (backgroundVideo == null) {
                System.out.println("✗ Cannot initialize video - MediaView is null");
                return;
            }
            
            Media videoMedia = null;
            String foundPath = null;
            
            // Method 1: Try loading from resources (works when compiled)
            URL videoURL = getClass().getClassLoader().getResource("audio/menu_background.mp4");
            if (videoURL != null) {
                try {
                    videoMedia = new Media(videoURL.toString());
                    foundPath = videoURL.toString();
                    System.out.println("✓ Video found in resources: " + foundPath);
                } catch (Exception e) {
                    System.out.println("✗ Failed to load video from resources: " + e.getMessage());
                }
            }
            
            // Method 2: Try direct file path (works during development)
            if (videoMedia == null) {
                String videoPath = "src/main/resources/audio/menu_background.mp4";
                File videoFile = new File(videoPath);
                if (videoFile.exists()) {
                    try {
                        videoMedia = new Media(videoFile.toURI().toString());
                        foundPath = videoFile.getAbsolutePath();
                        System.out.println("✓ Video found at file path: " + foundPath);
                    } catch (Exception e) {
                        System.out.println("✗ Failed to load video from file: " + e.getMessage());
                    }
                } else {
                    System.out.println("✗ Video file not found at: " + videoFile.getAbsolutePath());
                }
            }
            
            if (videoMedia == null) {
                System.out.println("✗ No video file found - menu will use background color only");
                return;
            }
            
            // Create MediaPlayer
            System.out.println("Creating MediaPlayer for video...");
            backgroundVideoPlayer = new MediaPlayer(videoMedia);
            
            // Set up video properties
            backgroundVideoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundVideoPlayer.setMute(true);
            backgroundVideoPlayer.setAutoPlay(false); // We'll start manually
            
            // Add event handlers
            backgroundVideoPlayer.setOnError(() -> {
                if (backgroundVideoPlayer.getError() != null) {
                    System.out.println("✗ Video player error: " + backgroundVideoPlayer.getError().getMessage());
                }
            });
            
            backgroundVideoPlayer.setOnReady(() -> {
                System.out.println("✓ Video player ready - starting playback");
                backgroundVideoPlayer.play();
            });
            
            backgroundVideoPlayer.setOnPlaying(() -> {
                System.out.println("✓ Video is now playing");
            });
            
            backgroundVideoPlayer.setOnEndOfMedia(() -> {
                System.out.println("Video ended - restarting");
                backgroundVideoPlayer.seek(Duration.ZERO);
                backgroundVideoPlayer.play();
            });
            
            // Bind to MediaView
            backgroundVideo.setMediaPlayer(backgroundVideoPlayer);
            
            // Setup video sizing after scene is available
            // If scene is already available, set it up now
            if (backgroundVideo.getScene() != null) {
                setupVideoFullScreen();
            } else {
                // Otherwise, wait for scene to be set (called from MainMenu)
                backgroundVideo.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        setupVideoFullScreen();
                    }
                });
            }
            
            System.out.println("✓ Video bound to MediaView and configured for full screen");
            
        } catch (Exception e) {
            System.out.println("✗ Video initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup video to fill entire screen (cover mode - may crop edges to avoid black bars)
     */
    private void setupVideoFullScreen() {
        if (backgroundVideo != null) {
            // Get the scene to bind video size properly
            if (backgroundVideo.getScene() != null) {
                // Bind to scene size to fill entire screen
                // With preserveRatio="false", video will stretch to cover entire screen
                backgroundVideo.fitWidthProperty().bind(backgroundVideo.getScene().widthProperty());
                backgroundVideo.fitHeightProperty().bind(backgroundVideo.getScene().heightProperty());
                System.out.println("✓ Video size bound to scene dimensions - full screen coverage");
            } else if (primaryStage != null) {
                // Fallback to stage size
                backgroundVideo.fitWidthProperty().bind(primaryStage.widthProperty());
                backgroundVideo.fitHeightProperty().bind(primaryStage.heightProperty());
                System.out.println("✓ Video size bound to stage dimensions - full screen coverage");
            } else {
                // Final fallback - set large enough to cover typical screens
                backgroundVideo.setFitWidth(1920);
                backgroundVideo.setFitHeight(1080);
                System.out.println("✓ Video set to large size for full screen coverage (fallback)");
            }
        }
    }

    /**
     * Initialize background music for the menu.
     * TODO: Replace the audio file path with your custom music file
     */
    private void initializeBackgroundMusic() {
        try {
            System.out.println("Looking for background music files...");
            
            // Try multiple audio file formats
            String[] musicFiles = {
                "audio/menu_background.mp3",
                "audio/menu_music.mp3", 
                "audio/background_music.mp3",
                "audio/menu_background.wav",
                "audio/menu_background.m4a"
            };
            
            Media musicMedia = null;
            String foundFile = null;
            
            // Try loading from resources first (recommended)
            for (String fileName : musicFiles) {
                System.out.println("Trying to load: " + fileName);
                URL musicURL = getClass().getClassLoader().getResource(fileName);
                if (musicURL != null) {
                    try {
                        musicMedia = new Media(musicURL.toString());
                        foundFile = fileName;
                        System.out.println("✓ Background music found: " + fileName);
                        break;
                    } catch (Exception e) {
                        System.out.println("✗ Failed to load " + fileName + ": " + e.getMessage());
                    }
                }
            }
            
            // If not found in resources, try direct file paths
            if (musicMedia == null) {
                System.out.println("Trying direct file paths...");
                for (String fileName : musicFiles) {
                    String fullPath = "src/main/resources/" + fileName;
                    File musicFile = new File(fullPath);
                    System.out.println("Checking: " + musicFile.getAbsolutePath());
                    if (musicFile.exists()) {
                        try {
                            musicMedia = new Media(musicFile.toURI().toString());
                            foundFile = fullPath;
                            System.out.println("✓ Background music found at: " + fullPath);
                            break;
                        } catch (Exception e) {
                            System.out.println("✗ Failed to load " + fullPath + ": " + e.getMessage());
                        }
                    }
                }
            }
            
            if (musicMedia != null) {
                System.out.println("Creating MediaPlayer for music...");
                backgroundMusic = new MediaPlayer(musicMedia);
                
                // Set up music properties for seamless looping
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusic.setVolume(0.3);
                backgroundMusic.setAutoPlay(false); // Start manually
                
                // Add event handlers
                backgroundMusic.setOnError(() -> {
                    if (backgroundMusic.getError() != null) {
                        System.out.println("✗ Music player error: " + backgroundMusic.getError().getMessage());
                    }
                });
                
                backgroundMusic.setOnReady(() -> {
                    System.out.println("✓ Background music ready - starting playback");
                    backgroundMusic.play();
                });
                
                backgroundMusic.setOnPlaying(() -> {
                    System.out.println("✓ Background music is now playing");
                });
                
                backgroundMusic.setOnEndOfMedia(() -> {
                    System.out.println("Music ended - restarting");
                    backgroundMusic.seek(Duration.ZERO);
                    backgroundMusic.play();
                });
                
                System.out.println("✓ Background music setup completed for: " + foundFile);
            } else {
                System.out.println("✗ No background music file found. Tried:");
                for (String fileName : musicFiles) {
                    System.out.println("  - " + fileName);
                }
                System.out.println("Place your music file in src/main/resources/audio/ with one of the above names");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Music initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Add hover animations to menu buttons
     */
    private void addButtonAnimations() {
        addButtonHoverEffect(singlePlayerBtn);
        addButtonHoverEffect(settingsBtn);
        addButtonHoverEffect(exitBtn);
        
        // Add animations to mode buttons
        if (classicBtn != null) addButtonHoverEffect(classicBtn);
        if (sprintBtn != null) addButtonHoverEffect(sprintBtn);
        if (ultraBtn != null) addButtonHoverEffect(ultraBtn);
        if (survivalBtn != null) addButtonHoverEffect(survivalBtn);
        if (backToMenuBtn != null) addButtonHoverEffect(backToMenuBtn);
    }

    /**
     * Add enhanced hover effect to a button with smooth transitions
     */
    private void addButtonHoverEffect(Button button) {
        // Create smooth translate animation for lift effect
        TranslateTransition translateUp = new TranslateTransition(Duration.millis(250), button);
        translateUp.setToY(-2);
        translateUp.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        
        TranslateTransition translateDown = new TranslateTransition(Duration.millis(250), button);
        translateDown.setToY(0);
        translateDown.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        
        button.setOnMouseEntered(e -> {
            translateUp.play();
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), button);
            scaleIn.setToX(1.02);
            scaleIn.setToY(1.02);
            scaleIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
            scaleIn.play();
        });

        button.setOnMouseExited(e -> {
            translateDown.play();
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), button);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);
            scaleOut.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
            scaleOut.play();
        });
    }

    /**
     * Handle Single Player button click - Show mode selection on same screen
     */
    @FXML
    private void showModeSelection(ActionEvent event) {
        // Fade out "TETRIS" title and replace with "SELECT MODE"
        if (titleLabel != null) {
            FadeTransition fadeOutTitle = new FadeTransition(Duration.millis(300), titleLabel);
            fadeOutTitle.setToValue(0);
            fadeOutTitle.setOnFinished(e -> {
                titleLabel.setText("SELECT MODE");
                // Apply mode-title style (smaller font, same golden effect)
                titleLabel.getStyleClass().clear();
                titleLabel.getStyleClass().add("mode-title");
                FadeTransition fadeInTitle = new FadeTransition(Duration.millis(300), titleLabel);
                fadeInTitle.setToValue(1.0);
                fadeInTitle.play();
            });
            fadeOutTitle.play();
        }
        
        // Hide main menu buttons with fade out
        if (menuButtonsContainer != null && menuButtonsContainer.isVisible()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), menuButtonsContainer);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                menuButtonsContainer.setVisible(false);
                menuButtonsContainer.setManaged(false);
                
                // Show mode selection buttons with fade in and slide up
                showModeButtons();
            });
            fadeOut.play();
        } else {
            // If menu buttons are already hidden, just show mode buttons
            showModeButtons();
        }
    }
    
    /**
     * Show mode selection buttons with smooth animation
     */
    private void showModeButtons() {
        // Ensure main menu buttons are completely hidden first
        if (menuButtonsContainer != null) {
            menuButtonsContainer.setVisible(false);
            menuButtonsContainer.setManaged(false);
        }
        
        // Now show mode buttons
        if (modeButtonsContainer != null) {
            modeButtonsContainer.setManaged(true);
            modeButtonsContainer.setVisible(true);
            modeButtonsContainer.setOpacity(0);
            modeButtonsContainer.setTranslateY(30);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), modeButtonsContainer);
            fadeIn.setToValue(1.0);
            
            TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), modeButtonsContainer);
            slideUp.setToY(0);
            slideUp.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
            
            ParallelTransition parallel = new ParallelTransition(fadeIn, slideUp);
            parallel.play();
        }
    }
    
    /**
     * Go back to main menu from mode selection
     */
    @FXML
    private void backToMainMenu(ActionEvent event) {
        // Fade out "SELECT MODE" title and replace with "TETRIS"
        if (titleLabel != null) {
            FadeTransition fadeOutTitle = new FadeTransition(Duration.millis(300), titleLabel);
            fadeOutTitle.setToValue(0);
            fadeOutTitle.setOnFinished(e -> {
                titleLabel.setText("TETRIS");
                // Restore original game-title style
                titleLabel.getStyleClass().clear();
                titleLabel.getStyleClass().add("game-title");
                FadeTransition fadeInTitle = new FadeTransition(Duration.millis(300), titleLabel);
                fadeInTitle.setToValue(1.0);
                fadeInTitle.play();
            });
            fadeOutTitle.play();
        }
        
        // Hide mode selection buttons with fade out
        if (modeButtonsContainer != null && modeButtonsContainer.isVisible()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), modeButtonsContainer);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                modeButtonsContainer.setVisible(false);
                modeButtonsContainer.setManaged(false);
                
                // Show main menu buttons with fade in and slide up
                if (menuButtonsContainer != null) {
                    menuButtonsContainer.setManaged(true);
                    menuButtonsContainer.setVisible(true);
                    menuButtonsContainer.setOpacity(0);
                    menuButtonsContainer.setTranslateY(30);
                    
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(400), menuButtonsContainer);
                    fadeIn.setToValue(1.0);
                    
                    TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), menuButtonsContainer);
                    slideUp.setToY(0);
                    slideUp.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                    
                    ParallelTransition parallel = new ParallelTransition(fadeIn, slideUp);
                    parallel.play();
                }
            });
            fadeOut.play();
        }
    }
    
    /**
     * Navigate to game with selected mode
     */
    private void startGame(GameMode mode) {
        try {
            // Immediately hide mode selection buttons to prevent visual glitches
            if (modeButtonsContainer != null) {
                modeButtonsContainer.setVisible(false);
                modeButtonsContainer.setManaged(false);
            }
            
            // Also hide title if it's "SELECT MODE"
            if (titleLabel != null && "SELECT MODE".equals(titleLabel.getText())) {
                titleLabel.setVisible(false);
            }
            
            // Stop background video and music
            if (backgroundVideoPlayer != null) {
                backgroundVideoPlayer.stop();
            }
            if (backgroundMusic != null) {
                backgroundMusic.stop();
            }

            // Load the game layout
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent gameRoot = fxmlLoader.load();
            
            // Get the GuiController and start the game
            GuiController guiController = fxmlLoader.getController();
            
            // Get current stage and switch scene
            Stage stage = (Stage) classicBtn.getScene().getWindow();
            Scene gameScene = new Scene(gameRoot, 1200, 800);
            stage.setScene(gameScene);
            stage.setTitle("Tetris - " + mode.getDisplayName());
            
            // Maintain full screen mode
            stage.setFullScreen(true);
            stage.setFullScreenExitKeyCombination(null);
            stage.setFullScreenExitHint("");
            
            // Initialize the game controller with selected mode
            new GameController(guiController, mode);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error starting game: " + e.getMessage());
        }
    }
    
    @FXML
    private void selectClassic(ActionEvent event) {
        startGame(GameMode.CLASSIC);
    }
    
    @FXML
    private void selectSprint(ActionEvent event) {
        startGame(GameMode.SPRINT);
    }
    
    @FXML
    private void selectUltra(ActionEvent event) {
        startGame(GameMode.ULTRA);
    }
    
    @FXML
    private void selectSurvival(ActionEvent event) {
        startGame(GameMode.SURVIVAL);
    }

    /**
     * Handle Settings button click - Open settings menu
     */
    @FXML
    private void openSettings(ActionEvent event) {
        try {
            // Stop background video and music
            if (backgroundVideoPlayer != null) {
                backgroundVideoPlayer.stop();
            }
            if (backgroundMusic != null) {
                backgroundMusic.stop();
            }
            
            // Load settings page
            URL location = getClass().getClassLoader().getResource("settings.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent settingsRoot = fxmlLoader.load();
            
            SettingsController settingsController = fxmlLoader.getController();
            
            Stage stage = (Stage) settingsBtn.getScene().getWindow();
            Scene settingsScene = new Scene(settingsRoot, 1200, 800);
            stage.setScene(settingsScene);
            stage.setTitle("Tetris - Settings");
            
            SettingsManager settingsManager = SettingsManager.getInstance();
            stage.setFullScreen(settingsManager.isFullscreen());
            stage.setFullScreenExitKeyCombination(null);
            stage.setFullScreenExitHint("");
            
            settingsController.setPrimaryStage(stage);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error opening settings: " + e.getMessage());
            showInfoAlert("Error", "Could not open settings menu: " + e.getMessage());
        }
    }

    /**
     * Handle Exit button click - Close the application
     */
    @FXML
    private void exitGame(ActionEvent event) {
        // Stop background video and music
        if (backgroundVideoPlayer != null) {
            backgroundVideoPlayer.stop();
        }
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        
        // Close the application
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * Set the primary stage reference for scene switching
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Show an information alert dialog
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show an error alert dialog
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clean up resources when the menu is closed
     */
    public void cleanup() {
        if (backgroundVideoPlayer != null) {
            backgroundVideoPlayer.stop();
            backgroundVideoPlayer.dispose();
        }
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
    }
}
