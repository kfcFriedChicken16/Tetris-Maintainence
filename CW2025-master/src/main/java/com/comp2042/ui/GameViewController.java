package com.comp2042.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import com.comp2042.events.InputEventListener;
import com.comp2042.modes.GameMode;
import com.comp2042.models.ViewData;
import com.comp2042.models.DownData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.comp2042.managers.ScoreManager;
import com.comp2042.managers.AudioManager;
import com.comp2042.managers.VideoManager;
import com.comp2042.ui.panels.GameOverPanel;
import com.comp2042.ui.panels.PausePanel;
import com.comp2042.ui.panels.NotificationPanel;
import com.comp2042.core.GameStateManager;
import com.comp2042.core.GameController;
import com.comp2042.events.MoveEvent;
import com.comp2042.menu.MenuController;

import java.net.URL;
import java.util.ResourceBundle;

public class GameViewController implements Initializable {

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane ghostPanel;

    @FXML
    private GameOverPanel gameOverPanel;
    
    @FXML
    private PausePanel pausePanel;
    
    @FXML
    private Group pauseGroup;
    
    @FXML
    private javafx.scene.layout.StackPane levelUpGroup;
    
    @FXML
    private Button clearBottomBtn;
    @FXML
    private Button slowTimeBtn;
    @FXML
    private Button colorBombBtn;
    @FXML
    private Button colorSyncBtn;

    @FXML
    private BorderPane gameBoard;

    @FXML
    private Button backToMenuBtn;

    @FXML
    private javafx.scene.control.Label scoreLabel;
    
    @FXML
    private javafx.scene.control.Label highestScoreLabel;
    
    // Sprint mode UI elements
    @FXML
    private javafx.scene.layout.VBox sprintModeDisplay;
    @FXML
    private javafx.scene.control.Label sprintLinesLabel;
    @FXML
    private javafx.scene.control.Label sprintTimerLabel;
    @FXML
    private javafx.scene.control.Label sprintBestTimeLabel;
    
    // Ultra mode UI elements
    @FXML
    private javafx.scene.layout.VBox ultraModeDisplay;
    @FXML
    private javafx.scene.control.Label ultraTimerLabel;
    @FXML
    private javafx.scene.control.Label ultraSpeedLevelLabel;
    @FXML
    private javafx.scene.control.Label ultraBestScoreLabel;
    
    // Survival mode UI elements
    @FXML
    private javafx.scene.layout.VBox survivalModeDisplay;
    @FXML
    private javafx.scene.control.Label survivalSpeedLevelLabel;
    @FXML
    private javafx.scene.control.Label survivalNextThresholdLabel;
    @FXML
    private javafx.scene.control.Label survivalHighestLevelLabel;
    
    // RPG mode UI elements
    @FXML
    private javafx.scene.layout.VBox rpgModeDisplay;
    @FXML
    private javafx.scene.control.Label rpgLinesClearedLabel;
    @FXML
    private javafx.scene.control.Label rpgCurrentLevelLabel;
    @FXML
    private javafx.scene.control.Label rpgNextLevelLabel;
    @FXML
    private javafx.scene.control.Label abilitySlot1Label;
    @FXML
    private javafx.scene.control.Label abilitySlot1Timer;
    @FXML
    private javafx.scene.control.Label abilitySlot2Label;
    @FXML
    private javafx.scene.control.Label abilitySlot2Timer;
    @FXML
    private javafx.scene.control.Label abilitySlot3Label;
    @FXML
    private javafx.scene.control.Label abilitySlot3Timer;
    @FXML
    private javafx.scene.control.Label abilitySlot4Label;
    @FXML
    private javafx.scene.control.Label abilitySlot4Timer;

    @FXML
    private javafx.scene.layout.VBox nextPiecesContainer;
    
    @FXML
    private javafx.scene.layout.VBox nextPanelVBox; // The container VBox from FXML
    
    @FXML
    private javafx.scene.layout.VBox holdPieceContainer;
    
    @FXML
    private javafx.scene.layout.VBox holdPanelVBox; // The container VBox for hold panel
    
    @FXML
    private MediaView gameplayBackgroundVideo;
    
    @FXML
    private Region gameplayVideoOverlay;

    private InputEventListener eventListener;

    // Game state management - handles pause and game over state
    private GameStateManager gameStateManager;
    
    // Score management - handles all score persistence
    private ScoreManager scoreManager;
    
    // Audio management - handles all sound effects and background music
    private AudioManager audioManager;
    
    // Video management - handles background video and vignette effects
    private VideoManager videoManager;
    
    // Current game mode
    private GameMode currentGameMode = GameMode.CLASSIC;

    // Extracted components
    private GameUIRenderer uiRenderer;
    private GameInputHandler inputHandler;
    private GameAnimationManager animationManager;
    private GameModeManager modeManager;
    
    // Slow time ability tracking
    private Timeline slowModeTimeline;
    private boolean slowModeActive = false;
    private int slowModeSecondsRemaining = 0;
    private long normalDropSpeedMs = 400;
    private int slowAbilitySlotIndex = -1;
    
    // Random ability selection for level-up
    private String[] currentLevelUpAbilities = new String[3]; // Stores the 3 random abilities shown

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        
        // Initialize score manager
        scoreManager = new ScoreManager();
        scoreManager.setLabels(highestScoreLabel, sprintBestTimeLabel, 
                              ultraBestScoreLabel, survivalHighestLevelLabel);
        
        // Initialize audio manager
        audioManager = new AudioManager();
        
        // Initialize video manager
        videoManager = new VideoManager(gameplayBackgroundVideo, gameplayVideoOverlay);
        videoManager.initializeBackgroundVideo();
        videoManager.setupVignetteEffect();
        
        // Initialize game state manager
        gameStateManager = new GameStateManager();
        
        // Initialize extracted components
        uiRenderer = new GameUIRenderer(gamePanel, brickPanel, ghostPanel, nextPiecesContainer, holdPieceContainer);
        
        inputHandler = new GameInputHandler(gameStateManager);
        inputHandler.setGameActionCallback(new GameInputHandler.GameActionCallback() {
            @Override
            public void newGame() {
                GameViewController.this.newGame(null);
            }
            
            @Override
            public void backToMenu() {
                GameViewController.this.backToMenu(null);
            }
            
            @Override
            public void togglePause() {
                GameViewController.this.togglePause();
            }
            
            @Override
            public void moveDown(MoveEvent event) {
                GameViewController.this.moveDown(event);
            }
            
            @Override
            public void hardDrop(MoveEvent event) {
                GameViewController.this.hardDrop(event);
            }
            
            @Override
            public ViewData refreshBrick(ViewData viewData) {
                GameViewController.this.refreshBrick(viewData);
                return viewData;
            }
            
            @Override
            public void selectAbility(String abilityType) {
                // Forward to GameController
                if (eventListener instanceof GameController) {
                    ((GameController) eventListener).selectAbility(abilityType);
                }
                hideLevelUpPopup();
            }
            
            @Override
            public void selectAbility(int shortcut) {
                // Get the ability type for this keyboard shortcut
                String abilityType = getAbilityForShortcut(shortcut);
                if (abilityType != null) {
                    selectAbility(abilityType);
                }
            }
            
            @Override
            public boolean isLevelUpPopupVisible() {
                return levelUpGroup != null && levelUpGroup.isVisible();
            }
        });
        
        animationManager = new GameAnimationManager();
        animationManager.setMoveDownCallback(this::moveDown);
        
        modeManager = new GameModeManager(scoreManager, audioManager, gameStateManager);
        modeManager.setLabels(sprintLinesLabel, sprintTimerLabel, ultraTimerLabel, 
                             ultraSpeedLevelLabel, survivalSpeedLevelLabel, survivalNextThresholdLabel);
        modeManager.setGameModeCallback(new GameModeManager.GameModeCallback() {
            @Override
            public void ultraComplete() {
                GameViewController.this.ultraComplete();
            }
            
            @Override
            public void sprintComplete() {
                GameViewController.this.sprintComplete();
            }
            
            @Override
            public void createAndStartGameTimeline(long speed) {
                GameViewController.this.updateDropSpeed(speed, true);
            }
            
            @Override
            public void centerGameOverPanel(Pane root) {
                GameViewController.this.centerGameOverPanel(root);
            }
            
            @Override
            public void stopTimeline() {
                animationManager.stopTimeline();
            }
        });
        
        gamePanel.setFocusTraversable(true);
        resetSlowMode();
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(this::handleKeyPressed);
        gameOverPanel.setVisible(false);
        
        // Initialize pause panel
        if (pausePanel != null && pauseGroup != null) {
            pauseGroup.setVisible(false);
            pauseGroup.setManaged(false);
            
            // Set up button actions
            pausePanel.getContinueButton().setOnAction(e -> continueGame(null));
            pausePanel.getRestartButton().setOnAction(e -> restartFromPause(null));
            pausePanel.getQuitButton().setOnAction(e -> quitToMainMenuFromPause(null));
        }

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);

        // Center the entire game cluster without changing FXML structure
        centerGameCluster();
    }

    private void centerGameCluster() {
        Parent parent = gameBoard.getParent();
        if (!(parent instanceof Pane)) return;
        Pane root = (Pane) parent;

        // Move the 3 nodes as a single unit (gameBoard, brickPanel, ghostPanel)
        // groupNotification and pauseGroup are handled separately to center on entire screen
        Group gameCluster = new Group(gameBoard, brickPanel, ghostPanel);
        gameCluster.setManaged(false);
        brickPanel.setManaged(false);
        ghostPanel.setManaged(false);
        groupNotification.setManaged(false);
        if (pauseGroup != null) {
            pauseGroup.setManaged(false);
        }

        // Remove nodes that need to be repositioned
        root.getChildren().removeAll(gameBoard, brickPanel, ghostPanel, groupNotification);
        if (pauseGroup != null && root.getChildren().contains(pauseGroup)) {
            root.getChildren().remove(pauseGroup);
        }
        
        root.getChildren().add(gameCluster);
        root.getChildren().add(groupNotification); // Add game over panel separately
        if (pauseGroup != null) {
            root.getChildren().add(pauseGroup); // Add pause panel separately
        }

        Platform.runLater(() -> {
            // 1) Freeze board size so it never changes with content
            double w = Math.ceil(gameBoard.getBoundsInParent().getWidth());
            double h = Math.ceil(gameBoard.getBoundsInParent().getHeight());
            if (w <= 0 || h <= 0) { // fallback to pref if CSS not applied yet
                w = gameBoard.prefWidth(-1);
                h = gameBoard.prefHeight(-1);
            }
            gameBoard.setPrefSize(w, h);
            gameBoard.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            gameBoard.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

            final double fw = w;
            final double fh = h;

            // 2) Recenter only using stable board size
            Runnable reposition = () -> {
                double x = (root.getWidth() - fw) * 0.5;
                double y = (root.getHeight() - fh) * 0.5 - 40; // nudge board slightly upward
                gameCluster.relocate(x, y);
                
                // Center game over panel on entire screen (not relative to game board)
                centerGameOverPanel(root);
                
                // Center pause panel on entire screen
                centerPausePanel(root);
                
                // Position HOLD panel relative to game board's left edge
                if (holdPanelVBox != null) {
                    double holdPanelX = x - 120; // 120px to the left of game board
                    double holdPanelY = y; // Align with top of game board
                    holdPanelVBox.setLayoutX(holdPanelX);
                    holdPanelVBox.setLayoutY(holdPanelY);
                }
                
                // Position NEXT panel relative to game board's right edge
                if (nextPanelVBox != null) {
                    double nextPanelX = x + fw + 20; // 20px gap from game board right edge
                    double nextPanelY = y; // Align with top of game board
                    nextPanelVBox.setLayoutX(nextPanelX);
                    nextPanelVBox.setLayoutY(nextPanelY);
                    
                    // Height will be set dynamically based on content in updateNextPiecesDisplay
                }
            };

            // Recenter on window size changes; do NOT listen to gameCluster bounds
            root.widthProperty().addListener((o, ov, nv) -> reposition.run());
            root.heightProperty().addListener((o, ov, nv) -> reposition.run());

            reposition.run();
        });
    }
    
    /**
     * Center the game over panel on the entire screen
     */
    private void centerGameOverPanel(Pane root) {
        if (groupNotification != null && root != null) {
            // Wait for layout to calculate bounds
            Platform.runLater(() -> {
                double panelWidth = groupNotification.getBoundsInLocal().getWidth();
                double panelHeight = groupNotification.getBoundsInLocal().getHeight();
                if (panelWidth <= 0 || panelHeight <= 0) {
                    // Use preferred size if bounds not calculated yet
                    panelWidth = 500; // Approximate width
                    panelHeight = 400; // Approximate height
                }
                double panelX = (root.getWidth() - panelWidth) * 0.5;
                double panelY = (root.getHeight() - panelHeight) * 0.5;
                groupNotification.relocate(panelX, panelY);
            });
        }
    }
    
    /**
     * Center the pause panel on the entire screen
     */
    private void centerPausePanel(Pane root) {
        if (pauseGroup != null && root != null) {
            // Wait for layout to calculate bounds
            Platform.runLater(() -> {
                double panelWidth = pauseGroup.getBoundsInLocal().getWidth();
                double panelHeight = pauseGroup.getBoundsInLocal().getHeight();
                if (panelWidth <= 0 || panelHeight <= 0) {
                    // Use preferred size if bounds not calculated yet
                    panelWidth = 500; // Approximate width
                    panelHeight = 400; // Approximate height
                }
                double panelX = (root.getWidth() - panelWidth) * 0.5;
                double panelY = (root.getHeight() - panelHeight) * 0.5;
                pauseGroup.relocate(panelX, panelY);
            });
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        uiRenderer.initGameView(boardMatrix, brick);

        // Create and start game timeline with appropriate speed for current mode
        long initialSpeed = animationManager.getInitialSpeedForMode(currentGameMode, 
                                                                   modeManager.getCurrentSpeedInterval(), 
                                                                   modeManager.getSurvivalSpeedInterval());
        resetSlowMode();
        updateDropSpeed(initialSpeed, true);
        
        // Start mode-specific timers
        if (currentGameMode == GameMode.SPRINT) {
            modeManager.startSprintTimer();
        } else if (currentGameMode == GameMode.ULTRA) {
            modeManager.startUltraTimer();
        }
    }

    private void refreshBrick(ViewData brick) {
        uiRenderer.refreshBrick(brick, gameStateManager.isPaused());
    }

    public void refreshGameBackground(int[][] board) {
        uiRenderer.refreshGameBackground(board);
    }
    
    private void updateDropSpeed(long speedMs, boolean treatAsNormal) {
        if (treatAsNormal) {
            normalDropSpeedMs = speedMs;
            if (slowModeActive) {
                return;
            }
        }
        animationManager.createAndStartGameTimeline(speedMs);
    }
    
    /**
     * Update game speed for RPG mode (called when leveling up)
     * @param speedMs new drop interval in milliseconds
     */
    public void updateRPGSpeed(long speedMs) {
        updateDropSpeed(speedMs, true);
    }
    
    private void resetSlowMode() {
        if (slowModeTimeline != null) {
            slowModeTimeline.stop();
            slowModeTimeline = null;
        }
        slowModeActive = false;
        slowModeSecondsRemaining = 0;
        updateSlowTimerLabel();
    }
    
    private void updateSlowTimerLabel() {
        Label[] timerLabels = new Label[]{abilitySlot1Timer, abilitySlot2Timer, abilitySlot3Timer, abilitySlot4Timer};
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
            updateDropSpeed(slowSpeed, false);
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
        updateDropSpeed(normalDropSpeedMs, false);
    }

    private void moveDown(MoveEvent event) {
        // Don't move if game is paused or game over
        if (!gameStateManager.isPaused() && !gameStateManager.isGameOver()) {
            // Additional check for Ultra mode: stop if time is up
            if (currentGameMode == GameMode.ULTRA && modeManager.getUltraStartTime() > 0) {
                long elapsed = System.currentTimeMillis() - modeManager.getUltraStartTime();
                if (elapsed >= GameModeManager.getUltraTimeLimit()) {
                    // Time's up, stop moving immediately
                    animationManager.stopTimeline();
                    gameStateManager.setGameOver(true);
                    Platform.runLater(() -> {
                        ultraComplete();
                    });
                    return;
                }
            }
            
            DownData downData = eventListener.onDownEvent(event);
            
            // Play sound when block lands (when clearRow is not null, it means block was merged)
            if (downData.getClearRow() != null) {
                audioManager.playBlockLandSound();
                
                if (downData.getClearRow().getLinesRemoved() > 0) {
                    // Play line clear success sound
                    audioManager.playLineClearSound();
                    
                    NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                    groupNotification.getChildren().add(notificationPanel);
                    notificationPanel.showScore(groupNotification.getChildren());
                    
                    // Update Sprint mode lines count is handled in GameController
                }
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    private void hardDrop(MoveEvent event) {
        if (!gameStateManager.isPaused()) {
            // Play hard drop sound immediately when space is pressed
            audioManager.playHardDropSound();
            
            DownData downData = eventListener.onHardDropEvent(event);
            
            // Also play land sound since block lands immediately after hard drop
            if (downData.getClearRow() != null) {
                audioManager.playBlockLandSound();
                
                if (downData.getClearRow().getLinesRemoved() > 0) {
                    // Play line clear success sound
                    audioManager.playLineClearSound();
                    
                    NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                    groupNotification.getChildren().add(notificationPanel);
                    notificationPanel.showScore(groupNotification.getChildren());
                }
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        inputHandler.setEventListener(eventListener);
    }

    public void bindScore(IntegerProperty integerProperty) {
        // Fix: Actually bind the score to the UI label
        if (scoreLabel != null) {
            scoreLabel.textProperty().bind(integerProperty.asString());
        }
        
        // Listen to score changes to update highest score
        integerProperty.addListener((obs, oldVal, newVal) -> {
            int currentScore = newVal.intValue();
            scoreManager.updateHighestScore(currentScore);
            
            // Check Survival mode speed increase (every 1500 points)
            if (currentGameMode == GameMode.SURVIVAL) {
                modeManager.checkSurvivalSpeedIncrease(currentScore, currentGameMode);
            }
        });
    }
    
    /**
     * Set the current game mode (called by GameController)
     */
    public void setGameMode(GameMode mode) {
        currentGameMode = mode;
        System.out.println("Game mode set to: " + mode.getDisplayName());
        
        // Show/hide mode-specific UI - use helper method to reduce repetition
        setModeDisplayVisibility(sprintModeDisplay, mode == GameMode.SPRINT);
        setModeDisplayVisibility(ultraModeDisplay, mode == GameMode.ULTRA);
        setModeDisplayVisibility(survivalModeDisplay, mode == GameMode.SURVIVAL);
        setModeDisplayVisibility(rpgModeDisplay, mode == GameMode.RPG);
    }
    
    /**
     * Helper method to set visibility and managed state for a mode display
     */
    private void setModeDisplayVisibility(javafx.scene.layout.VBox display, boolean visible) {
        if (display != null) {
            display.setVisible(visible);
            display.setManaged(visible);
        }
    }
    
    /**
     * Get the current game mode
     */
    public GameMode getGameMode() {
        return currentGameMode;
    }
    
    /**
     * Update Sprint lines count (called from GameController after lines are cleared)
     */
    public void updateSprintLines(int linesCleared) {
        modeManager.updateSprintLines(linesCleared);
    }
    
    /**
     * Update RPG mode display (called from GameController after lines are cleared)
     */
    public void updateRPGDisplay(int totalLinesCleared, int currentLevel, int linesToNextLevel,
                                 String slot1Text, String slot2Text, String slot3Text, String slot4Text,
                                 int slowAbilitySlotIndex) {
        if (rpgLinesClearedLabel != null) {
            rpgLinesClearedLabel.setText(String.valueOf(totalLinesCleared));
        }
        if (rpgCurrentLevelLabel != null) {
            rpgCurrentLevelLabel.setText(String.valueOf(currentLevel));
        }
        if (rpgNextLevelLabel != null) {
            rpgNextLevelLabel.setText(linesToNextLevel + " lines");
        }
        if (abilitySlot1Label != null) {
            abilitySlot1Label.setText(slot1Text);
        }
        if (abilitySlot2Label != null) {
            abilitySlot2Label.setText(slot2Text);
        }
        if (abilitySlot3Label != null) {
            abilitySlot3Label.setText(slot3Text);
        }
        if (abilitySlot4Label != null) {
            abilitySlot4Label.setText(slot4Text);
        }
        this.slowAbilitySlotIndex = slowAbilitySlotIndex;
        updateSlowTimerLabel();
    }
    
    /**
     * Show the level-up popup for ability selection with 3 random abilities
     */
    public void showLevelUpPopup() {
        System.out.println("showLevelUpPopup() called");
        if (levelUpGroup != null) {
            // Randomly select 3 abilities from the 4 available
            String[] allAbilities = {"CLEAR_BOTTOM_3", "SLOW_TIME", "COLOR_BOMB", "COLOR_SYNC"};
            List<String> abilityList = new ArrayList<>(Arrays.asList(allAbilities));
            Collections.shuffle(abilityList);
            
            // Take first 3 abilities
            currentLevelUpAbilities[0] = abilityList.get(0);
            currentLevelUpAbilities[1] = abilityList.get(1);
            currentLevelUpAbilities[2] = abilityList.get(2);
            
            System.out.println("Random abilities selected: " + 
                currentLevelUpAbilities[0] + ", " + 
                currentLevelUpAbilities[1] + ", " + 
                currentLevelUpAbilities[2]);
            
            // Update button visibility and labels
            updateAbilityButtons();
            
            levelUpGroup.setVisible(true);
            levelUpGroup.setManaged(true);
            // Pause the game when level-up popup appears
            gameStateManager.setPaused(true);
            System.out.println("Level-up popup should now be visible!");
        } else {
            System.out.println("ERROR: levelUpGroup is null!");
        }
    }
    
    /**
     * Update ability buttons to show only the 3 randomly selected abilities
     * Buttons are always labeled [1], [2], [3] in sequential order from top to bottom
     * The abilities themselves are randomly selected, but displayed in sequential order
     */
    private void updateAbilityButtons() {
        // Hide all buttons first
        if (clearBottomBtn != null) clearBottomBtn.setVisible(false);
        if (slowTimeBtn != null) slowTimeBtn.setVisible(false);
        if (colorBombBtn != null) colorBombBtn.setVisible(false);
        if (colorSyncBtn != null) colorSyncBtn.setVisible(false);
        
        // Buttons in display order (top to bottom) - use first 3 buttons from FXML
        Button[] displayButtons = {clearBottomBtn, slowTimeBtn, colorBombBtn};
        
        // Assign the 3 randomly selected abilities to buttons in sequential order [1], [2], [3]
        for (int i = 0; i < 3; i++) {
            String ability = currentLevelUpAbilities[i];
            Button button = displayButtons[i]; // Use buttons in FXML order
            int buttonNumber = i + 1; // Always sequential: 1, 2, 3
            
            // Set the button text based on ability type
            String buttonText = "";
            switch (ability) {
                case "CLEAR_BOTTOM_3":
                    buttonText = "[" + buttonNumber + "] Clear Bottom 3 Rows";
                    break;
                case "SLOW_TIME":
                    buttonText = "[" + buttonNumber + "] Slow Time (10s)";
                    break;
                case "COLOR_BOMB":
                    buttonText = "[" + buttonNumber + "] Color Bomb (clear matching color)";
                    break;
                case "COLOR_SYNC":
                    buttonText = "[" + buttonNumber + "] Color Sync (combo setup)";
                    break;
            }
            
            if (button != null) {
                button.setVisible(true);
                button.setText(buttonText);
                
                // Update the button's onAction to call the correct ability
                // We need to update the action handler dynamically
                button.setOnAction(e -> {
                    if (eventListener instanceof GameController) {
                        ((GameController) eventListener).selectAbility(ability);
                    }
                    hideLevelUpPopup();
                });
            }
        }
    }
    
    /**
     * Get the ability type for a given keyboard shortcut (1, 2, or 3)
     */
    public String getAbilityForShortcut(int shortcut) {
        if (shortcut >= 1 && shortcut <= 3) {
            return currentLevelUpAbilities[shortcut - 1];
        }
        return null;
    }
    
    /**
     * Hide the level-up popup and resume game
     */
    public void hideLevelUpPopup() {
        if (levelUpGroup != null) {
            levelUpGroup.setVisible(false);
            levelUpGroup.setManaged(false);
            // Resume the game
            gameStateManager.setPaused(false);
        }
    }
    
    /**
     * Handle Clear Bottom ability selection
     */
    @FXML
    private void selectClearBottom() {
        // Notify GameController of ability selection
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).selectAbility("CLEAR_BOTTOM_3");
        }
        hideLevelUpPopup();
    }
    
    /**
     * Handle Slow Time ability selection
     */
    @FXML
    private void selectSlowTime() {
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).selectAbility("SLOW_TIME");
        }
        hideLevelUpPopup();
    }
    
    /**
     * Handle Color Bomb ability selection
     */
    @FXML
    private void selectColorBomb() {
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).selectAbility("COLOR_BOMB");
        }
        hideLevelUpPopup();
    }
    
    /**
     * Handle Color Sync ability selection
     */
    @FXML
    private void selectColorSync() {
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).selectAbility("COLOR_SYNC");
        }
        hideLevelUpPopup();
    }
    
    /**
     * Sprint mode completed - show completion screen
     */
    public void sprintComplete() {
        modeManager.sprintComplete(gameOverPanel, gameBoard);
        gamePanel.requestFocus();
    }
    
    /**
     * Ultra mode completed - time's up!
     */
    public void ultraComplete() {
        modeManager.ultraComplete(gameOverPanel, gameBoard, scoreLabel);
        gamePanel.requestFocus();
    }
    
    /**
     * RPG mode completed - player reached level 40!
     */
    public void rpgComplete(int totalLinesCleared, int finalLevel) {
        // Stop game timeline
        animationManager.stopTimeline();
        
        // Set game over state
        gameStateManager.setGameOver(true);
        
        // Show congratulations message
        gameOverPanel.setVisible(true);
        gameOverPanel.setGameOverMessage("HOORAY!");
        
        // Show completion info
        String infoText = "You successfully completed RPG Mode!";
        String statsText = "Final Level: " + finalLevel + " | Lines Cleared: " + totalLinesCleared;
        gameOverPanel.setTimeInfo(infoText, statsText);
        
        // Center the game over panel on screen
        Parent parent = gameBoard.getParent();
        if (parent instanceof Pane) {
            centerGameOverPanel((Pane) parent);
        }
        
        // Play completion sound
        audioManager.playLineClearSound();
        
        // Ensure focus is on gamePanel
        Platform.runLater(() -> {
            gamePanel.requestFocus();
        });
    }

    public void gameOver() {
        // Stop Ultra timer if in Ultra mode
        modeManager.stopUltraTimerIfActive(currentGameMode);
        
        // Lower background music volume instead of stopping (hybrid approach)
        audioManager.setBackgroundMusicVolume(0.15); // Lower to 15% volume
        
        // Play game over sound
        audioManager.playGameOverSound();
        
        animationManager.stopTimeline();
        gameOverPanel.setVisible(true);
        gameOverPanel.resetToDefault(); // Reset to default game over display
        gameStateManager.setGameOver(true);
        
        // Center the game over panel on screen
        Parent parent = gameBoard.getParent();
        if (parent instanceof Pane) {
            centerGameOverPanel((Pane) parent);
        }
        
        // Ensure focus is on gamePanel so keyboard controls work for game over
        Platform.runLater(() -> {
            gamePanel.requestFocus();
        });
    }

    public void newGame(ActionEvent actionEvent) {
        animationManager.stopTimeline();
        gameOverPanel.setVisible(false);
        gameOverPanel.resetToDefault(); // Reset game over panel to default state
        
        // Restore background music to normal volume when starting new game
        audioManager.setBackgroundMusicVolume(0.4); // Restore to 40% volume
        
        // Reset mode-specific variables
        modeManager.resetForNewGame(currentGameMode);
        
        eventListener.createNewGame();
        gamePanel.requestFocus();
        
        // Reset timeline with correct speed for mode
        long initialSpeed = animationManager.getInitialSpeedForMode(currentGameMode, 
                                                                   modeManager.getCurrentSpeedInterval(), 
                                                                   modeManager.getSurvivalSpeedInterval());
        resetSlowMode();
        updateDropSpeed(initialSpeed, true);
        
        gameStateManager.reset();
        
        // Restart mode-specific timers
        if (currentGameMode == GameMode.SPRINT) {
            modeManager.startSprintTimer();
        } else if (currentGameMode == GameMode.ULTRA) {
            modeManager.startUltraTimer();
        } else if (currentGameMode == GameMode.SURVIVAL) {
            modeManager.initializeSurvivalMode();
        }
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
    
    /**
     * Handle key press events for game controls
     */
    private void handleKeyPressed(KeyEvent keyEvent) {
        inputHandler.handleKeyPressed(keyEvent);
    }
    
    /**
     * Toggle pause state
     */
    private void togglePause() {
        if (!gameStateManager.isPaused()) {
            // Pause the game
            pauseGameInternal();
        } else {
            // Resume the game
            continueGame(null);
        }
    }
    
    /**
     * Pause the game (internal method)
     */
    private void pauseGameInternal() {
        if (gameStateManager.isGameOver()) {
            return; // Don't pause if game is over
        }
        
        gameStateManager.setPaused(true);
        
        // Stop game timeline
        animationManager.pauseTimeline();
        
        // Pause mode-specific timers
        modeManager.pauseTimers(currentGameMode);
        
        // Show pause panel
        if (pauseGroup != null) {
            pauseGroup.setVisible(true);
            pauseGroup.setManaged(true);
            
            // Center the pause panel
            Parent parent = gameBoard.getParent();
            if (parent instanceof Pane) {
                centerPausePanel((Pane) parent);
            }
        }
        
        gamePanel.requestFocus();
    }
    
    /**
     * Continue the game from pause
     */
    @FXML
    public void continueGame(ActionEvent actionEvent) {
        if (gameStateManager.isPaused()) {
            gameStateManager.setPaused(false);
            
            // Resume game timeline
            animationManager.playTimeline();
            
            // Resume mode-specific timers
            modeManager.resumeTimers(currentGameMode);
            
            // Hide pause panel
            if (pauseGroup != null) {
                pauseGroup.setVisible(false);
                pauseGroup.setManaged(false);
            }
            
            gamePanel.requestFocus();
        }
    }
    
    /**
     * Restart the game from pause menu
     */
    @FXML
    public void restartFromPause(ActionEvent actionEvent) {
        // Hide pause panel first
        if (pauseGroup != null) {
            pauseGroup.setVisible(false);
            pauseGroup.setManaged(false);
        }
        
        // Reset pause state
        gameStateManager.setPaused(false);
        
        // Restart the game
        newGame(null);
    }
    
    /**
     * Quit to main menu from pause menu
     */
    @FXML
    public void quitToMainMenuFromPause(ActionEvent actionEvent) {
        // Reset pause state
        gameStateManager.setPaused(false);
        
        // Hide pause panel
        if (pauseGroup != null) {
            pauseGroup.setVisible(false);
            pauseGroup.setManaged(false);
        }
        
        // Go back to main menu
        backToMenu(null);
    }

    /**
     * Handle Back to Menu button click - Return to the main menu
     * Can be called from button click or keyboard (actionEvent may be null)
     */
    @FXML
    public void backToMenu(ActionEvent actionEvent) {
        try {
            // Stop the game timeline
            animationManager.stopTimeline();

            // Load the main menu
            URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent menuRoot = fxmlLoader.load();
            
            // Get the menu controller
            MenuController menuController = fxmlLoader.getController();
            
            // Get current stage and switch back to menu
            // Can get stage from scene (works for both button click and keyboard)
            Stage stage = null;
            if (backToMenuBtn != null && backToMenuBtn.getScene() != null) {
                stage = (Stage) backToMenuBtn.getScene().getWindow();
            } else if (gamePanel != null && gamePanel.getScene() != null) {
                stage = (Stage) gamePanel.getScene().getWindow();
            }
            
            if (stage == null) {
                System.out.println("✗ Cannot get stage - cannot return to menu");
                return;
            }
            Scene menuScene = new Scene(menuRoot, 1200, 800); // Larger size for full screen
            stage.setScene(menuScene);
            stage.setTitle("Tetris - Enhanced Edition");
            
            // Maintain full screen mode
            stage.setFullScreen(true);
            stage.setResizable(false); // Prevent window manipulation
            stage.setFullScreenExitKeyCombination(null);
            stage.setFullScreenExitHint("");
            
            // Add fullscreen enforcement listener
            enforceFullscreenMode(stage);
            
            // Stop background video
            if (videoManager != null) {
                videoManager.dispose();
            }
            
            // Stop and dispose audio resources
            if (audioManager != null) {
                audioManager.dispose();
            }
            
            // Set the primary stage reference for the menu controller
            menuController.setPrimaryStage(stage);
            
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
    
    /**
     * Play combo sound effect (for Color Bomb ability)
     */
    public void playComboSound() {
        if (audioManager != null) {
            audioManager.playComboSound();
        }
    }
    
    /**
     * Play color sync sound effect (for Color Sync ability)
     */
    public void playColorSyncSound() {
        if (audioManager != null) {
            audioManager.playColorSyncSound();
        }
    }
    
    /**
     * Play clear rows sound effect (for Clear Bottom 3 Rows ability)
     */
    public void playClearRowsSound() {
        if (audioManager != null) {
            audioManager.playClearRowsSound();
        }
    }
}