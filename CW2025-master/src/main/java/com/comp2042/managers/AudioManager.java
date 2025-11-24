package com.comp2042.managers;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;

/**
 * Manages all audio for the game including sound effects and background music.
 * This class simplifies audio management by centralizing all audio operations.
 */
public class AudioManager {
    
    // Sound effect players
    private MediaPlayer blockLandSound;
    private MediaPlayer hardDropSound;
    private MediaPlayer lineClearSound;
    private MediaPlayer gameOverSound;
    private MediaPlayer comboSound;
    private MediaPlayer colorSyncSound;
    private MediaPlayer clearRowsSound;
    
    // Background music
    private MediaPlayer gameplayBackgroundMusic;
    
    private SettingsManager settings;
    
    /**
     * Constructor - initializes all audio
     */
    public AudioManager() {
        settings = SettingsManager.getInstance();
        initializeSoundEffects();
        initializeGameplayBackgroundMusic();
    }
    
    /**
     * Initialize sound effects for gameplay
     */
    private void initializeSoundEffects() {
        try {
            System.out.println("Initializing sound effects...");
            
            // Try to load block land sound
            loadSoundEffect(new String[]{
                "audio/block_land.mp3", 
                "audio/block_place.mp3", 
                "audio/land.wav", 
                "audio/place.wav",
                "audio/block_land.wav",
                "audio/place_block.mp3"
            }, "blockLand");
            
            // Try to load hard drop sound
            loadSoundEffect(new String[]{
                "audio/hard_drop.mp3", 
                "audio/drop.mp3", 
                "audio/hard_drop.wav",
                "audio/drop.wav",
                "audio/harddrop.mp3"
            }, "hardDrop");
            
            // Try to load line clear sound
            loadSoundEffect(new String[]{
                "audio/tetris_success.wav",
                "audio/line_clear.wav",
                "audio/line_clear.mp3",
                "audio/success.wav",
                "audio/clear.wav"
            }, "lineClear");
            
            // Try to load game over sound
            loadSoundEffect(new String[]{
                "audio/game_over.wav",
                "audio/gameover.wav",
                "audio/game_over.mp3",
                "audio/gameover.mp3"
            }, "gameOver");
            
            // Try to load combo sound (for Color Bomb ability)
            loadSoundEffect(new String[]{
                "combo.mp3",
                "audio/combo.mp3",
                "combo.wav",
                "audio/combo.wav"
            }, "combo");
            
            // Try to load color sync sound (for Color Sync ability)
            loadSoundEffect(new String[]{
                "color_sync.wav",
                "audio/color_sync.wav",
                "color_sync.mp3",
                "audio/color_sync.mp3"
            }, "colorSync");
            
            // Try to load clear rows sound (for Clear Bottom 3 Rows ability)
            loadSoundEffect(new String[]{
                "clear3lines.wav",
                "audio/clear3lines.wav",
                "clear3lines.mp3",
                "audio/clear3lines.mp3",
                "clear_rows.wav",
                "audio/clear_rows.wav"
            }, "clearRows");
            
            System.out.println("✓ Sound effects initialization complete");
        } catch (Exception e) {
            System.out.println("✗ Sound effects initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to load a sound effect, trying multiple possible file names
     */
    private void loadSoundEffect(String[] fileNames, String soundType) {
        Media soundMedia = null;
        String foundFile = null;
        
        // Try loading from resources first
        for (String fileName : fileNames) {
            URL soundURL = getClass().getClassLoader().getResource(fileName);
            if (soundURL != null) {
                try {
                    soundMedia = new Media(soundURL.toString());
                    foundFile = fileName;
                    System.out.println("✓ Sound found: " + fileName);
                    break;
                } catch (Exception e) {
                    System.out.println("✗ Failed to load " + fileName + ": " + e.getMessage());
                }
            }
        }
        
        // If not found in resources, try direct file paths
        if (soundMedia == null) {
            for (String fileName : fileNames) {
                String fullPath = "src/main/resources/" + fileName;
                File soundFile = new File(fullPath);
                if (soundFile.exists()) {
                    try {
                        soundMedia = new Media(soundFile.toURI().toString());
                        foundFile = fileName;
                        System.out.println("✓ Sound found at: " + fullPath);
                        break;
                    } catch (Exception e) {
                        System.out.println("✗ Failed to load " + fullPath + ": " + e.getMessage());
                    }
                }
            }
        }
        
        if (soundMedia != null) {
            MediaPlayer soundPlayer = new MediaPlayer(soundMedia);
            soundPlayer.setVolume(settings.getSfxVolume());
            
            switch (soundType) {
                case "blockLand":
                    blockLandSound = soundPlayer;
                    break;
                case "hardDrop":
                    hardDropSound = soundPlayer;
                    break;
                case "lineClear":
                    lineClearSound = soundPlayer;
                    break;
                case "gameOver":
                    gameOverSound = soundPlayer;
                    break;
                case "combo":
                    comboSound = soundPlayer;
                    break;
                case "colorSync":
                    colorSyncSound = soundPlayer;
                    break;
                case "clearRows":
                    clearRowsSound = soundPlayer;
                    break;
            }
            
            System.out.println("✓ Sound effect loaded for: " + foundFile + " -> " + soundType);
        } else {
            System.out.println("✗ No sound file found for " + soundType + ". Tried:");
            for (String fileName : fileNames) {
                System.out.println("  - " + fileName);
            }
            System.out.println("Place sound files in src/main/resources/audio/");
        }
    }
    
    /**
     * Initialize background music for gameplay
     */
    private void initializeGameplayBackgroundMusic() {
        try {
            System.out.println("Initializing gameplay background music...");
            
            Media musicMedia = null;
            String foundFile = null;
            
            String[] musicFiles = {
                "audio/play_music.mp3",
                "audio/play_music.wav",
                "audio/gameplay_music.mp3",
                "audio/gameplay_music.wav",
                "audio/music.mp3",
                "audio/music.wav",
                "audio/tetris_music.mp3",
                "audio/tetris_music.wav"
            };
            
            // Try loading from resources first
            for (String fileName : musicFiles) {
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
                for (String fileName : musicFiles) {
                    String fullPath = "src/main/resources/" + fileName;
                    File musicFile = new File(fullPath);
                    if (musicFile.exists()) {
                        try {
                            musicMedia = new Media(musicFile.toURI().toString());
                            foundFile = fileName;
                            System.out.println("✓ Background music found at: " + fullPath);
                            break;
                        } catch (Exception e) {
                            System.out.println("✗ Failed to load " + fullPath + ": " + e.getMessage());
                        }
                    }
                }
            }
            
            if (musicMedia != null) {
                gameplayBackgroundMusic = new MediaPlayer(musicMedia);
                
                // Set up music properties for seamless looping
                gameplayBackgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                gameplayBackgroundMusic.setVolume(settings.getMusicVolume());
                gameplayBackgroundMusic.setAutoPlay(false); // Start manually
                
                // Add event handlers
                gameplayBackgroundMusic.setOnError(() -> {
                    if (gameplayBackgroundMusic.getError() != null) {
                        System.out.println("✗ Background music error: " + gameplayBackgroundMusic.getError().getMessage());
                    }
                });
                
                gameplayBackgroundMusic.setOnReady(() -> {
                    System.out.println("✓ Gameplay background music ready - starting playback");
                    gameplayBackgroundMusic.play();
                });
                
                gameplayBackgroundMusic.setOnPlaying(() -> {
                    System.out.println("✓ Gameplay background music is now playing");
                });
                
                gameplayBackgroundMusic.setOnEndOfMedia(() -> {
                    System.out.println("Gameplay music ended - restarting");
                    gameplayBackgroundMusic.seek(Duration.ZERO);
                    gameplayBackgroundMusic.play();
                });
                
                System.out.println("✓ Gameplay background music setup completed for: " + foundFile);
            } else {
                System.out.println("✗ No background music file found. Tried:");
                for (String fileName : musicFiles) {
                    System.out.println("  - " + fileName);
                }
                System.out.println("Place your music file in src/main/resources/audio/ with one of the above names");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Gameplay background music initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Sound effect playback methods
    public void playBlockLandSound() {
        if (blockLandSound != null && settings.isSfxEnabled()) {
            blockLandSound.seek(Duration.ZERO);
            blockLandSound.play();
        }
    }
    
    public void playHardDropSound() {
        if (hardDropSound != null && settings.isSfxEnabled()) {
            hardDropSound.seek(Duration.ZERO);
            hardDropSound.play();
        }
    }
    
    public void playLineClearSound() {
        if (lineClearSound != null && settings.isSfxEnabled()) {
            lineClearSound.seek(Duration.ZERO);
            lineClearSound.play();
        }
    }
    
    public void playGameOverSound() {
        if (gameOverSound != null && settings.isSfxEnabled()) {
            gameOverSound.seek(Duration.ZERO);
            gameOverSound.setVolume(0.85); // 85% volume for game over sound
            
            // When game over sound ends, optionally restore background music volume
            gameOverSound.setOnEndOfMedia(() -> {
                if (gameplayBackgroundMusic != null) {
                    gameplayBackgroundMusic.setVolume(0.2); // Slightly increase to 20% after sound finishes
                }
            });
            
            gameOverSound.play();
        }
    }
    
    public void playComboSound() {
        if (comboSound != null && settings.isSfxEnabled()) {
            comboSound.seek(Duration.ZERO);
            comboSound.play();
        }
    }
    
    public void playColorSyncSound() {
        if (colorSyncSound != null && settings.isSfxEnabled()) {
            colorSyncSound.seek(Duration.ZERO);
            colorSyncSound.play();
        }
    }
    
    public void playClearRowsSound() {
        if (clearRowsSound != null && settings.isSfxEnabled()) {
            clearRowsSound.seek(Duration.ZERO);
            clearRowsSound.play();
        }
    }
    
    // Background music control methods
    public void setBackgroundMusicVolume(double volume) {
        if (gameplayBackgroundMusic != null) {
            gameplayBackgroundMusic.setVolume(volume);
        }
    }
    
    public void stopBackgroundMusic() {
        if (gameplayBackgroundMusic != null) {
            gameplayBackgroundMusic.stop();
        }
    }
    
    public void playBackgroundMusic() {
        if (gameplayBackgroundMusic != null) {
            gameplayBackgroundMusic.play();
        }
    }
    
    /**
     * Clean up all audio resources
     */
    public void dispose() {
        if (blockLandSound != null) {
            blockLandSound.stop();
            blockLandSound.dispose();
        }
        if (hardDropSound != null) {
            hardDropSound.stop();
            hardDropSound.dispose();
        }
        if (lineClearSound != null) {
            lineClearSound.stop();
            lineClearSound.dispose();
        }
        if (gameOverSound != null) {
            gameOverSound.stop();
            gameOverSound.dispose();
        }
        if (gameplayBackgroundMusic != null) {
            gameplayBackgroundMusic.stop();
            gameplayBackgroundMusic.dispose();
        }
        if (comboSound != null) {
            comboSound.stop();
            comboSound.dispose();
        }
        if (colorSyncSound != null) {
            colorSyncSound.stop();
            colorSyncSound.dispose();
        }
        if (clearRowsSound != null) {
            clearRowsSound.stop();
            clearRowsSound.dispose();
        }
    }
}

