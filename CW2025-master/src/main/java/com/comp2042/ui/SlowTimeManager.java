package com.comp2042.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Manages slow time ability functionality.
 * Extracted from GameViewController for better maintainability.
 */
public class SlowTimeManager {
    
    private Timeline slowModeTimeline;
    private boolean slowModeActive = false;
    private int slowModeSecondsRemaining = 0;
    private long normalDropSpeedMs = 400;
    private int slowAbilitySlotIndex = -1;
    
    private Label[] timerLabels;
    private SpeedUpdateCallback speedUpdateCallback;
    
    public interface SpeedUpdateCallback {
        void updateDropSpeed(long speedMs, boolean treatAsNormal);
    }
    
    public SlowTimeManager(Label abilitySlot1Timer, Label abilitySlot2Timer, 
                          Label abilitySlot3Timer, Label abilitySlot4Timer,
                          SpeedUpdateCallback speedUpdateCallback) {
        this.timerLabels = new Label[]{abilitySlot1Timer, abilitySlot2Timer, abilitySlot3Timer, abilitySlot4Timer};
        this.speedUpdateCallback = speedUpdateCallback;
    }
    
    public void resetSlowMode() {
        if (slowModeTimeline != null) {
            slowModeTimeline.stop();
            slowModeTimeline = null;
        }
        slowModeActive = false;
        slowModeSecondsRemaining = 0;
        updateSlowTimerLabel();
    }
    
    private void updateSlowTimerLabel() {
        for (Label lbl : timerLabels) {
            if (lbl != null) {
                lbl.setText("");
            }
        }
        if (slowAbilitySlotIndex < 0 || slowAbilitySlotIndex >= timerLabels.length) {
            return;
        }
        Label target = timerLabels[slowAbilitySlotIndex];
        if (target == null) {
            return;
        }
        if (slowModeActive && slowModeSecondsRemaining > 0) {
            target.setText(slowModeSecondsRemaining + "s");
        } else {
            target.setText("Inactive");
        }
    }
    
    public void activateSlowTime(int durationSeconds) {
        Platform.runLater(() -> startSlowModeEffect(durationSeconds));
    }
    
    private void startSlowModeEffect(int durationSeconds) {
        if (durationSeconds <= 0) {
            return;
        }
        if (!slowModeActive) {
            slowModeActive = true;
            long slowSpeed = Math.min(normalDropSpeedMs * 2, normalDropSpeedMs + 400);
            speedUpdateCallback.updateDropSpeed(slowSpeed, false);
        } else if (slowModeTimeline != null) {
            slowModeTimeline.stop();
        }
        slowModeSecondsRemaining = durationSeconds;
        updateSlowTimerLabel();
        slowModeTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            slowModeSecondsRemaining--;
            updateSlowTimerLabel();
            if (slowModeSecondsRemaining <= 0) {
                endSlowModeEffect();
            }
        }));
        slowModeTimeline.setCycleCount(durationSeconds);
        slowModeTimeline.play();
    }
    
    private void endSlowModeEffect() {
        if (slowModeTimeline != null) {
            slowModeTimeline.stop();
            slowModeTimeline = null;
        }
        slowModeActive = false;
        slowModeSecondsRemaining = 0;
        updateSlowTimerLabel();
        speedUpdateCallback.updateDropSpeed(normalDropSpeedMs, false);
    }
    
    public void setNormalDropSpeedMs(long normalDropSpeedMs) {
        this.normalDropSpeedMs = normalDropSpeedMs;
    }
    
    public void setSlowAbilitySlotIndex(int slowAbilitySlotIndex) {
        this.slowAbilitySlotIndex = slowAbilitySlotIndex;
        updateSlowTimerLabel();
    }
    
    public boolean isSlowModeActive() {
        return slowModeActive;
    }
}

