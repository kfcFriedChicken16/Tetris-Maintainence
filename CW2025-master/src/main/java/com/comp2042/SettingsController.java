package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the settings screen.
 * Handles key binding customization, audio settings, and other game preferences.
 */
public class SettingsController implements Initializable {
    
    @FXML private MediaView backgroundVideo;
    @FXML private Button moveLeftBtn, moveLeftAltBtn;
    @FXML private Button moveRightBtn, moveRightAltBtn;
    @FXML private Button moveDownBtn, moveDownAltBtn;
    @FXML private Button rotateBtn, rotateAltBtn;
    @FXML private Button hardDropBtn;
    @FXML private Button holdBtn, holdAltBtn;
    @FXML private Button pauseBtn;
    @FXML private Button restartBtn;
    @FXML private Button saveBtn, resetBtn, backBtn;
    @FXML private javafx.scene.control.Label successMessageLabel;
    
    private SettingsManager settingsManager;
    private Button currentKeyBindingButton = null;
    private MediaPlayer backgroundVideoPlayer;
    private MediaPlayer backgroundMusic;
    private Stage primaryStage;
    private javafx.scene.Scene settingsScene; // Store scene reference for event filter
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingsManager = SettingsManager.getInstance();
        
        // Initialize background video
        initializeBackgroundVideo();
        
        // Initialize background music
        initializeBackgroundMusic();
        
        // Load current settings into UI
        loadSettingsToUI();
        
        // Setup event handlers (removed - no audio/game options UI)
        // setupEventHandlers();
    }
    
    private void initializeBackgroundVideo() {
        try {
            System.out.println("=== SettingsController: Initializing background video ===");
            
            if (backgroundVideo == null) {
                System.out.println("✗ ERROR: MediaView is null - check FXML fx:id");
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
                System.out.println("✗ No video file found - settings will use background color only");
                return;
            }
            
            // Create MediaPlayer
            System.out.println("Creating MediaPlayer for settings background video...");
            backgroundVideoPlayer = new MediaPlayer(videoMedia);
            
            // Set up video properties
            backgroundVideoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundVideoPlayer.setMute(true); // Mute video audio
            backgroundVideoPlayer.setAutoPlay(false);
            
            // Add event handlers
            backgroundVideoPlayer.setOnError(() -> {
                if (backgroundVideoPlayer.getError() != null) {
                    System.out.println("✗ Video player error: " + backgroundVideoPlayer.getError().getMessage());
                }
            });
            
            backgroundVideoPlayer.setOnReady(() -> {
                System.out.println("✓ Settings background video player ready - starting playback");
                backgroundVideoPlayer.play();
            });
            
            backgroundVideoPlayer.setOnPlaying(() -> {
                System.out.println("✓ Settings background video is now playing");
            });
            
            backgroundVideoPlayer.setOnEndOfMedia(() -> {
                System.out.println("Settings video ended - restarting");
                backgroundVideoPlayer.seek(Duration.ZERO);
                backgroundVideoPlayer.play();
            });
            
            // Bind to MediaView
            backgroundVideo.setMediaPlayer(backgroundVideoPlayer);
            
            // Setup video sizing after scene is available
            if (backgroundVideo.getScene() != null) {
                setupVideoFullScreen();
            } else {
                backgroundVideo.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        setupVideoFullScreen();
                    }
                });
            }
            
            System.out.println("✓ Settings background video bound to MediaView and configured for full screen");
            
        } catch (Exception e) {
            System.out.println("✗ Settings background video initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Setup video to fill entire screen
     */
    private void setupVideoFullScreen() {
        if (backgroundVideo != null && backgroundVideo.getScene() != null) {
            backgroundVideo.fitWidthProperty().bind(backgroundVideo.getScene().widthProperty());
            backgroundVideo.fitHeightProperty().bind(backgroundVideo.getScene().heightProperty());
            System.out.println("✓ Settings video size bound to scene dimensions - full screen coverage");
        } else {
            // Fallback - set large enough to cover typical screens
            if (backgroundVideo != null) {
                backgroundVideo.setFitWidth(1920);
                backgroundVideo.setFitHeight(1080);
                System.out.println("✓ Settings video set to large size for full screen coverage (fallback)");
            }
        }
    }
    
    private void initializeBackgroundMusic() {
        try {
            Media musicMedia = null;
            URL musicURL = getClass().getClassLoader().getResource("audio/background_music.mp3");
            if (musicURL != null) {
                musicMedia = new Media(musicURL.toString());
            } else {
                String musicPath = "src/main/resources/audio/background_music.mp3";
                File musicFile = new File(musicPath);
                if (musicFile.exists()) {
                    musicMedia = new Media(musicFile.toURI().toString());
                }
            }
            
            if (musicMedia != null) {
                backgroundMusic = new MediaPlayer(musicMedia);
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusic.setVolume(settingsManager.getMusicVolume());
                backgroundMusic.setAutoPlay(false);
                backgroundMusic.play();
            }
        } catch (Exception e) {
            System.out.println("Error initializing background music: " + e.getMessage());
        }
    }
    
    /**
     * Format key code for display (shorten long key names)
     */
    private String formatKeyName(KeyCode keyCode) {
        if (keyCode == null) return "";
        String name = keyCode.toString();
        // Shorten common long key names
        switch (name) {
            case "ESCAPE": return "ESC";
            case "SPACE": return "SPACE";
            case "SHIFT": return "SHIFT";
            case "CONTROL": return "CTRL";
            case "ALT": return "ALT";
            case "ENTER": return "ENTER";
            case "BACK_SPACE": return "BACKSPACE";
            case "DELETE": return "DEL";
            default: return name;
        }
    }
    
    private void loadSettingsToUI() {
        // Load key bindings (with formatted names)
        moveLeftBtn.setText(formatKeyName(settingsManager.getMoveLeft()));
        moveLeftAltBtn.setText(formatKeyName(settingsManager.getMoveLeftAlt()));
        moveRightBtn.setText(formatKeyName(settingsManager.getMoveRight()));
        moveRightAltBtn.setText(formatKeyName(settingsManager.getMoveRightAlt()));
        moveDownBtn.setText(formatKeyName(settingsManager.getMoveDown()));
        moveDownAltBtn.setText(formatKeyName(settingsManager.getMoveDownAlt()));
        rotateBtn.setText(formatKeyName(settingsManager.getRotate()));
        rotateAltBtn.setText(formatKeyName(settingsManager.getRotateAlt()));
        hardDropBtn.setText(formatKeyName(settingsManager.getHardDrop()));
        holdBtn.setText(formatKeyName(settingsManager.getHold()));
        holdAltBtn.setText(formatKeyName(settingsManager.getHoldAlt()));
        pauseBtn.setText(formatKeyName(settingsManager.getPause()));
        restartBtn.setText(formatKeyName(settingsManager.getRestart()));
        
        // Audio and game options UI removed - settings still saved/loaded in SettingsManager
    }
    
    // Event handlers removed - audio and game options UI removed
    
    @FXML
    private void changeKeyBinding(ActionEvent event) {
        Button button = (Button) event.getSource();
        
        if (currentKeyBindingButton != null) {
            // Reset previous button
            currentKeyBindingButton.setStyle("");
            // Remove previous filter
            removeKeyBindingFilter();
        }
        
        currentKeyBindingButton = button;
        button.setText("Press any key...");
        button.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black;");
        
        // Make button focusable and request focus
        button.setFocusTraversable(true);
        button.requestFocus();
        
        // Store scene reference and use addEventFilter to capture events earlier
        // This ensures arrow keys are captured before focus traversal consumes them
        if (button.getScene() != null) {
            settingsScene = button.getScene();
            settingsScene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyBindingFilter);
        }
    }
    
    private void removeKeyBindingFilter() {
        if (settingsScene != null) {
            settingsScene.removeEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyBindingFilter);
        }
    }
    
    private void handleKeyBindingFilter(KeyEvent event) {
        if (currentKeyBindingButton == null) {
            removeKeyBindingFilter();
            return;
        }
        
        KeyCode keyCode = event.getCode();
        
        // Don't allow ESC, ENTER, or TAB as bindings (they're used for navigation)
        if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.ENTER || keyCode == KeyCode.TAB) {
            currentKeyBindingButton.setText(getCurrentKeyForButton(currentKeyBindingButton));
            currentKeyBindingButton.setStyle("");
            currentKeyBindingButton = null;
            event.consume();
            removeKeyBindingFilter();
            return;
        }
        
        // Update the button text and save to settings (with formatted name)
        currentKeyBindingButton.setText(formatKeyName(keyCode));
        currentKeyBindingButton.setStyle("");
        
        // Update settings based on which button was pressed
        if (currentKeyBindingButton == moveLeftBtn) {
            settingsManager.setMoveLeft(keyCode);
        } else if (currentKeyBindingButton == moveLeftAltBtn) {
            settingsManager.setMoveLeftAlt(keyCode);
        } else if (currentKeyBindingButton == moveRightBtn) {
            settingsManager.setMoveRight(keyCode);
        } else if (currentKeyBindingButton == moveRightAltBtn) {
            settingsManager.setMoveRightAlt(keyCode);
        } else if (currentKeyBindingButton == moveDownBtn) {
            settingsManager.setMoveDown(keyCode);
        } else if (currentKeyBindingButton == moveDownAltBtn) {
            settingsManager.setMoveDownAlt(keyCode);
        } else if (currentKeyBindingButton == rotateBtn) {
            settingsManager.setRotate(keyCode);
        } else if (currentKeyBindingButton == rotateAltBtn) {
            settingsManager.setRotateAlt(keyCode);
        } else if (currentKeyBindingButton == hardDropBtn) {
            settingsManager.setHardDrop(keyCode);
        } else if (currentKeyBindingButton == holdBtn) {
            settingsManager.setHold(keyCode);
        } else if (currentKeyBindingButton == holdAltBtn) {
            settingsManager.setHoldAlt(keyCode);
        } else if (currentKeyBindingButton == pauseBtn) {
            settingsManager.setPause(keyCode);
        } else if (currentKeyBindingButton == restartBtn) {
            settingsManager.setRestart(keyCode);
        }
        
        currentKeyBindingButton = null;
        event.consume();
        
        // Remove the filter after capturing the key
        removeKeyBindingFilter();
    }
    
    private String getCurrentKeyForButton(Button button) {
        if (button == moveLeftBtn) return formatKeyName(settingsManager.getMoveLeft());
        if (button == moveLeftAltBtn) return formatKeyName(settingsManager.getMoveLeftAlt());
        if (button == moveRightBtn) return formatKeyName(settingsManager.getMoveRight());
        if (button == moveRightAltBtn) return formatKeyName(settingsManager.getMoveRightAlt());
        if (button == moveDownBtn) return formatKeyName(settingsManager.getMoveDown());
        if (button == moveDownAltBtn) return formatKeyName(settingsManager.getMoveDownAlt());
        if (button == rotateBtn) return formatKeyName(settingsManager.getRotate());
        if (button == rotateAltBtn) return formatKeyName(settingsManager.getRotateAlt());
        if (button == hardDropBtn) return formatKeyName(settingsManager.getHardDrop());
        if (button == holdBtn) return formatKeyName(settingsManager.getHold());
        if (button == holdAltBtn) return formatKeyName(settingsManager.getHoldAlt());
        if (button == pauseBtn) return formatKeyName(settingsManager.getPause());
        if (button == restartBtn) return formatKeyName(settingsManager.getRestart());
        return "";
    }
    
    @FXML
    private void saveSettings(ActionEvent event) {
        settingsManager.saveSettings();
        
        // Show success message inline
        showSuccessMessage("Settings saved successfully!");
        
        // Apply fullscreen setting if stage is available (game options UI removed but setting still works)
        if (primaryStage != null) {
            primaryStage.setFullScreen(settingsManager.isFullscreen());
        }
    }
    
    /**
     * Show a success message inline on the settings page
     */
    private void showSuccessMessage(String message) {
        if (successMessageLabel != null) {
            successMessageLabel.setText(message);
            successMessageLabel.setVisible(true);
            successMessageLabel.setManaged(true);
            
            // Auto-hide after 3 seconds
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> {
                    successMessageLabel.setVisible(false);
                    successMessageLabel.setManaged(false);
                })
            );
            timeline.play();
        }
    }
    
    @FXML
    private void resetToDefaults(ActionEvent event) {
        // Reset to defaults without confirmation dialog
        settingsManager.setMoveLeft(KeyCode.LEFT);
        settingsManager.setMoveLeftAlt(KeyCode.A);
        settingsManager.setMoveRight(KeyCode.RIGHT);
        settingsManager.setMoveRightAlt(KeyCode.D);
        settingsManager.setMoveDown(KeyCode.DOWN);
        settingsManager.setMoveDownAlt(KeyCode.S);
        settingsManager.setRotate(KeyCode.UP);
        settingsManager.setRotateAlt(KeyCode.W);
        settingsManager.setHardDrop(KeyCode.SPACE);
        settingsManager.setHold(KeyCode.C);
        settingsManager.setHoldAlt(KeyCode.SHIFT);
        settingsManager.setPause(KeyCode.ESCAPE);
        settingsManager.setRestart(KeyCode.N);
        // Audio and game options reset (UI removed but settings still managed)
        settingsManager.setMusicVolume(0.4);
        settingsManager.setSfxVolume(0.5);
        settingsManager.setMusicEnabled(true);
        settingsManager.setSfxEnabled(true);
        settingsManager.setFullscreen(true);
        settingsManager.setShowGhostPiece(true);
        
        // Save the reset settings
        settingsManager.saveSettings();
        
        // Reload UI
        loadSettingsToUI();
        
        // Show success message inline
        showSuccessMessage("Settings reset to defaults!");
    }
    
    @FXML
    private void backToMenu(ActionEvent event) {
        try {
            // Stop background video and music
            if (backgroundVideoPlayer != null) {
                backgroundVideoPlayer.stop();
            }
            if (backgroundMusic != null) {
                backgroundMusic.stop();
            }
            
            // Load main menu
            URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent menuRoot = fxmlLoader.load();
            
            MenuController menuController = fxmlLoader.getController();
            
            Stage stage = (Stage) backBtn.getScene().getWindow();
            Scene menuScene = new Scene(menuRoot, 1200, 800);
            stage.setScene(menuScene);
            stage.setTitle("Tetris - Enhanced Edition");
            stage.setFullScreen(settingsManager.isFullscreen());
            stage.setFullScreenExitHint("Press ESC to exit full screen");
            
            menuController.setPrimaryStage(stage);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error returning to menu: " + e.getMessage());
        }
    }
    
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
}

