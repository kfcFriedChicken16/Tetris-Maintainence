package com.comp2042.menu;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.comp2042.modes.GameMode;
import com.comp2042.ui.GameViewController;
import com.comp2042.core.GameController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the mode selection screen.
 * Handles navigation between mode selection and game/menu.
 */
public class ModeSelectionController implements Initializable {

    @FXML private Button classicBtn;
    @FXML private Button sprintBtn;
    @FXML private Button ultraBtn;
    @FXML private Button survivalBtn;
    @FXML private Button backBtn;
    @FXML private VBox modeButtonsContainer;
    @FXML private MediaView backgroundVideo;
    
    private MediaPlayer backgroundVideoPlayer;
    private MediaPlayer backgroundMusic;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== ModeSelectionController initializing ===");
        
        // Initialize background video
        initializeBackgroundVideo();
        
        // Initialize background music
        initializeBackgroundMusic();
        
        // Add button animations
        addButtonAnimations();
        
        System.out.println("=== ModeSelectionController initialization complete ===");
    }
    
    /**
     * Initialize background video for the mode selection screen
     */
    private void initializeBackgroundVideo() {
        try {
            if (backgroundVideo == null) {
                System.out.println("✗ Cannot initialize video - MediaView is null");
                return;
            }
            
            Media videoMedia = null;
            
            // Try loading from resources first
            URL videoURL = getClass().getClassLoader().getResource("menu_background.mp4");
            if (videoURL != null) {
                try {
                    videoMedia = new Media(videoURL.toString());
                    System.out.println("✓ Video found in resources");
                } catch (Exception e) {
                    System.out.println("✗ Failed to load video from resources: " + e.getMessage());
                }
            }
            
            // If not found in resources, try direct file path
            if (videoMedia == null) {
                String videoPath = "src/main/resources/menu_background.mp4";
                File videoFile = new File(videoPath);
                if (videoFile.exists()) {
                    try {
                        videoMedia = new Media(videoFile.toURI().toString());
                        System.out.println("✓ Video found at file path");
                    } catch (Exception e) {
                        System.out.println("✗ Failed to load video from file: " + e.getMessage());
                    }
                }
            }
            
            if (videoMedia != null) {
                backgroundVideoPlayer = new MediaPlayer(videoMedia);
                backgroundVideoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundVideoPlayer.setMute(true);
                backgroundVideo.setMediaPlayer(backgroundVideoPlayer);
                
                // Bind video size to scene
                backgroundVideo.fitWidthProperty().bind(
                    ((StackPane) backgroundVideo.getParent()).widthProperty()
                );
                backgroundVideo.fitHeightProperty().bind(
                    ((StackPane) backgroundVideo.getParent()).heightProperty()
                );
                
                backgroundVideoPlayer.play();
                System.out.println("✓ Background video playing");
            } else {
                System.out.println("✗ No video file found - using black background");
            }
        } catch (Exception e) {
            System.out.println("✗ Error initializing background video: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize background music for the mode selection screen
     */
    private void initializeBackgroundMusic() {
        try {
            String[] musicFiles = {"menu_music.mp3", "background_music.mp3"};
            
            Media musicMedia = null;
            String foundFile = null;
            
            // Try loading from resources first
            for (String fileName : musicFiles) {
                URL musicURL = getClass().getClassLoader().getResource(fileName);
                if (musicURL != null) {
                    try {
                        musicMedia = new Media(musicURL.toString());
                        foundFile = fileName;
                        System.out.println("✓ Music found: " + fileName);
                        break;
                    } catch (Exception e) {
                        System.out.println("✗ Failed to load " + fileName + ": " + e.getMessage());
                    }
                }
            }
            
            // If not found in resources, try direct file paths
            if (musicMedia == null) {
                for (String fileName : musicFiles) {
                    String fullPath = "src/main/resources/" + fileName;
                    File musicFile = new File(fullPath);
                    if (musicFile.exists()) {
                        try {
                            musicMedia = new Media(musicFile.toURI().toString());
                            foundFile = fullPath;
                            System.out.println("✓ Music found at: " + fullPath);
                            break;
                        } catch (Exception e) {
                            System.out.println("✗ Failed to load " + fullPath + ": " + e.getMessage());
                        }
                    }
                }
            }
            
            if (musicMedia != null) {
                backgroundMusic = new MediaPlayer(musicMedia);
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusic.setVolume(0.3); // 30% volume
                backgroundMusic.play();
                System.out.println("✓ Background music playing: " + foundFile);
            } else {
                System.out.println("✗ No music file found");
            }
        } catch (Exception e) {
            System.out.println("✗ Error initializing background music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Add button animations (hover effects)
     */
    private void addButtonAnimations() {
        Button[] buttons = {classicBtn, sprintBtn, ultraBtn, survivalBtn, backBtn};
        
        for (Button button : buttons) {
            if (button == null) continue;
            
            button.setOnMouseEntered(e -> {
                TranslateTransition translateUp = new TranslateTransition(Duration.millis(100), button);
                translateUp.setToY(-3);
                translateUp.play();
                ScaleTransition scaleIn = new ScaleTransition(Duration.millis(100), button);
                scaleIn.setToX(1.05);
                scaleIn.setToY(1.05);
                scaleIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                scaleIn.play();
            });
            
            button.setOnMouseExited(e -> {
                TranslateTransition translateDown = new TranslateTransition(Duration.millis(100), button);
                translateDown.setToY(0);
                translateDown.play();
                ScaleTransition scaleOut = new ScaleTransition(Duration.millis(100), button);
                scaleOut.setToX(1.0);
                scaleOut.setToY(1.0);
                scaleOut.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                scaleOut.play();
            });
        }
    }
    
    /**
     * Navigate to game with selected mode
     */
    private void startGame(GameMode mode) {
        try {
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
    
    @FXML
    private void goBack(ActionEvent event) {
        try {
            // Stop background video and music
            if (backgroundVideoPlayer != null) {
                backgroundVideoPlayer.stop();
            }
            if (backgroundMusic != null) {
                backgroundMusic.stop();
            }
            
            // Load the main menu
            URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent menuRoot = fxmlLoader.load();
            
            // Get current stage and switch back to menu
            Stage stage = (Stage) backBtn.getScene().getWindow();
            Scene menuScene = new Scene(menuRoot, 1200, 800);
            stage.setScene(menuScene);
            stage.setTitle("Tetris - Enhanced Edition");
            
            // Lock in full screen mode - cannot be exited
            stage.setResizable(false); // Prevent window manipulation
            stage.setFullScreen(true);
            stage.setFullScreenExitKeyCombination(null);
            stage.setFullScreenExitHint("");
            
            // Add fullscreen enforcement listener
            enforceFullscreenMode(stage);
            
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

