package com.comp2042;

import javafx.scene.input.KeyCode;

import java.io.*;
import java.util.Properties;

/**
 * Manages game settings including key bindings, audio, and other preferences.
 * Settings are persisted to a properties file.
 */
public class SettingsManager {
    
    private static final String SETTINGS_FILE = "game_settings.properties";
    private static SettingsManager instance;
    
    private Properties settings;
    
    // Default key bindings
    private KeyCode moveLeft = KeyCode.LEFT;
    private KeyCode moveLeftAlt = KeyCode.A;
    private KeyCode moveRight = KeyCode.RIGHT;
    private KeyCode moveRightAlt = KeyCode.D;
    private KeyCode moveDown = KeyCode.DOWN;
    private KeyCode moveDownAlt = KeyCode.S;
    private KeyCode rotate = KeyCode.UP;
    private KeyCode rotateAlt = KeyCode.W;
    private KeyCode hardDrop = KeyCode.SPACE;
    private KeyCode hold = KeyCode.C;
    private KeyCode holdAlt = KeyCode.SHIFT;
    private KeyCode pause = KeyCode.ESCAPE;
    private KeyCode restart = KeyCode.N;
    
    // Audio settings
    private double musicVolume = 0.4; // 40%
    private double sfxVolume = 0.5; // 50%
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;
    
    // Other settings
    private boolean fullscreen = true;
    private boolean showGhostPiece = true;
    
    private SettingsManager() {
        settings = new Properties();
        loadSettings();
    }
    
    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }
    
    /**
     * Load settings from file, or use defaults if file doesn't exist
     */
    private void loadSettings() {
        File settingsFile = new File(SETTINGS_FILE);
        if (settingsFile.exists() && settingsFile.canRead()) {
            try (FileInputStream fis = new FileInputStream(settingsFile)) {
                settings.load(fis);
                
                // Load key bindings
                moveLeft = KeyCode.valueOf(settings.getProperty("key.moveLeft", "LEFT"));
                moveLeftAlt = KeyCode.valueOf(settings.getProperty("key.moveLeftAlt", "A"));
                moveRight = KeyCode.valueOf(settings.getProperty("key.moveRight", "RIGHT"));
                moveRightAlt = KeyCode.valueOf(settings.getProperty("key.moveRightAlt", "D"));
                moveDown = KeyCode.valueOf(settings.getProperty("key.moveDown", "DOWN"));
                moveDownAlt = KeyCode.valueOf(settings.getProperty("key.moveDownAlt", "S"));
                rotate = KeyCode.valueOf(settings.getProperty("key.rotate", "UP"));
                rotateAlt = KeyCode.valueOf(settings.getProperty("key.rotateAlt", "W"));
                hardDrop = KeyCode.valueOf(settings.getProperty("key.hardDrop", "SPACE"));
                hold = KeyCode.valueOf(settings.getProperty("key.hold", "C"));
                holdAlt = KeyCode.valueOf(settings.getProperty("key.holdAlt", "SHIFT"));
                pause = KeyCode.valueOf(settings.getProperty("key.pause", "ESCAPE"));
                restart = KeyCode.valueOf(settings.getProperty("key.restart", "N"));
                
                // Load audio settings
                musicVolume = Double.parseDouble(settings.getProperty("audio.musicVolume", "0.4"));
                sfxVolume = Double.parseDouble(settings.getProperty("audio.sfxVolume", "0.5"));
                musicEnabled = Boolean.parseBoolean(settings.getProperty("audio.musicEnabled", "true"));
                sfxEnabled = Boolean.parseBoolean(settings.getProperty("audio.sfxEnabled", "true"));
                
                // Load other settings
                fullscreen = Boolean.parseBoolean(settings.getProperty("display.fullscreen", "true"));
                showGhostPiece = Boolean.parseBoolean(settings.getProperty("game.showGhostPiece", "true"));
                
            } catch (Exception e) {
                System.out.println("Error loading settings: " + e.getMessage());
                // Use defaults
            }
        }
    }
    
    /**
     * Save current settings to file
     */
    public void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            settings.clear();
            
            // Save key bindings
            settings.setProperty("key.moveLeft", moveLeft.toString());
            settings.setProperty("key.moveLeftAlt", moveLeftAlt.toString());
            settings.setProperty("key.moveRight", moveRight.toString());
            settings.setProperty("key.moveRightAlt", moveRightAlt.toString());
            settings.setProperty("key.moveDown", moveDown.toString());
            settings.setProperty("key.moveDownAlt", moveDownAlt.toString());
            settings.setProperty("key.rotate", rotate.toString());
            settings.setProperty("key.rotateAlt", rotateAlt.toString());
            settings.setProperty("key.hardDrop", hardDrop.toString());
            settings.setProperty("key.hold", hold.toString());
            settings.setProperty("key.holdAlt", holdAlt.toString());
            settings.setProperty("key.pause", pause.toString());
            settings.setProperty("key.restart", restart.toString());
            
            // Save audio settings
            settings.setProperty("audio.musicVolume", String.valueOf(musicVolume));
            settings.setProperty("audio.sfxVolume", String.valueOf(sfxVolume));
            settings.setProperty("audio.musicEnabled", String.valueOf(musicEnabled));
            settings.setProperty("audio.sfxEnabled", String.valueOf(sfxEnabled));
            
            // Save other settings
            settings.setProperty("display.fullscreen", String.valueOf(fullscreen));
            settings.setProperty("game.showGhostPiece", String.valueOf(showGhostPiece));
            
            settings.store(fos, "Tetris Game Settings");
            System.out.println("Settings saved successfully");
        } catch (Exception e) {
            System.out.println("Error saving settings: " + e.getMessage());
        }
    }
    
    // Getters and setters for key bindings
    public KeyCode getMoveLeft() { return moveLeft; }
    public void setMoveLeft(KeyCode key) { this.moveLeft = key; }
    
    public KeyCode getMoveLeftAlt() { return moveLeftAlt; }
    public void setMoveLeftAlt(KeyCode key) { this.moveLeftAlt = key; }
    
    public KeyCode getMoveRight() { return moveRight; }
    public void setMoveRight(KeyCode key) { this.moveRight = key; }
    
    public KeyCode getMoveRightAlt() { return moveRightAlt; }
    public void setMoveRightAlt(KeyCode key) { this.moveRightAlt = key; }
    
    public KeyCode getMoveDown() { return moveDown; }
    public void setMoveDown(KeyCode key) { this.moveDown = key; }
    
    public KeyCode getMoveDownAlt() { return moveDownAlt; }
    public void setMoveDownAlt(KeyCode key) { this.moveDownAlt = key; }
    
    public KeyCode getRotate() { return rotate; }
    public void setRotate(KeyCode key) { this.rotate = key; }
    
    public KeyCode getRotateAlt() { return rotateAlt; }
    public void setRotateAlt(KeyCode key) { this.rotateAlt = key; }
    
    public KeyCode getHardDrop() { return hardDrop; }
    public void setHardDrop(KeyCode key) { this.hardDrop = key; }
    
    public KeyCode getHold() { return hold; }
    public void setHold(KeyCode key) { this.hold = key; }
    
    public KeyCode getHoldAlt() { return holdAlt; }
    public void setHoldAlt(KeyCode key) { this.holdAlt = key; }
    
    public KeyCode getPause() { return pause; }
    public void setPause(KeyCode key) { this.pause = key; }
    
    public KeyCode getRestart() { return restart; }
    public void setRestart(KeyCode key) { this.restart = key; }
    
    // Getters and setters for audio
    public double getMusicVolume() { return musicVolume; }
    public void setMusicVolume(double volume) { this.musicVolume = Math.max(0.0, Math.min(1.0, volume)); }
    
    public double getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(double volume) { this.sfxVolume = Math.max(0.0, Math.min(1.0, volume)); }
    
    public boolean isMusicEnabled() { return musicEnabled; }
    public void setMusicEnabled(boolean enabled) { this.musicEnabled = enabled; }
    
    public boolean isSfxEnabled() { return sfxEnabled; }
    public void setSfxEnabled(boolean enabled) { this.sfxEnabled = enabled; }
    
    // Getters and setters for other settings
    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }
    
    public boolean isShowGhostPiece() { return showGhostPiece; }
    public void setShowGhostPiece(boolean show) { this.showGhostPiece = show; }
}

