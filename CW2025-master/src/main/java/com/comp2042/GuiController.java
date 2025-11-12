package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

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

    private MediaPlayer gameplayVideoPlayer;
    
    // Sound effects
    private MediaPlayer blockLandSound;
    private MediaPlayer hardDropSound;
    private MediaPlayer lineClearSound;
    private MediaPlayer gameOverSound;
    
    // Background music
    private MediaPlayer gameplayBackgroundMusic;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Rectangle[][] ghostRectangles;

    private Timeline timeLine;
    
    // Sprint mode timer
    private Timeline sprintTimer;
    private long sprintStartTime = 0;
    private static long sprintBestTime = Long.MAX_VALUE; // Best time in milliseconds
    private static final String SPRINT_BEST_TIME_FILE = "sprint_best_time.txt";
    
    // Ultra mode variables
    private Timeline ultraTimer;
    private long ultraStartTime = 0;
    private int ultraSpeedLevel = 1; // Current speed level (starts at 1)
    private long currentSpeedInterval = 400; // Current speed in milliseconds (starts at 400ms)
    private static int ultraBestScore = 0; // Best score achieved in Ultra mode
    private static final String ULTRA_BEST_SCORE_FILE = "ultra_best_score.txt";
    private static final long ULTRA_TIME_LIMIT = 120000; // 2 minutes in milliseconds
    private static final long ULTRA_SPEED_INCREASE_INTERVAL = 20000; // 20 seconds in milliseconds
    
    // Survival mode variables
    private int survivalSpeedLevel = 1; // Current speed level (starts at 1)
    private long survivalSpeedInterval = 400; // Current speed in milliseconds (starts at 400ms)
    private int survivalNextThreshold = 1500; // Next score threshold for speed increase
    private static int survivalHighestLevel = 1; // Highest level reached in Survival mode
    private static final String SURVIVAL_HIGHEST_LEVEL_FILE = "survival_highest_level.txt";
    private static final int SURVIVAL_SPEED_INCREASE_THRESHOLD = 1500; // Points needed for each speed increase

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    
    // Highest score (persists across games)
    private static int highestScore = 0;
    private static final String HIGHEST_SCORE_FILE = "highest_score.txt";
    
    // Current game mode
    private GameMode currentGameMode = GameMode.CLASSIC;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        
        // Load highest score from file on startup
        loadHighestScore();
        
        // Initialize background video
        initializeGameplayBackgroundVideo();
        
        // Setup vignette effect (bright center, darker edges)
        setupVignetteEffect();
        
        // Initialize sound effects
        initializeSoundEffects();
        
        // Initialize and start background music
        initializeGameplayBackgroundMusic();
        
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                SettingsManager settings = SettingsManager.getInstance();
                KeyCode keyCode = keyEvent.getCode();
                
                // Game over controls - check FIRST before normal game controls
                if (isGameOver.getValue() == Boolean.TRUE) {
                    if (keyCode == KeyCode.SPACE) {
                        // Press Space to restart game
                        newGame(null);
                        keyEvent.consume();
                        return;
                    } else if (keyCode == settings.getPause()) {
                        // Press pause key to return to main menu
                        backToMenu(null);
                        keyEvent.consume();
                        return;
                    }
                }
                
                // Pause key toggles pause (only when not game over)
                if (keyCode == settings.getPause() && isGameOver.getValue() == Boolean.FALSE) {
                    togglePause();
                    keyEvent.consume();
                    return;
                }
                
                // Normal game controls (only when not paused and not game over)
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    // Move left
                    if (keyCode == settings.getMoveLeft() || keyCode == settings.getMoveLeftAlt()) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    // Move right
                    if (keyCode == settings.getMoveRight() || keyCode == settings.getMoveRightAlt()) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    // Rotate
                    if (keyCode == settings.getRotate() || keyCode == settings.getRotateAlt()) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    // Move down
                    if (keyCode == settings.getMoveDown() || keyCode == settings.getMoveDownAlt()) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    // Hard drop
                    if (keyCode == settings.getHardDrop()) {
                        hardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
                        keyEvent.consume();
                    }
                    // Hold piece
                    if (keyCode == settings.getHold() || keyCode == settings.getHoldAlt()) {
                        ViewData newViewData = eventListener.onHoldEvent();
                        refreshBrick(newViewData);
                        keyEvent.consume();
                    }
                }
                
                // Restart key (for quick restart during gameplay)
                if (keyCode == settings.getRestart() && isGameOver.getValue() == Boolean.FALSE) {
                    newGame(null);
                    keyEvent.consume();
                }
            }
        });
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
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        // Reduce the historical top offset so pieces spawn fully inside the frame
        brickPanel.setLayoutY(-18 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        // Initialize ghost piece with smooth appearance
        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle ghostRect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                ghostRect.setFill(Color.TRANSPARENT);
                ghostRect.setStroke(Color.TRANSPARENT);
                ghostRect.setStrokeWidth(0);
                ghostRect.setArcHeight(9); // Match main piece styling
                ghostRect.setArcWidth(9);
                ghostRectangles[i][j] = ghostRect;
                ghostPanel.add(ghostRect, j, i);
            }
        }
        updateGhostPiece(brick);
        
        // Initialize next pieces display
        updateNextPiecesDisplay(brick);
        
        // Initialize held piece display
        updateHeldPieceDisplay(brick);


        // Determine initial speed based on game mode
        long initialSpeed = 400; // Default for Classic mode
        if (currentGameMode == GameMode.SPRINT) {
            initialSpeed = 400; // Sprint mode: fixed 400ms
        } else if (currentGameMode == GameMode.ULTRA) {
            initialSpeed = currentSpeedInterval; // Ultra mode: starts at 400ms
        } else if (currentGameMode == GameMode.SURVIVAL) {
            initialSpeed = survivalSpeedInterval; // Survival mode: starts at 400ms, increases with score
        }
        
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(initialSpeed),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
        
        // Start mode-specific timers
        if (currentGameMode == GameMode.SPRINT) {
            startSprintTimer();
        } else if (currentGameMode == GameMode.ULTRA) {
            startUltraTimer();
        }
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            // Update ghost piece
            updateGhostPiece(brick);
            // Update next pieces display
            updateNextPiecesDisplay(brick);
            // Update held piece display
            updateHeldPieceDisplay(brick);
        }
    }

    private void updateNextPiecesDisplay(ViewData brick) {
        if (nextPiecesContainer == null) return;
        
        // Clear existing next pieces
        nextPiecesContainer.getChildren().clear();
        
        // Add single white border style to container
        nextPiecesContainer.getStyleClass().clear();
        nextPiecesContainer.getStyleClass().add("next-pieces-container");
        
        java.util.List<int[][]> nextBricksList = brick.getNextBricksList();
        if (nextBricksList == null || nextBricksList.isEmpty()) {
            return;
        }
        
        // Find the maximum width and total height needed for all pieces
        int maxWidth = 0;
        int totalHeight = 0;
        int displayCount = Math.min(4, nextBricksList.size());
        for (int pieceIndex = 0; pieceIndex < displayCount; pieceIndex++) {
            int[][] pieceData = nextBricksList.get(pieceIndex);
            if (pieceData == null) continue;
            
            int minRow = Integer.MAX_VALUE, maxRow = -1;
            int minCol = Integer.MAX_VALUE, maxCol = -1;
            for (int i = 0; i < pieceData.length; i++) {
                for (int j = 0; j < pieceData[i].length; j++) {
                    if (pieceData[i][j] != 0) {
                        minRow = Math.min(minRow, i);
                        maxRow = Math.max(maxRow, i);
                        minCol = Math.min(minCol, j);
                        maxCol = Math.max(maxCol, j);
                    }
                }
            }
            if (maxCol >= minCol) {
                int pieceWidth = (maxCol - minCol + 1) * BRICK_SIZE + (maxCol - minCol) * 2; // blocks + gaps
                maxWidth = Math.max(maxWidth, pieceWidth);
            }
            if (maxRow >= minRow) {
                int pieceHeight = (maxRow - minRow + 1) * BRICK_SIZE + (maxRow - minRow) * 2; // blocks + gaps
                totalHeight += pieceHeight;
                if (pieceIndex < displayCount - 1) {
                    totalHeight += 15; // spacing between pieces
                }
            }
        }
        
        // Set container width to fit content (max block width + padding) - make it wider
        if (maxWidth > 0) {
            int extraWidth = 50; // Add extra width to make box fatter
            nextPiecesContainer.setPrefWidth(maxWidth + extraWidth);
            nextPiecesContainer.setMinWidth(maxWidth + extraWidth);
            nextPiecesContainer.setMaxWidth(maxWidth + extraWidth);
        }
        
        // Set container height to fit content (total block height + padding)
        if (totalHeight > 0) {
            int extraHeight = 20; // Add extra height to make box taller
            nextPiecesContainer.setPrefHeight(totalHeight + extraHeight); // 3px padding top and bottom
            nextPiecesContainer.setMinHeight(totalHeight + extraHeight);
            nextPiecesContainer.setMaxHeight(totalHeight + extraHeight);
        }
        
        // Display up to 4 next pieces
        for (int pieceIndex = 0; pieceIndex < displayCount; pieceIndex++) {
            int[][] pieceData = nextBricksList.get(pieceIndex);
            if (pieceData == null) continue;
            
            GridPane pieceGrid = new GridPane();
            pieceGrid.setHgap(2);
            pieceGrid.setVgap(2);
            pieceGrid.setAlignment(javafx.geometry.Pos.CENTER); // Center each piece grid
            
            // Find the actual bounds of the piece (non-zero cells)
            int minRow = Integer.MAX_VALUE, maxRow = -1;
            int minCol = Integer.MAX_VALUE, maxCol = -1;
            boolean hasCells = false;
            
            for (int i = 0; i < pieceData.length; i++) {
                for (int j = 0; j < pieceData[i].length; j++) {
                    if (pieceData[i][j] != 0) {
                        hasCells = true;
                        minRow = Math.min(minRow, i);
                        maxRow = Math.max(maxRow, i);
                        minCol = Math.min(minCol, j);
                        maxCol = Math.max(maxCol, j);
                    }
                }
            }
            
            if (!hasCells) continue;
            
            // Create rectangles for this piece
            for (int i = minRow; i <= maxRow; i++) {
                for (int j = minCol; j <= maxCol; j++) {
                    if (i < pieceData.length && j < pieceData[i].length && pieceData[i][j] != 0) {
                        Rectangle rect = new Rectangle(BRICK_SIZE - 2, BRICK_SIZE - 2);
                        setRectangleData(pieceData[i][j], rect);
                        pieceGrid.add(rect, j - minCol, i - minRow);
                    }
                }
            }
            
            nextPiecesContainer.getChildren().add(pieceGrid);
        }
    }
    
    /**
     * Update the held piece display
     */
    private void updateHeldPieceDisplay(ViewData brick) {
        if (holdPieceContainer == null) return;
        
        // Clear existing held piece
        holdPieceContainer.getChildren().clear();
        
        // Get held piece data
        int[][] heldPieceData = brick.getHeldBrickData();
        
        if (heldPieceData == null) {
            // Nothing held - show empty container with border
            holdPieceContainer.getStyleClass().clear();
            holdPieceContainer.getStyleClass().add("next-pieces-container");
            return;
        }
        
        // Add single white border style to container
        holdPieceContainer.getStyleClass().clear();
        holdPieceContainer.getStyleClass().add("next-pieces-container");
        
        // Create grid for held piece
        GridPane pieceGrid = new GridPane();
        pieceGrid.setHgap(2);
        pieceGrid.setVgap(2);
        pieceGrid.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Find the actual bounds of the piece (non-zero cells)
        int minRow = Integer.MAX_VALUE, maxRow = -1;
        int minCol = Integer.MAX_VALUE, maxCol = -1;
        boolean hasCells = false;
        
        for (int i = 0; i < heldPieceData.length; i++) {
            for (int j = 0; j < heldPieceData[i].length; j++) {
                if (heldPieceData[i][j] != 0) {
                    hasCells = true;
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }
        
        if (!hasCells) return;
        
        // Create rectangles for this piece
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (i < heldPieceData.length && j < heldPieceData[i].length && heldPieceData[i][j] != 0) {
                    Rectangle rect = new Rectangle(BRICK_SIZE - 2, BRICK_SIZE - 2);
                    setRectangleData(heldPieceData[i][j], rect);
                    pieceGrid.add(rect, j - minCol, i - minRow);
                }
            }
        }
        
        holdPieceContainer.getChildren().add(pieceGrid);
        
        // Set container size to fit piece
        int pieceWidth = (maxCol - minCol + 1) * BRICK_SIZE + (maxCol - minCol) * 2;
        int pieceHeight = (maxRow - minRow + 1) * BRICK_SIZE + (maxRow - minRow) * 2;
        int extraWidth = 20;
        int extraHeight = 20;
        holdPieceContainer.setPrefWidth(pieceWidth + extraWidth);
        holdPieceContainer.setMinWidth(pieceWidth + extraWidth);
        holdPieceContainer.setMaxWidth(pieceWidth + extraWidth);
        holdPieceContainer.setPrefHeight(pieceHeight + extraHeight);
        holdPieceContainer.setMinHeight(pieceHeight + extraHeight);
        holdPieceContainer.setMaxHeight(pieceHeight + extraHeight);
    }

    private void updateGhostPiece(ViewData brick) {
        SettingsManager settings = SettingsManager.getInstance();
        if (!settings.isShowGhostPiece()) {
            // Hide ghost piece if disabled in settings
            if (ghostRectangles != null) {
                for (int i = 0; i < ghostRectangles.length; i++) {
                    for (int j = 0; j < ghostRectangles[i].length; j++) {
                        ghostRectangles[i][j].setFill(Color.TRANSPARENT);
                        ghostRectangles[i][j].setStroke(Color.TRANSPARENT);
                        ghostRectangles[i][j].setStrokeWidth(0);
                    }
                }
            }
            return;
        }
        
        if (brick.getGhostPosition() != null && ghostRectangles != null) {
            java.awt.Point ghostPos = brick.getGhostPosition();
            java.awt.Point currentPos = new java.awt.Point(brick.getxPosition(), brick.getyPosition());
            
            // Only show ghost piece if it's different from current position
            boolean showGhost = !ghostPos.equals(currentPos);
            
            if (showGhost) {
                // Position the ghost panel
                ghostPanel.setLayoutX(-1 + gamePanel.getLayoutX() + ghostPos.x * ghostPanel.getVgap() + ghostPos.x * BRICK_SIZE);
                // Match ghost with active piece vertical offset
                ghostPanel.setLayoutY(-45 + gamePanel.getLayoutY() + ghostPos.y * ghostPanel.getHgap() + ghostPos.y * BRICK_SIZE);
                
                // Update ghost rectangles with smoother appearance
                for (int i = 0; i < brick.getBrickData().length; i++) {
                    for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                        Rectangle ghostRect = ghostRectangles[i][j];
                        if (brick.getBrickData()[i][j] != 0) {
                            // Simple gray ghost piece with 70% transparency
                            Color ghostColor = new Color(0.5, 0.5, 0.5, 0.3); // Gray with 30% opacity (70% transparent)
                            ghostRect.setFill(ghostColor);
                            ghostRect.setStroke(Color.GRAY);
                            ghostRect.setStrokeWidth(1);
                            ghostRect.setOpacity(1.0);
                            ghostRect.setArcHeight(9); // Smooth rounded corners
                            ghostRect.setArcWidth(9);
                        } else {
                            // Hide empty parts completely
                            ghostRect.setFill(Color.TRANSPARENT);
                            ghostRect.setStroke(Color.TRANSPARENT);
                            ghostRect.setStrokeWidth(0);
                        }
                    }
                }
            } else {
                // Hide ghost piece when it overlaps with current piece
                for (int i = 0; i < ghostRectangles.length; i++) {
                    for (int j = 0; j < ghostRectangles[i].length; j++) {
                        ghostRectangles[i][j].setFill(Color.TRANSPARENT);
                        ghostRectangles[i][j].setStroke(Color.TRANSPARENT);
                        ghostRectangles[i][j].setStrokeWidth(0);
                    }
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9); // Smooth rounded corners
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
        // Don't move if game is paused or game over
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            // Additional check for Ultra mode: stop if time is up
            if (currentGameMode == GameMode.ULTRA && ultraStartTime > 0) {
                long elapsed = System.currentTimeMillis() - ultraStartTime;
                if (elapsed >= ULTRA_TIME_LIMIT) {
                    // Time's up, stop moving immediately
                    if (timeLine != null) {
                        timeLine.stop();
                    }
                    isGameOver.setValue(true);
                    Platform.runLater(() -> {
                        ultraComplete();
                    });
                    return;
                }
            }
            
            DownData downData = eventListener.onDownEvent(event);
            
            // Play sound when block lands (when clearRow is not null, it means block was merged)
            if (downData.getClearRow() != null) {
                playBlockLandSound();
                
                if (downData.getClearRow().getLinesRemoved() > 0) {
                    // Play line clear success sound
                    playLineClearSound();
                    
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
        if (isPause.getValue() == Boolean.FALSE) {
            // Play hard drop sound immediately when space is pressed
            playHardDropSound();
            
            DownData downData = eventListener.onHardDropEvent(event);
            
            // Also play land sound since block lands immediately after hard drop
            if (downData.getClearRow() != null) {
                playBlockLandSound();
                
                if (downData.getClearRow().getLinesRemoved() > 0) {
                    // Play line clear success sound
                    playLineClearSound();
                    
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
    }

    public void bindScore(IntegerProperty integerProperty) {
        // Fix: Actually bind the score to the UI label
        if (scoreLabel != null) {
            scoreLabel.textProperty().bind(integerProperty.asString());
        }
        
        // Listen to score changes to update highest score
        integerProperty.addListener((obs, oldVal, newVal) -> {
            int currentScore = newVal.intValue();
            if (currentScore > highestScore) {
                highestScore = currentScore;
                updateHighestScoreDisplay();
                saveHighestScore(); // Save to file whenever highest score is updated
            }
            
            // Check Survival mode speed increase (every 1500 points)
            if (currentGameMode == GameMode.SURVIVAL) {
                checkSurvivalSpeedIncrease(currentScore);
            }
        });
        
        // Initialize highest score display
        updateHighestScoreDisplay();
    }
    
    private void updateHighestScoreDisplay() {
        if (highestScoreLabel != null) {
            highestScoreLabel.setText(String.valueOf(highestScore));
        }
    }
    
    /**
     * Load highest score from file on application startup
     */
    private void loadHighestScore() {
        try {
            File scoreFile = new File(HIGHEST_SCORE_FILE);
            if (scoreFile.exists() && scoreFile.canRead()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        highestScore = Integer.parseInt(line.trim());
                    }
                }
            }
        } catch (Exception e) {
            // If file doesn't exist or can't be read, start with 0
            System.out.println("Could not load highest score: " + e.getMessage());
            highestScore = 0;
        }
        
        // Update display with loaded score
        Platform.runLater(() -> updateHighestScoreDisplay());
    }
    
    /**
     * Save highest score to file whenever it's updated
     */
    private void saveHighestScore() {
        try {
            File scoreFile = new File(HIGHEST_SCORE_FILE);
            try (PrintWriter writer = new PrintWriter(new FileWriter(scoreFile))) {
                writer.println(highestScore);
            }
        } catch (Exception e) {
            System.out.println("Could not save highest score: " + e.getMessage());
        }
    }
    
    /**
     * Set the current game mode (called by GameController)
     */
    public void setGameMode(GameMode mode) {
        currentGameMode = mode;
        System.out.println("Game mode set to: " + mode.getDisplayName());
        
        // Show/hide mode-specific UI
        if (mode == GameMode.SPRINT) {
            if (sprintModeDisplay != null) {
                sprintModeDisplay.setVisible(true);
                sprintModeDisplay.setManaged(true);
            }
            if (ultraModeDisplay != null) {
                ultraModeDisplay.setVisible(false);
                ultraModeDisplay.setManaged(false);
            }
            loadSprintBestTime();
            updateSprintBestTimeDisplay();
        } else if (mode == GameMode.ULTRA) {
            if (ultraModeDisplay != null) {
                ultraModeDisplay.setVisible(true);
                ultraModeDisplay.setManaged(true);
            }
            if (sprintModeDisplay != null) {
                sprintModeDisplay.setVisible(false);
                sprintModeDisplay.setManaged(false);
            }
            if (survivalModeDisplay != null) {
                survivalModeDisplay.setVisible(false);
                survivalModeDisplay.setManaged(false);
            }
            loadUltraBestScore();
        } else if (mode == GameMode.SURVIVAL) {
            if (survivalModeDisplay != null) {
                survivalModeDisplay.setVisible(true);
                survivalModeDisplay.setManaged(true);
            }
            if (sprintModeDisplay != null) {
                sprintModeDisplay.setVisible(false);
                sprintModeDisplay.setManaged(false);
            }
            if (ultraModeDisplay != null) {
                ultraModeDisplay.setVisible(false);
                ultraModeDisplay.setManaged(false);
            }
            loadSurvivalHighestLevel();
        } else {
            if (sprintModeDisplay != null) {
                sprintModeDisplay.setVisible(false);
                sprintModeDisplay.setManaged(false);
            }
            if (ultraModeDisplay != null) {
                ultraModeDisplay.setVisible(false);
                ultraModeDisplay.setManaged(false);
            }
            if (survivalModeDisplay != null) {
                survivalModeDisplay.setVisible(false);
                survivalModeDisplay.setManaged(false);
            }
        }
    }
    
    /**
     * Get the current game mode
     */
    public GameMode getGameMode() {
        return currentGameMode;
    }
    
    /**
     * Load Ultra mode best score from file
     */
    private void loadUltraBestScore() {
        try {
            File scoreFile = new File(ULTRA_BEST_SCORE_FILE);
            if (scoreFile.exists() && scoreFile.canRead()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        ultraBestScore = Integer.parseInt(line.trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not load Ultra best score: " + e.getMessage());
            ultraBestScore = 0;
        }
        updateUltraBestScoreDisplay();
    }
    
    /**
     * Save Ultra mode best score to file
     */
    private void saveUltraBestScore() {
        try {
            File scoreFile = new File(ULTRA_BEST_SCORE_FILE);
            try (PrintWriter writer = new PrintWriter(new FileWriter(scoreFile))) {
                writer.println(ultraBestScore);
            }
        } catch (Exception e) {
            System.out.println("Could not save Ultra best score: " + e.getMessage());
        }
    }
    
    /**
     * Update Ultra best score display
     */
    private void updateUltraBestScoreDisplay() {
        if (ultraBestScoreLabel != null) {
            Platform.runLater(() -> {
                ultraBestScoreLabel.setText(String.valueOf(ultraBestScore));
            });
        }
    }
    
    /**
     * Load Survival mode highest level from file
     */
    private void loadSurvivalHighestLevel() {
        try {
            File levelFile = new File(SURVIVAL_HIGHEST_LEVEL_FILE);
            if (levelFile.exists() && levelFile.canRead()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(levelFile))) {
                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        survivalHighestLevel = Integer.parseInt(line.trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not load Survival highest level: " + e.getMessage());
            survivalHighestLevel = 1;
        }
        updateSurvivalHighestLevelDisplay();
    }
    
    /**
     * Save Survival mode highest level to file
     */
    private void saveSurvivalHighestLevel() {
        try {
            File levelFile = new File(SURVIVAL_HIGHEST_LEVEL_FILE);
            try (PrintWriter writer = new PrintWriter(new FileWriter(levelFile))) {
                writer.println(survivalHighestLevel);
            }
        } catch (Exception e) {
            System.out.println("Could not save Survival highest level: " + e.getMessage());
        }
    }
    
    /**
     * Update Survival highest level display
     */
    private void updateSurvivalHighestLevelDisplay() {
        if (survivalHighestLevelLabel != null) {
            Platform.runLater(() -> {
                survivalHighestLevelLabel.setText(String.valueOf(survivalHighestLevel));
            });
        }
    }
    
    /**
     * Initialize Survival mode
     */
    private void initializeSurvivalMode() {
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
    public void checkSurvivalSpeedIncrease(int currentScore) {
        if (currentGameMode == GameMode.SURVIVAL && currentScore >= survivalNextThreshold) {
            survivalSpeedLevel++;
            // Decrease interval (make faster): 400ms -> 350ms -> 300ms -> 250ms -> 200ms, etc.
            // Keep at 400ms for now (can be adjusted later)
            survivalSpeedInterval = 400;
            
            // Update next threshold
            survivalNextThreshold += SURVIVAL_SPEED_INCREASE_THRESHOLD;
            
            // Update highest level if this is better
            if (survivalSpeedLevel > survivalHighestLevel) {
                survivalHighestLevel = survivalSpeedLevel;
                saveSurvivalHighestLevel();
                updateSurvivalHighestLevelDisplay();
            }
            
            // Update game timeline speed
            if (timeLine != null && currentGameMode == GameMode.SURVIVAL) {
                timeLine.stop();
                timeLine = new Timeline(new KeyFrame(
                        Duration.millis(survivalSpeedInterval),
                        ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
                ));
                timeLine.setCycleCount(Timeline.INDEFINITE);
                timeLine.play();
            }
            
            updateSurvivalDisplay();
        }
    }
    
    /**
     * Start Ultra mode timer and speed progression
     */
    private void startUltraTimer() {
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
    private void stopUltraTimer() {
        if (ultraTimer != null) {
            ultraTimer.stop();
        }
    }
    
    /**
     * Update Ultra mode timer display (countdown from 2 minutes)
     */
    private void updateUltraTimerDisplay() {
        if (ultraStartTime > 0 && ultraTimerLabel != null && !isGameOver.getValue()) {
            long elapsed = System.currentTimeMillis() - ultraStartTime;
            long remaining = ULTRA_TIME_LIMIT - elapsed;
            
            if (remaining <= 0) {
                // Time's up! Stop immediately
                ultraTimerLabel.setText("00:00");
                // Stop timeline immediately to prevent blocks from falling
                if (timeLine != null) {
                    timeLine.stop();
                }
                // Set game over state to prevent any further moves
                isGameOver.setValue(true);
                // Then show completion screen
                Platform.runLater(() -> {
                    ultraComplete();
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
                if (timeLine != null && currentGameMode == GameMode.ULTRA) {
                    timeLine.stop();
                    timeLine = new Timeline(new KeyFrame(
                            Duration.millis(currentSpeedInterval),
                            ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
                    ));
                    timeLine.setCycleCount(Timeline.INDEFINITE);
                    timeLine.play();
                }
                
                updateUltraSpeedLevelDisplay();
            }
        }
    }
    
    /**
     * Ultra mode completed - time's up!
     */
    public void ultraComplete() {
        // Stop Ultra timer
        stopUltraTimer();
        
        // Ensure game timeline is stopped (should already be stopped, but double-check)
        if (timeLine != null) {
            timeLine.stop();
        }
        
        // Ensure game over state is set
        isGameOver.setValue(true);
        
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
        boolean isNewBest = false;
        if (finalScore > ultraBestScore) {
            ultraBestScore = finalScore;
            saveUltraBestScore();
            updateUltraBestScoreDisplay();
            isNewBest = true;
        }
        
        // Show completion message
        isGameOver.setValue(true);
        gameOverPanel.setVisible(true);
        
        String successMessage = "TIME'S UP!";
        if (isNewBest) {
            successMessage = "NEW RECORD!";
        }
        gameOverPanel.setGameOverMessage(successMessage);
        
        // Show final score
        String bestScoreStr = String.valueOf(ultraBestScore);
        String currentScoreStr = String.valueOf(finalScore);
        gameOverPanel.setTimeInfo("BEST SCORE: " + bestScoreStr, "FINAL SCORE: " + currentScoreStr);
        
        // Center the game over panel on screen
        Parent parent = gameBoard.getParent();
        if (parent instanceof Pane) {
            centerGameOverPanel((Pane) parent);
        }
        
        // Play completion sound
        playLineClearSound();
        
        gamePanel.requestFocus();
    }
    
    /**
     * Start Sprint mode timer
     */
    private void startSprintTimer() {
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
    public void sprintComplete() {
        if (sprintTimer != null) {
            sprintTimer.stop();
        }
        
        long elapsedTime = System.currentTimeMillis() - sprintStartTime;
        boolean isNewBest = false;
        
        // Update best time if this is better
        if (elapsedTime < sprintBestTime) {
            sprintBestTime = elapsedTime;
            saveSprintBestTime();
            updateSprintBestTimeDisplay();
            isNewBest = true;
        }
        
        // Stop game timeline
        if (timeLine != null) {
            timeLine.stop();
        }
        
        // Show big success message
        isGameOver.setValue(true);
        gameOverPanel.setVisible(true);
        
        String successMessage = "SUCCESS!";
        if (isNewBest) {
            successMessage = "NEW RECORD!";
        }
        gameOverPanel.setGameOverMessage(successMessage);
        
        // Show best time and current time
        String bestTimeStr = (sprintBestTime == Long.MAX_VALUE) ? "--:--" : formatTime(sprintBestTime);
        String currentTimeStr = formatTime(elapsedTime);
        gameOverPanel.setTimeInfo(bestTimeStr, currentTimeStr);
        
        // Center the game over panel on screen
        Parent parent = gameBoard.getParent();
        if (parent instanceof Pane) {
            centerGameOverPanel((Pane) parent);
        }
        
        // Play completion sound
        playLineClearSound();
        
        gamePanel.requestFocus();
    }
    
    /**
     * Format time in milliseconds to MM:SS format
     */
    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Load Sprint best time from file
     */
    private void loadSprintBestTime() {
        try {
            File timeFile = new File(SPRINT_BEST_TIME_FILE);
            if (timeFile.exists() && timeFile.canRead()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(timeFile))) {
                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        sprintBestTime = Long.parseLong(line.trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not load Sprint best time: " + e.getMessage());
            sprintBestTime = Long.MAX_VALUE;
        }
    }
    
    /**
     * Save Sprint best time to file
     */
    private void saveSprintBestTime() {
        try {
            File timeFile = new File(SPRINT_BEST_TIME_FILE);
            try (PrintWriter writer = new PrintWriter(new FileWriter(timeFile))) {
                writer.println(sprintBestTime);
            }
        } catch (Exception e) {
            System.out.println("Could not save Sprint best time: " + e.getMessage());
        }
    }
    
    /**
     * Update Sprint best time display
     */
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

    public void gameOver() {
        // Stop Ultra timer if in Ultra mode
        if (currentGameMode == GameMode.ULTRA) {
            stopUltraTimer();
        }
        
        // Lower background music volume instead of stopping (hybrid approach)
        if (gameplayBackgroundMusic != null) {
            gameplayBackgroundMusic.setVolume(0.15); // Lower to 15% volume
        }
        
        // Play game over sound
        playGameOverSound();
        
        timeLine.stop();
        gameOverPanel.setVisible(true);
        gameOverPanel.resetToDefault(); // Reset to default game over display
        isGameOver.setValue(Boolean.TRUE);
        
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
        timeLine.stop();
        gameOverPanel.setVisible(false);
        gameOverPanel.resetToDefault(); // Reset game over panel to default state
        
        // Restore background music to normal volume when starting new game
        if (gameplayBackgroundMusic != null) {
            gameplayBackgroundMusic.setVolume(0.4); // Restore to 40% volume
        }
        
        // Reset mode-specific variables
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
        
        eventListener.createNewGame();
        gamePanel.requestFocus();
        
        // Reset timeline with correct speed for mode
        long initialSpeed = 400;
        if (currentGameMode == GameMode.SPRINT) {
            initialSpeed = 400;
        } else if (currentGameMode == GameMode.ULTRA) {
            initialSpeed = currentSpeedInterval;
        } else if (currentGameMode == GameMode.SURVIVAL) {
            initialSpeed = survivalSpeedInterval;
        }
        
        timeLine.stop();
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(initialSpeed),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
        
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        
        // Restart mode-specific timers
        if (currentGameMode == GameMode.SPRINT) {
            startSprintTimer();
        } else if (currentGameMode == GameMode.ULTRA) {
            startUltraTimer();
        } else if (currentGameMode == GameMode.SURVIVAL) {
            initializeSurvivalMode();
        }
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
    
    /**
     * Toggle pause state
     */
    private void togglePause() {
        if (isPause.getValue() == Boolean.FALSE) {
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
        if (isGameOver.getValue() == Boolean.TRUE) {
            return; // Don't pause if game is over
        }
        
        isPause.setValue(Boolean.TRUE);
        
        // Stop game timeline
        if (timeLine != null) {
            timeLine.pause();
        }
        
        // Pause mode-specific timers
        if (currentGameMode == GameMode.SPRINT && sprintTimer != null) {
            sprintTimer.pause();
        } else if (currentGameMode == GameMode.ULTRA && ultraTimer != null) {
            ultraTimer.pause();
        }
        
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
        if (isPause.getValue() == Boolean.TRUE) {
            isPause.setValue(Boolean.FALSE);
            
            // Resume game timeline
            if (timeLine != null) {
                timeLine.play();
            }
            
            // Resume mode-specific timers
            if (currentGameMode == GameMode.SPRINT && sprintTimer != null) {
                sprintTimer.play();
            } else if (currentGameMode == GameMode.ULTRA && ultraTimer != null) {
                ultraTimer.play();
            }
            
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
        isPause.setValue(Boolean.FALSE);
        
        // Restart the game
        newGame(null);
    }
    
    /**
     * Quit to main menu from pause menu
     */
    @FXML
    public void quitToMainMenuFromPause(ActionEvent actionEvent) {
        // Reset pause state
        isPause.setValue(Boolean.FALSE);
        
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
            if (timeLine != null) {
                timeLine.stop();
            }

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
            stage.setFullScreenExitHint("Press ESC to exit full screen");
            
            // Stop background video
            if (gameplayVideoPlayer != null) {
                gameplayVideoPlayer.stop();
                gameplayVideoPlayer.dispose();
            }
            
            // Stop and dispose sound effects
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
            
            // Set the primary stage reference for the menu controller
            menuController.setPrimaryStage(stage);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error returning to menu: " + e.getMessage());
        }
    }
    
    /**
     * Initialize background video for gameplay
     */
    private void initializeGameplayBackgroundVideo() {
        try {
            System.out.println("Looking for gameplay video: single_player.mp4");
            
            if (gameplayBackgroundVideo == null) {
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
            gameplayVideoPlayer = new MediaPlayer(videoMedia);
            
            // Set up video properties
            gameplayVideoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            gameplayVideoPlayer.setMute(true); // Mute video audio
            gameplayVideoPlayer.setAutoPlay(false);
            
            // Add event handlers
            gameplayVideoPlayer.setOnError(() -> {
                if (gameplayVideoPlayer.getError() != null) {
                    System.out.println("✗ Video player error: " + gameplayVideoPlayer.getError().getMessage());
                }
            });
            
            gameplayVideoPlayer.setOnReady(() -> {
                System.out.println("✓ Gameplay video player ready - starting playback");
                gameplayVideoPlayer.play();
            });
            
            gameplayVideoPlayer.setOnPlaying(() -> {
                System.out.println("✓ Gameplay video is now playing");
            });
            
            gameplayVideoPlayer.setOnEndOfMedia(() -> {
                System.out.println("Gameplay video ended - restarting");
                gameplayVideoPlayer.seek(Duration.ZERO);
                gameplayVideoPlayer.play();
            });
            
            // Bind to MediaView
            gameplayBackgroundVideo.setMediaPlayer(gameplayVideoPlayer);
            
            // Setup video sizing after scene is available
            if (gameplayBackgroundVideo.getScene() != null) {
                setupGameplayVideoFullScreen();
            } else {
                gameplayBackgroundVideo.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        setupGameplayVideoFullScreen();
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
     * Setup gameplay video to fill entire screen
     */
    private void setupGameplayVideoFullScreen() {
        if (gameplayBackgroundVideo != null) {
            if (gameplayBackgroundVideo.getScene() != null) {
                gameplayBackgroundVideo.fitWidthProperty().bind(gameplayBackgroundVideo.getScene().widthProperty());
                gameplayBackgroundVideo.fitHeightProperty().bind(gameplayBackgroundVideo.getScene().heightProperty());
                System.out.println("✓ Gameplay video size bound to scene dimensions - full screen coverage");
            } else {
                // Fallback - set large enough to cover typical screens
                gameplayBackgroundVideo.setFitWidth(1920);
                gameplayBackgroundVideo.setFitHeight(1080);
                System.out.println("✓ Gameplay video set to large size for full screen coverage (fallback)");
            }
        }
    }
    
    /**
     * Setup dark overlay - makes the entire background 70% darker
     */
    private void setupVignetteEffect() {
        if (gameplayVideoOverlay == null) {
            System.out.println("✗ gameplayVideoOverlay is null");
            return;
        }
        
        System.out.println("Setting up dark overlay (70% darker)...");
        
        // Use Platform.runLater to ensure scene is ready
        Platform.runLater(() -> {
            Runnable applyOverlay = () -> {
                if (gameplayVideoOverlay.getScene() == null) {
                    System.out.println("✗ Scene is null, cannot apply overlay");
                    return;
                }
                
                System.out.println("Applying uniform dark overlay (70% darker)...");
                
                // Create uniform dark overlay - 70% darker (0.7 opacity black)
                Color darkOverlay = Color.rgb(0, 0, 0, 0.7);
                
                // Set the background with uniform dark color
                gameplayVideoOverlay.setBackground(new javafx.scene.layout.Background(
                    new javafx.scene.layout.BackgroundFill(darkOverlay, null, null)
                ));
                
                // Make overlay fill entire scene
                javafx.scene.Scene scene = gameplayVideoOverlay.getScene();
                
                // Unbind first to avoid conflicts
                gameplayVideoOverlay.prefWidthProperty().unbind();
                gameplayVideoOverlay.prefHeightProperty().unbind();
                
                // Bind to scene size
                gameplayVideoOverlay.prefWidthProperty().bind(scene.widthProperty());
                gameplayVideoOverlay.prefHeightProperty().bind(scene.heightProperty());
                gameplayVideoOverlay.setMaxWidth(Double.MAX_VALUE);
                gameplayVideoOverlay.setMaxHeight(Double.MAX_VALUE);
                gameplayVideoOverlay.setManaged(false); // Disable managed sizing
                
                // Ensure it's visible
                gameplayVideoOverlay.setVisible(true);
                gameplayVideoOverlay.setMouseTransparent(true); // Allow clicks to pass through
                
                System.out.println("✓ Dark overlay applied (70% darker)!");
                System.out.println("  Overlay size: " + gameplayVideoOverlay.getPrefWidth() + "x" + gameplayVideoOverlay.getPrefHeight());
                System.out.println("  Scene size: " + scene.getWidth() + "x" + scene.getHeight());
            };
            
            if (gameplayVideoOverlay.getScene() != null) {
                applyOverlay.run();
            } else {
                System.out.println("Waiting for scene to be ready...");
                // Wait for scene to be ready
                gameplayVideoOverlay.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        System.out.println("Scene is now ready, applying dark overlay...");
                        applyOverlay.run();
                    }
                });
            }
        });
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
            }, "blockLandSound");
            
            // Try to load hard drop sound
            loadSoundEffect(new String[]{
                "audio/hard_drop.mp3", 
                "audio/drop.mp3", 
                "audio/hard_drop.wav",
                "audio/drop.wav",
                "audio/harddrop.mp3"
            }, "hardDropSound");
            
            // Try to load line clear sound
            loadSoundEffect(new String[]{
                "audio/tetris_success.wav",
                "audio/line_clear.wav",
                "audio/line_clear.mp3",
                "audio/success.wav",
                "audio/clear.wav"
            }, "lineClearSound");
            
            // Try to load game over sound
            loadSoundEffect(new String[]{
                "audio/game_over.wav",
                "audio/gameover.wav",
                "audio/game_over.mp3",
                "audio/gameover.mp3"
            }, "gameOverSound");
            
            System.out.println("✓ Sound effects initialization complete");
        } catch (Exception e) {
            System.out.println("✗ Sound effects initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to load a sound effect, trying multiple possible file names
     * @param fileNames Array of possible file names to try
     * @param variableName Name of the variable to assign to ("blockLandSound" or "hardDropSound")
     */
    private void loadSoundEffect(String[] fileNames, String variableName) {
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
            soundPlayer.setVolume(0.5); // Set volume to 50%
            
            // Apply SFX volume from settings
            SettingsManager settings = SettingsManager.getInstance();
            soundPlayer.setVolume(settings.getSfxVolume());
            
            if (variableName.equals("blockLandSound")) {
                blockLandSound = soundPlayer;
            } else if (variableName.equals("hardDropSound")) {
                hardDropSound = soundPlayer;
            } else if (variableName.equals("lineClearSound")) {
                lineClearSound = soundPlayer;
            } else if (variableName.equals("gameOverSound")) {
                gameOverSound = soundPlayer;
            }
            
            System.out.println("✓ Sound effect loaded for: " + foundFile + " -> " + variableName);
        } else {
            System.out.println("✗ No sound file found for " + variableName + ". Tried:");
            for (String fileName : fileNames) {
                System.out.println("  - " + fileName);
            }
            System.out.println("Place sound files in src/main/resources/audio/");
        }
    }
    
    /**
     * Play block landing sound
     */
    private void playBlockLandSound() {
        SettingsManager settings = SettingsManager.getInstance();
        if (blockLandSound != null && settings.isSfxEnabled()) {
            // Reset to start if already playing
            blockLandSound.seek(Duration.ZERO);
            blockLandSound.play();
        }
    }
    
    /**
     * Play hard drop sound
     */
    private void playHardDropSound() {
        SettingsManager settings = SettingsManager.getInstance();
        if (hardDropSound != null && settings.isSfxEnabled()) {
            // Reset to start if already playing
            hardDropSound.seek(Duration.ZERO);
            hardDropSound.play();
        }
    }
    
    /**
     * Play line clear success sound
     */
    private void playLineClearSound() {
        SettingsManager settings = SettingsManager.getInstance();
        if (lineClearSound != null && settings.isSfxEnabled()) {
            // Reset to start if already playing
            lineClearSound.seek(Duration.ZERO);
            lineClearSound.play();
        }
    }
    
    /**
     * Play game over sound
     */
    private void playGameOverSound() {
        SettingsManager settings = SettingsManager.getInstance();
        if (gameOverSound != null && settings.isSfxEnabled()) {
            // Reset to start if already playing
            gameOverSound.seek(Duration.ZERO);
            
            // Set higher volume for game over sound so it's clearly heard over the background music
            gameOverSound.setVolume(0.85); // 85% volume for game over sound
            
            // When game over sound ends, optionally restore background music volume
            gameOverSound.setOnEndOfMedia(() -> {
                // Keep background music at low volume during game over screen
                if (gameplayBackgroundMusic != null) {
                    gameplayBackgroundMusic.setVolume(0.2); // Slightly increase to 20% after sound finishes
                }
            });
            
            gameOverSound.play();
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
            
            // Try multiple possible filenames
            String[] musicFiles = {
                "audio/play_music.mp3",
                "audio/gameplay_music.mp3",
                "audio/game_music.mp3",
                "audio/music.mp3"
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
                SettingsManager settings = SettingsManager.getInstance();
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
}
