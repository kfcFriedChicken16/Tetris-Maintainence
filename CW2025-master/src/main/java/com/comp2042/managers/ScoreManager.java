package com.comp2042.managers;

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
 * Supports Classic mode (highest score), Sprint mode (best time), 
 * Ultra mode (best score), and Survival mode (highest level).
 * This class simplifies score management by centralizing file I/O operations.
 * 
 * @author Phung Yu Jie
 * @version 1.0
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
     * Sets UI labels for score display (optional).
     * Updates all displays after setting the labels.
     * 
     * @param highestScoreLabel Label for displaying highest Classic mode score
     * @param sprintBestTimeLabel Label for displaying Sprint mode best time
     * @param ultraBestScoreLabel Label for displaying Ultra mode best score
     * @param survivalHighestLevelLabel Label for displaying Survival mode highest level
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
    
    /**
     * Gets the highest score achieved in Classic mode.
     * 
     * @return The highest score value
     */
    public int getHighestScore() {
        return highestScore;
    }
    
    /**
     * Updates the highest score if the new score is greater.
     * Saves to file and updates the display.
     * 
     * @param newScore The new score to check against the current highest
     */
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
    
    /**
     * Gets the best time achieved in Sprint mode (in milliseconds).
     * Returns Long.MAX_VALUE if no time has been recorded.
     * 
     * @return The best time in milliseconds, or Long.MAX_VALUE if no record exists
     */
    public long getSprintBestTime() {
        return sprintBestTime;
    }
    
    /**
     * Updates the Sprint best time if the new time is faster.
     * Saves to file and updates the display.
     * 
     * @param newTime The new time in milliseconds to check against the current best
     */
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
    
    /**
     * Gets the best score achieved in Ultra mode.
     * 
     * @return The best Ultra mode score
     */
    public int getUltraBestScore() {
        return ultraBestScore;
    }
    
    /**
     * Updates the Ultra best score if the new score is greater.
     * Saves to file and updates the display.
     * 
     * @param newScore The new score to check against the current best
     */
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
    
    /**
     * Gets the highest level reached in Survival mode.
     * 
     * @return The highest level achieved
     */
    public int getSurvivalHighestLevel() {
        return survivalHighestLevel;
    }
    
    /**
     * Updates the Survival highest level if the new level is greater.
     * Saves to file and updates the display.
     * 
     * @param newLevel The new level to check against the current highest
     */
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
     * Updates all score displays in the UI.
     * Refreshes all label values on the JavaFX application thread.
     */
    public void updateAllDisplays() {
        updateHighestScoreDisplay();
        updateSprintBestTimeDisplay();
        updateUltraBestScoreDisplay();
        updateSurvivalHighestLevelDisplay();
    }
    
    /**
     * Formats time in milliseconds to MM:SS format.
     * Made public static so it can be used by other classes.
     * 
     * @param milliseconds The time in milliseconds to format
     * @return A formatted string in MM:SS format
     */
    public static String formatTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

