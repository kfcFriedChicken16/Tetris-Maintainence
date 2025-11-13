package com.comp2042;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Manages score persistence and tracking for all game modes.
 * Handles loading/saving scores to files and updating UI displays.
 * This class simplifies score management by centralizing file I/O operations.
 */
public class ScoreManager {
    
    // File names for different score types
    private static final String HIGHEST_SCORE_FILE = "highest_score.txt";
    private static final String SPRINT_BEST_TIME_FILE = "sprint_best_time.txt";
    private static final String ULTRA_BEST_SCORE_FILE = "ultra_best_score.txt";
    private static final String SURVIVAL_HIGHEST_LEVEL_FILE = "survival_highest_level.txt";
    
    // Score values
    private int highestScore = 0;
    private long sprintBestTime = Long.MAX_VALUE;
    private int ultraBestScore = 0;
    private int survivalHighestLevel = 1;
    
    // UI Labels (optional - can be null if not needed)
    private Label highestScoreLabel;
    private Label sprintBestTimeLabel;
    private Label ultraBestScoreLabel;
    private Label survivalHighestLevelLabel;
    
    /**
     * Constructor - loads all scores from files
     */
    public ScoreManager() {
        loadAllScores();
    }
    
    /**
     * Set UI labels for score display (optional)
     */
    public void setLabels(Label highestScoreLabel, Label sprintBestTimeLabel, 
                         Label ultraBestScoreLabel, Label survivalHighestLevelLabel) {
        this.highestScoreLabel = highestScoreLabel;
        this.sprintBestTimeLabel = sprintBestTimeLabel;
        this.ultraBestScoreLabel = ultraBestScoreLabel;
        this.survivalHighestLevelLabel = survivalHighestLevelLabel;
        updateAllDisplays();
    }
    
    /**
     * Load all scores from files
     */
    private void loadAllScores() {
        highestScore = loadIntFromFile(HIGHEST_SCORE_FILE, 0);
        sprintBestTime = loadLongFromFile(SPRINT_BEST_TIME_FILE, Long.MAX_VALUE);
        ultraBestScore = loadIntFromFile(ULTRA_BEST_SCORE_FILE, 0);
        survivalHighestLevel = loadIntFromFile(SURVIVAL_HIGHEST_LEVEL_FILE, 1);
    }
    
    /**
     * Generic method to load an integer from a file
     */
    private int loadIntFromFile(String fileName, int defaultValue) {
        try {
            File file = new File(fileName);
            if (file.exists() && file.canRead()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        return Integer.parseInt(line.trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not load " + fileName + ": " + e.getMessage());
        }
        return defaultValue;
    }
    
    /**
     * Generic method to load a long from a file
     */
    private long loadLongFromFile(String fileName, long defaultValue) {
        try {
            File file = new File(fileName);
            if (file.exists() && file.canRead()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        return Long.parseLong(line.trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not load " + fileName + ": " + e.getMessage());
        }
        return defaultValue;
    }
    
    /**
     * Generic method to save an integer to a file
     */
    private void saveIntToFile(String fileName, int value) {
        try {
            File file = new File(fileName);
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(value);
            }
        } catch (Exception e) {
            System.out.println("Could not save " + fileName + ": " + e.getMessage());
        }
    }
    
    /**
     * Generic method to save a long to a file
     */
    private void saveLongToFile(String fileName, long value) {
        try {
            File file = new File(fileName);
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(value);
            }
        } catch (Exception e) {
            System.out.println("Could not save " + fileName + ": " + e.getMessage());
        }
    }
    
    // Highest Score methods
    public int getHighestScore() {
        return highestScore;
    }
    
    public void updateHighestScore(int newScore) {
        if (newScore > highestScore) {
            highestScore = newScore;
            saveIntToFile(HIGHEST_SCORE_FILE, highestScore);
            updateHighestScoreDisplay();
        }
    }
    
    private void updateHighestScoreDisplay() {
        if (highestScoreLabel != null) {
            Platform.runLater(() -> {
                highestScoreLabel.setText(String.valueOf(highestScore));
            });
        }
    }
    
    // Sprint Best Time methods
    public long getSprintBestTime() {
        return sprintBestTime;
    }
    
    public void updateSprintBestTime(long newTime) {
        if (newTime < sprintBestTime) {
            sprintBestTime = newTime;
            saveLongToFile(SPRINT_BEST_TIME_FILE, sprintBestTime);
            updateSprintBestTimeDisplay();
        }
    }
    
    private void updateSprintBestTimeDisplay() {
        if (sprintBestTimeLabel != null) {
            Platform.runLater(() -> {
                if (sprintBestTime == Long.MAX_VALUE) {
                    sprintBestTimeLabel.setText("--:--");
                } else {
                    sprintBestTimeLabel.setText(formatTime(sprintBestTime));
                }
            });
        }
    }
    
    // Ultra Best Score methods
    public int getUltraBestScore() {
        return ultraBestScore;
    }
    
    public void updateUltraBestScore(int newScore) {
        if (newScore > ultraBestScore) {
            ultraBestScore = newScore;
            saveIntToFile(ULTRA_BEST_SCORE_FILE, ultraBestScore);
            updateUltraBestScoreDisplay();
        }
    }
    
    private void updateUltraBestScoreDisplay() {
        if (ultraBestScoreLabel != null) {
            Platform.runLater(() -> {
                ultraBestScoreLabel.setText(String.valueOf(ultraBestScore));
            });
        }
    }
    
    // Survival Highest Level methods
    public int getSurvivalHighestLevel() {
        return survivalHighestLevel;
    }
    
    public void updateSurvivalHighestLevel(int newLevel) {
        if (newLevel > survivalHighestLevel) {
            survivalHighestLevel = newLevel;
            saveIntToFile(SURVIVAL_HIGHEST_LEVEL_FILE, survivalHighestLevel);
            updateSurvivalHighestLevelDisplay();
        }
    }
    
    private void updateSurvivalHighestLevelDisplay() {
        if (survivalHighestLevelLabel != null) {
            Platform.runLater(() -> {
                survivalHighestLevelLabel.setText(String.valueOf(survivalHighestLevel));
            });
        }
    }
    
    /**
     * Update all score displays
     */
    public void updateAllDisplays() {
        updateHighestScoreDisplay();
        updateSprintBestTimeDisplay();
        updateUltraBestScoreDisplay();
        updateSurvivalHighestLevelDisplay();
    }
    
    /**
     * Format time in milliseconds to MM:SS format
     * Made public static so it can be used by other classes
     */
    public static String formatTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

