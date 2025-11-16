package com.comp2042.managers;

import javafx.application.Platform;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;

/**
 * Manages background video and vignette effects for gameplay.
 * This class simplifies video management by centralizing video operations.
 */
public class VideoManager {
    
    private MediaView backgroundVideo;
    private Region videoOverlay;
    private MediaPlayer videoPlayer;
    
    /**
     * Constructor
     * @param backgroundVideo The MediaView from FXML for background video
     * @param videoOverlay The Region from FXML for vignette overlay
     */
    public VideoManager(MediaView backgroundVideo, Region videoOverlay) {
        this.backgroundVideo = backgroundVideo;
        this.videoOverlay = videoOverlay;
    }
    
    /**
     * Initialize background video for gameplay
     */
    public void initializeBackgroundVideo() {
        try {
            System.out.println("Initializing gameplay background video...");
            
            if (backgroundVideo == null) {
                System.out.println("✗ Cannot initialize video - MediaView is null");
                return;
            }
            
            Media videoMedia = null;
            String foundPath = null;
            
            // Method 1: Try loading from resources (works when compiled)
            URL videoURL = getClass().getClassLoader().getResource("single_player2.mp4");
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
                String videoPath = "src/main/resources/single_player.mp4";
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
                System.out.println("✗ No video file found - gameplay will use background color only");
                return;
            }
            
            // Create MediaPlayer
            System.out.println("Creating MediaPlayer for gameplay video...");
            videoPlayer = new MediaPlayer(videoMedia);
            
            // Set up video properties
            videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            videoPlayer.setMute(true); // Mute video audio
            videoPlayer.setAutoPlay(false);
            
            // Add event handlers
            videoPlayer.setOnError(() -> {
                if (videoPlayer.getError() != null) {
                    System.out.println("✗ Video player error: " + videoPlayer.getError().getMessage());
                }
            });
            
            videoPlayer.setOnReady(() -> {
                System.out.println("✓ Gameplay video player ready - starting playback");
                videoPlayer.play();
            });
            
            videoPlayer.setOnPlaying(() -> {
                System.out.println("✓ Gameplay video is now playing");
            });
            
            videoPlayer.setOnEndOfMedia(() -> {
                System.out.println("Gameplay video ended - restarting");
                videoPlayer.seek(Duration.ZERO);
                videoPlayer.play();
            });
            
            // Bind to MediaView
            backgroundVideo.setMediaPlayer(videoPlayer);
            
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
            
            System.out.println("✓ Gameplay video bound to MediaView and configured for full screen");
            
        } catch (Exception e) {
            System.out.println("✗ Gameplay video initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Setup video to fill entire screen
     */
    private void setupVideoFullScreen() {
        if (backgroundVideo != null) {
            if (backgroundVideo.getScene() != null) {
                backgroundVideo.fitWidthProperty().bind(backgroundVideo.getScene().widthProperty());
                backgroundVideo.fitHeightProperty().bind(backgroundVideo.getScene().heightProperty());
                System.out.println("✓ Gameplay video size bound to scene dimensions - full screen coverage");
            } else {
                // Fallback - set large enough to cover typical screens
                backgroundVideo.setFitWidth(1920);
                backgroundVideo.setFitHeight(1080);
                System.out.println("✓ Gameplay video set to large size for full screen coverage (fallback)");
            }
        }
    }
    
    /**
     * Setup dark overlay - makes the entire background 70% darker
     */
    public void setupVignetteEffect() {
        if (videoOverlay == null) {
            System.out.println("✗ videoOverlay is null");
            return;
        }
        
        System.out.println("Setting up dark overlay (70% darker)...");
        
        // Use Platform.runLater to ensure scene is ready
        Platform.runLater(() -> {
            Runnable applyOverlay = () -> {
                if (videoOverlay.getScene() == null) {
                    System.out.println("✗ Scene is null, cannot apply overlay");
                    return;
                }
                
                System.out.println("Applying uniform dark overlay (70% darker)...");
                
                // Create uniform dark overlay - 70% darker (0.7 opacity black)
                Color darkOverlay = Color.rgb(0, 0, 0, 0.7);
                
                // Set the background with uniform dark color
                videoOverlay.setBackground(new Background(
                    new BackgroundFill(darkOverlay, null, null)
                ));
                
                // Make overlay fill entire scene
                javafx.scene.Scene scene = videoOverlay.getScene();
                
                // Unbind first to avoid conflicts
                videoOverlay.prefWidthProperty().unbind();
                videoOverlay.prefHeightProperty().unbind();
                
                // Bind to scene size
                videoOverlay.prefWidthProperty().bind(scene.widthProperty());
                videoOverlay.prefHeightProperty().bind(scene.heightProperty());
                videoOverlay.setMaxWidth(Double.MAX_VALUE);
                videoOverlay.setMaxHeight(Double.MAX_VALUE);
                videoOverlay.setManaged(false); // Disable managed sizing
                
                // Ensure it's visible
                videoOverlay.setVisible(true);
                videoOverlay.setMouseTransparent(true); // Allow clicks to pass through
                
                System.out.println("✓ Dark overlay applied (70% darker)!");
                System.out.println("  Overlay size: " + videoOverlay.getPrefWidth() + "x" + videoOverlay.getPrefHeight());
                System.out.println("  Scene size: " + scene.getWidth() + "x" + scene.getHeight());
            };
            
            if (videoOverlay.getScene() != null) {
                applyOverlay.run();
            } else {
                System.out.println("Waiting for scene to be ready...");
                // Wait for scene to be ready
                videoOverlay.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        System.out.println("Scene is now ready, applying dark overlay...");
                        applyOverlay.run();
                    }
                });
            }
        });
    }
    
    /**
     * Stop and dispose video resources
     */
    public void dispose() {
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.dispose();
        }
    }
}

