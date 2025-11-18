package com.comp2042.menu;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import com.comp2042.managers.SettingsManager;

import java.io.File;

/**
 * Manages background video and audio for the menu.
 * Extracted from MenuController for better maintainability.
 */
public class MenuMediaManager {
    
    private MediaView backgroundVideo;
    private MediaPlayer backgroundVideoPlayer;
    private MediaPlayer backgroundMusic;
    
    public MenuMediaManager(MediaView backgroundVideo) {
        this.backgroundVideo = backgroundVideo;
    }
    
    /**
     * List all files in audio directory for debugging
     */
    public void listAudioFiles() {
        System.out.println("--- Listing Audio Directory ---");
        try {
            // Try to find the audio directory in resources
            String[] possiblePaths = {
                "src/main/resources/audio/",
                "target/classes/audio/",
                "audio/"
            };
            
            for (String path : possiblePaths) {
                File audioDir = new File(path);
                System.out.println("Checking: " + audioDir.getAbsolutePath());
                if (audioDir.exists() && audioDir.isDirectory()) {
                    System.out.println("✓ Found audio directory: " + audioDir.getAbsolutePath());
                    File[] files = audioDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            System.out.println("  - " + file.getName());
                        }
                    }
                    break;
                } else {
                    System.out.println("✗ Not found: " + audioDir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.out.println("Error listing audio files: " + e.getMessage());
        }
        System.out.println("--- End Audio Directory Listing ---");
    }
    
    /**
     * Initialize background video
     */
    public void initializeBackgroundVideo() {
        if (backgroundVideo == null) {
            System.out.println("✗ ERROR: MediaView is null - cannot initialize video");
            return;
        }
        
        try {
            // Try multiple video file locations - prioritize audio/menu_background.mp4
            String[] videoPaths = {
                "/audio/menu_background.mp4",
                "/menu_background.mp4",
                "/single_player.mp4",
                "/single_player2.mp4"
            };
            
            Media videoMedia = null;
            String usedPath = null;
            
            for (String path : videoPaths) {
                try {
                    java.net.URL videoUrl = getClass().getResource(path);
                    if (videoUrl != null) {
                        videoMedia = new Media(videoUrl.toExternalForm());
                        usedPath = path;
                        System.out.println("✓ Found video: " + path);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("✗ Could not load video: " + path + " - " + e.getMessage());
                }
            }
            
            if (videoMedia != null) {
                backgroundVideoPlayer = new MediaPlayer(videoMedia);
                
                // Configure video player
                backgroundVideoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundVideoPlayer.setMute(true); // Video should be silent
                backgroundVideoPlayer.setAutoPlay(true);
                
                // Set video to MediaView
                backgroundVideo.setMediaPlayer(backgroundVideoPlayer);
                
                // Center and size the video properly
                backgroundVideo.setPreserveRatio(false); // Allow stretching to fill
                backgroundVideo.setSmooth(true); // Enable smooth scaling
                
                // Bind video size to parent container for full coverage
                if (backgroundVideo.getParent() != null) {
                    javafx.scene.layout.Region parent = (javafx.scene.layout.Region) backgroundVideo.getParent();
                    backgroundVideo.fitWidthProperty().bind(parent.widthProperty());
                    backgroundVideo.fitHeightProperty().bind(parent.heightProperty());
                }
                
                // Handle video errors
                backgroundVideoPlayer.setOnError(() -> {
                    System.out.println("✗ Video playback error: " + backgroundVideoPlayer.getError());
                });
                
                // Start playing
                backgroundVideoPlayer.play();
                System.out.println("✓ Background video initialized and playing: " + usedPath);
                
            } else {
                System.out.println("✗ No background video found in resources");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Error initializing background video: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize background music
     */
    public void initializeBackgroundMusic() {
        try {
            // Try multiple audio file locations and formats
            String[] audioPaths = {
                "/audio/menu_background.mp3",
                "/audio/background_music.mp3",
                "/audio/play_music.mp3",
                "/menu_background.mp3",
                "/background_music.mp3",
                "/play_music.mp3"
            };
            
            Media audioMedia = null;
            String usedPath = null;
            
            for (String path : audioPaths) {
                try {
                    java.net.URL audioUrl = getClass().getResource(path);
                    if (audioUrl != null) {
                        audioMedia = new Media(audioUrl.toExternalForm());
                        usedPath = path;
                        System.out.println("✓ Found audio: " + path);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("✗ Could not load audio: " + path + " - " + e.getMessage());
                }
            }
            
            if (audioMedia != null) {
                backgroundMusic = new MediaPlayer(audioMedia);
                
                // Configure music player
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                
                // Get volume from settings
                SettingsManager settings = SettingsManager.getInstance();
                double volume = settings.isMusicEnabled() ? settings.getMusicVolume() : 0.0;
                backgroundMusic.setVolume(volume);
                
                // Handle music errors
                backgroundMusic.setOnError(() -> {
                    System.out.println("✗ Music playback error: " + backgroundMusic.getError());
                });
                
                // Handle when music is ready
                backgroundMusic.setOnReady(() -> {
                    System.out.println("✓ Background music ready, duration: " + 
                                     backgroundMusic.getTotalDuration().toSeconds() + " seconds");
                });
                
                // Start playing
                backgroundMusic.play();
                System.out.println("✓ Background music initialized and playing: " + usedPath);
                
            } else {
                System.out.println("✗ No background music found in resources");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Error initializing background music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Stop all media playback
     */
    public void stopAllMedia() {
        if (backgroundVideoPlayer != null) {
            backgroundVideoPlayer.stop();
            System.out.println("✓ Background video stopped");
        }
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            System.out.println("✓ Background music stopped");
        }
    }
    
    /**
     * Dispose of media resources
     */
    public void dispose() {
        if (backgroundVideoPlayer != null) {
            backgroundVideoPlayer.stop();
            backgroundVideoPlayer.dispose();
            backgroundVideoPlayer = null;
        }
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }
    
    /**
     * Update music volume from settings
     */
    public void updateMusicVolume() {
        if (backgroundMusic != null) {
            SettingsManager settings = SettingsManager.getInstance();
            double volume = settings.isMusicEnabled() ? settings.getMusicVolume() : 0.0;
            backgroundMusic.setVolume(volume);
        }
    }
    
    // Getters for media players (if needed by other components)
    public MediaPlayer getBackgroundVideoPlayer() {
        return backgroundVideoPlayer;
    }
    
    public MediaPlayer getBackgroundMusic() {
        return backgroundMusic;
    }
}
