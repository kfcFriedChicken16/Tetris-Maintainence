package com.comp2042.ui;

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
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.media.MediaView;

import com.comp2042.events.InputEventListener;
import com.comp2042.modes.GameMode;
import com.comp2042.models.ViewData;
import com.comp2042.models.DownData;
import com.comp2042.managers.ScoreManager;
import com.comp2042.managers.AudioManager;
import com.comp2042.managers.VideoManager;
import com.comp2042.ui.panels.GameOverPanel;
import com.comp2042.ui.panels.PausePanel;
import com.comp2042.ui.panels.NotificationPanel;
import com.comp2042.core.GameStateManager;
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
                animationManager.createAndStartGameTimeline(speed);
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
        animationManager.createAndStartGameTimeline(initialSpeed);
        
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
        animationManager.createAndStartGameTimeline(initialSpeed);
        
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
}