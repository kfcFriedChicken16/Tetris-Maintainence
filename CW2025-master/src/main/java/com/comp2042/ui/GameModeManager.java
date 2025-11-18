package com.comp2042.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import com.comp2042.modes.GameMode;
import com.comp2042.managers.ScoreManager;
import com.comp2042.managers.AudioManager;
import com.comp2042.ui.panels.GameOverPanel;
import com.comp2042.core.GameStateManager;

/**
 * Manages game mode specific logic and timers.
 * Extracted from GameViewController for better maintainability.
 */
public class GameModeManager {
    
    // Sprint mode timer
    private Timeline sprintTimer;
    private long sprintStartTime = 0;
    
    // Ultra mode variables
    private Timeline ultraTimer;
    private long ultraStartTime = 0;
    private int ultraSpeedLevel = 1; // Current speed level (starts at 1)
    private long currentSpeedInterval = 400; // Current speed in milliseconds (starts at 400ms)
    private static final long ULTRA_TIME_LIMIT = 120000; // 2 minutes in milliseconds
    private static final long ULTRA_SPEED_INCREASE_INTERVAL = 20000; // 20 seconds in milliseconds
    
    // Survival mode variables
    private int survivalSpeedLevel = 1; // Current speed level (starts at 1)
    private long survivalSpeedInterval = 400; // Current speed in milliseconds (starts at 400ms)
    private int survivalNextThreshold = 1500; // Next score threshold for speed increase
    private static final int SURVIVAL_SPEED_INCREASE_THRESHOLD = 1500; // Points needed for each speed increase
    
    // UI Labels
    private javafx.scene.control.Label sprintLinesLabel;
    private javafx.scene.control.Label sprintTimerLabel;
    private javafx.scene.control.Label ultraTimerLabel;
    private javafx.scene.control.Label ultraSpeedLevelLabel;
    private javafx.scene.control.Label survivalSpeedLevelLabel;
    private javafx.scene.control.Label survivalNextThresholdLabel;
    
    // Dependencies
    private ScoreManager scoreManager;
    private AudioManager audioManager;
    private GameStateManager gameStateManager;
    
    // Callback interfaces
    public interface GameModeCallback {
        void ultraComplete();
        void sprintComplete();
        void createAndStartGameTimeline(long speed);
        void centerGameOverPanel(Pane root);
        void stopTimeline();
    }
    
    private GameModeCallback gameModeCallback;
    
    public GameModeManager(ScoreManager scoreManager, AudioManager audioManager, GameStateManager gameStateManager) {
        this.scoreManager = scoreManager;
        this.audioManager = audioManager;
        this.gameStateManager = gameStateManager;
    }
    
    public void setLabels(javafx.scene.control.Label sprintLinesLabel, 
                         javafx.scene.control.Label sprintTimerLabel,
                         javafx.scene.control.Label ultraTimerLabel,
                         javafx.scene.control.Label ultraSpeedLevelLabel,
                         javafx.scene.control.Label survivalSpeedLevelLabel,
                         javafx.scene.control.Label survivalNextThresholdLabel) {
        this.sprintLinesLabel = sprintLinesLabel;
        this.sprintTimerLabel = sprintTimerLabel;
        this.ultraTimerLabel = ultraTimerLabel;
        this.ultraSpeedLevelLabel = ultraSpeedLevelLabel;
        this.survivalSpeedLevelLabel = survivalSpeedLevelLabel;
        this.survivalNextThresholdLabel = survivalNextThresholdLabel;
    }
    
    public void setGameModeCallback(GameModeCallback callback) {
        this.gameModeCallback = callback;
    }
    
    /**
     * Initialize Survival mode
     */
    public void initializeSurvivalMode() {
        survivalSpeedLevel = 1;
        survivalSpeedInterval = 400;
        survivalNextThreshold = SURVIVAL_SPEED_INCREASE_THRESHOLD;
        updateSurvivalDisplay();
    }
    
    /**
     * Update Survival mode display
     */
    private void updateSurvivalDisplay() {
        if (survivalSpeedLevelLabel != null) {
            Platform.runLater(() -> {
                survivalSpeedLevelLabel.setText(String.valueOf(survivalSpeedLevel));
            });
        }
        if (survivalNextThresholdLabel != null) {
            Platform.runLater(() -> {
                survivalNextThresholdLabel.setText(String.valueOf(survivalNextThreshold));
            });
        }
    }
    
    /**
     * Check if Survival mode speed should increase (every 1500 points)
     */
    public void checkSurvivalSpeedIncrease(int currentScore, GameMode currentGameMode) {
        if (currentGameMode == GameMode.SURVIVAL && currentScore >= survivalNextThreshold) {
            survivalSpeedLevel++;
            // Decrease interval (make faster): 400ms -> 350ms -> 300ms -> 250ms -> 200ms, etc.
            // Keep at 400ms for now (can be adjusted later)
            survivalSpeedInterval = 400;
            
            // Update next threshold
            survivalNextThreshold += SURVIVAL_SPEED_INCREASE_THRESHOLD;
            
            // Update highest level if this is better
            scoreManager.updateSurvivalHighestLevel(survivalSpeedLevel);
            
            // Update game timeline speed
            if (gameModeCallback != null && currentGameMode == GameMode.SURVIVAL) {
                gameModeCallback.createAndStartGameTimeline(survivalSpeedInterval);
            }
            
            updateSurvivalDisplay();
        }
    }
    
    /**
     * Start Ultra mode timer and speed progression
     */
    public void startUltraTimer() {
        ultraStartTime = System.currentTimeMillis();
        ultraSpeedLevel = 1;
        currentSpeedInterval = 400; // Start at 400ms
        
        // Update initial display
        updateUltraTimerDisplay();
        updateUltraSpeedLevelDisplay();
        
        if (ultraTimer != null) {
            ultraTimer.stop();
        }
        
        // Timer that updates every 100ms for smooth display
        ultraTimer = new Timeline(new KeyFrame(
                Duration.millis(100),
                ae -> {
                    updateUltraTimerDisplay();
                    checkUltraSpeedIncrease();
                }
        ));
        ultraTimer.setCycleCount(Timeline.INDEFINITE);
        ultraTimer.play();
    }
    
    /**
     * Stop Ultra mode timer
     */
    public void stopUltraTimer() {
        if (ultraTimer != null) {
            ultraTimer.stop();
        }
    }
    
    /**
     * Update Ultra mode timer display (countdown from 2 minutes)
     */
    private void updateUltraTimerDisplay() {
        if (ultraStartTime > 0 && ultraTimerLabel != null && !gameStateManager.isGameOver()) {
            long elapsed = System.currentTimeMillis() - ultraStartTime;
            long remaining = ULTRA_TIME_LIMIT - elapsed;
            
            if (remaining <= 0) {
                // Time's up! Stop immediately
                ultraTimerLabel.setText("00:00");
                // Stop timeline immediately to prevent blocks from falling
                if (gameModeCallback != null) {
                    gameModeCallback.stopTimeline();
                }
                // Set game over state to prevent any further moves
                gameStateManager.setGameOver(true);
                // Then show completion screen
                Platform.runLater(() -> {
                    if (gameModeCallback != null) {
                        gameModeCallback.ultraComplete();
                    }
                });
            } else {
                long seconds = remaining / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;
                ultraTimerLabel.setText(String.format("%02d:%02d", minutes, seconds));
            }
        }
    }
    
    /**
     * Update Ultra mode speed level display
     */
    private void updateUltraSpeedLevelDisplay() {
        if (ultraSpeedLevelLabel != null) {
            Platform.runLater(() -> {
                ultraSpeedLevelLabel.setText(String.valueOf(ultraSpeedLevel));
            });
        }
    }
    
    /**
     * Check if speed should increase (every 20 seconds)
     */
    private void checkUltraSpeedIncrease() {
        if (ultraStartTime > 0) {
            long elapsed = System.currentTimeMillis() - ultraStartTime;
            int expectedLevel = (int) (elapsed / ULTRA_SPEED_INCREASE_INTERVAL) + 1;
            
            if (expectedLevel > ultraSpeedLevel) {
                ultraSpeedLevel = expectedLevel;
                // Keep at 400ms for all levels (can be adjusted later)
                currentSpeedInterval = 400;
                
                // Update game timeline speed
                if (gameModeCallback != null) {
                    gameModeCallback.createAndStartGameTimeline(currentSpeedInterval);
                }
                
                updateUltraSpeedLevelDisplay();
            }
        }
    }
    
    /**
     * Start Sprint mode timer
     */
    public void startSprintTimer() {
        sprintStartTime = System.currentTimeMillis();
        if (sprintTimer != null) {
            sprintTimer.stop();
        }
        sprintTimer = new Timeline(new KeyFrame(
                Duration.millis(100), // Update every 100ms for smooth display
                ae -> updateSprintTimer()
        ));
        sprintTimer.setCycleCount(Timeline.INDEFINITE);
        sprintTimer.play();
    }
    
    /**
     * Update Sprint mode timer display
     */
    private void updateSprintTimer() {
        if (sprintStartTime > 0 && sprintTimerLabel != null) {
            long elapsed = System.currentTimeMillis() - sprintStartTime;
            long seconds = elapsed / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            sprintTimerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }
    
    /**
     * Update Sprint lines count (called from GameController after lines are cleared)
     */
    public void updateSprintLines(int linesCleared) {
        if (sprintLinesLabel != null) {
            Platform.runLater(() -> {
                sprintLinesLabel.setText(linesCleared + " / 40");
            });
        }
    }
    
    /**
     * Sprint mode completed - show completion screen
     */
    public void sprintComplete(GameOverPanel gameOverPanel, Parent gameBoard) {
        if (sprintTimer != null) {
            sprintTimer.stop();
        }
        
        long elapsedTime = System.currentTimeMillis() - sprintStartTime;
        
        // Update best time if this is better
        long oldBest = scoreManager.getSprintBestTime();
        scoreManager.updateSprintBestTime(elapsedTime);
        boolean isNewBest = (elapsedTime < oldBest);
        
        // Stop game timeline
        if (gameModeCallback != null) {
            gameModeCallback.stopTimeline();
        }
        
        // Show big success message
        gameStateManager.setGameOver(true);
        gameOverPanel.setVisible(true);
        
        String successMessage = "SUCCESS!";
        if (isNewBest) {
            successMessage = "NEW RECORD!";
        }
        gameOverPanel.setGameOverMessage(successMessage);
        
        // Show best time and current time
        long bestTime = scoreManager.getSprintBestTime();
        String bestTimeStr = (bestTime == Long.MAX_VALUE) ? "--:--" : ScoreManager.formatTime(bestTime);
        String currentTimeStr = ScoreManager.formatTime(elapsedTime);
        gameOverPanel.setTimeInfo(bestTimeStr, currentTimeStr);
        
        // Center the game over panel on screen
        if (gameBoard.getParent() instanceof Pane && gameModeCallback != null) {
            gameModeCallback.centerGameOverPanel((Pane) gameBoard.getParent());
        }
        
        // Play completion sound
        audioManager.playLineClearSound();
    }
    
    /**
     * Ultra mode completed - time's up!
     */
    public void ultraComplete(GameOverPanel gameOverPanel, Parent gameBoard, javafx.scene.control.Label scoreLabel) {
        // Stop Ultra timer
        stopUltraTimer();
        
        // Ensure game timeline is stopped (should already be stopped, but double-check)
        if (gameModeCallback != null) {
            gameModeCallback.stopTimeline();
        }
        
        // Ensure game over state is set
        gameStateManager.setGameOver(true);
        
        // Get final score from score label
        int finalScore = 0;
        try {
            if (scoreLabel != null && scoreLabel.getText() != null) {
                finalScore = Integer.parseInt(scoreLabel.getText().trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("Could not parse final score: " + e.getMessage());
        }
        
        // Update best score if this is better
        int oldBest = scoreManager.getUltraBestScore();
        scoreManager.updateUltraBestScore(finalScore);
        boolean isNewBest = (finalScore > oldBest);
        
        // Show completion message
        gameStateManager.setGameOver(true);
        gameOverPanel.setVisible(true);
        
        String successMessage = "TIME'S UP!";
        if (isNewBest) {
            successMessage = "NEW RECORD!";
        }
        gameOverPanel.setGameOverMessage(successMessage);
        
        // Show final score
        String bestScoreStr = String.valueOf(scoreManager.getUltraBestScore());
        String currentScoreStr = String.valueOf(finalScore);
        gameOverPanel.setTimeInfo("BEST SCORE: " + bestScoreStr, "FINAL SCORE: " + currentScoreStr);
        
        // Center the game over panel on screen
        if (gameBoard.getParent() instanceof Pane && gameModeCallback != null) {
            gameModeCallback.centerGameOverPanel((Pane) gameBoard.getParent());
        }
        
        // Play completion sound
        audioManager.playLineClearSound();
    }
    
    /**
     * Reset mode-specific variables for new game
     */
    public void resetForNewGame(GameMode currentGameMode) {
        if (currentGameMode == GameMode.SPRINT) {
            if (sprintTimer != null) {
                sprintTimer.stop();
            }
            sprintStartTime = 0;
            if (sprintLinesLabel != null) {
                sprintLinesLabel.setText("0 / 40");
            }
            if (sprintTimerLabel != null) {
                sprintTimerLabel.setText("00:00");
            }
        } else if (currentGameMode == GameMode.ULTRA) {
            if (ultraTimer != null) {
                ultraTimer.stop();
            }
            ultraStartTime = 0;
            ultraSpeedLevel = 1;
            currentSpeedInterval = 400;
            if (ultraTimerLabel != null) {
                ultraTimerLabel.setText("02:00");
            }
            if (ultraSpeedLevelLabel != null) {
                ultraSpeedLevelLabel.setText("1");
            }
        } else if (currentGameMode == GameMode.SURVIVAL) {
            survivalSpeedLevel = 1;
            survivalSpeedInterval = 400;
            survivalNextThreshold = SURVIVAL_SPEED_INCREASE_THRESHOLD;
            if (survivalSpeedLevelLabel != null) {
                survivalSpeedLevelLabel.setText("1");
            }
            if (survivalNextThresholdLabel != null) {
                survivalNextThresholdLabel.setText("1500");
            }
        }
    }
    
    /**
     * Pause mode-specific timers
     */
    public void pauseTimers(GameMode currentGameMode) {
        if (currentGameMode == GameMode.SPRINT && sprintTimer != null) {
            sprintTimer.pause();
        } else if (currentGameMode == GameMode.ULTRA && ultraTimer != null) {
            ultraTimer.pause();
        }
    }
    
    /**
     * Resume mode-specific timers
     */
    public void resumeTimers(GameMode currentGameMode) {
        if (currentGameMode == GameMode.SPRINT && sprintTimer != null) {
            sprintTimer.play();
        } else if (currentGameMode == GameMode.ULTRA && ultraTimer != null) {
            ultraTimer.play();
        }
    }
    
    /**
     * Stop Ultra timer if in Ultra mode
     */
    public void stopUltraTimerIfActive(GameMode currentGameMode) {
        if (currentGameMode == GameMode.ULTRA) {
            stopUltraTimer();
        }
    }
    
    // Getters for current values
    public long getCurrentSpeedInterval() {
        return currentSpeedInterval;
    }
    
    public long getSurvivalSpeedInterval() {
        return survivalSpeedInterval;
    }
    
    public long getUltraStartTime() {
        return ultraStartTime;
    }
    
    public static long getUltraTimeLimit() {
        return ULTRA_TIME_LIMIT;
    }
}
