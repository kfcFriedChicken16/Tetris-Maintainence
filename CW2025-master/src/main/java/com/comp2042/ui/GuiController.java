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
import javafx.scene.media.MediaView;

import com.comp2042.events.InputEventListener;
import com.comp2042.modes.GameMode;
import com.comp2042.models.ViewData;
import com.comp2042.models.DownData;
import com.comp2042.managers.ScoreManager;
import com.comp2042.managers.AudioManager;
import com.comp2042.managers.VideoManager;
import com.comp2042.managers.SettingsManager;
import com.comp2042.ui.panels.GameOverPanel;
import com.comp2042.ui.panels.PausePanel;
import com.comp2042.ui.panels.NotificationPanel;
import com.comp2042.core.GameStateManager;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventType;
import com.comp2042.events.EventSource;
import com.comp2042.menu.MenuController;

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

    // Video is now managed by VideoManager
    
    // Audio is now managed by AudioManager

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Rectangle[][] ghostRectangles;

    private Timeline timeLine;
    
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


        // Create and start game timeline with appropriate speed for current mode
        createAndStartGameTimeline(getInitialSpeedForMode());
        
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
        if (!gameStateManager.isPaused()) {
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
        if (!gameStateManager.isPaused() && !gameStateManager.isGameOver()) {
            // Additional check for Ultra mode: stop if time is up
            if (currentGameMode == GameMode.ULTRA && ultraStartTime > 0) {
                long elapsed = System.currentTimeMillis() - ultraStartTime;
                if (elapsed >= ULTRA_TIME_LIMIT) {
                    // Time's up, stop moving immediately
                    if (timeLine != null) {
                        timeLine.stop();
                    }
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
                checkSurvivalSpeedIncrease(currentScore);
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
            scoreManager.updateSurvivalHighestLevel(survivalSpeedLevel);
            
            // Update game timeline speed
            if (timeLine != null && currentGameMode == GameMode.SURVIVAL) {
                createAndStartGameTimeline(survivalSpeedInterval);
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
        if (ultraStartTime > 0 && ultraTimerLabel != null && !gameStateManager.isGameOver()) {
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
                gameStateManager.setGameOver(true);
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
                    createAndStartGameTimeline(currentSpeedInterval);
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
        Parent parent = gameBoard.getParent();
        if (parent instanceof Pane) {
            centerGameOverPanel((Pane) parent);
        }
        
        // Play completion sound
        audioManager.playLineClearSound();
        
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
        
        // Update best time if this is better
        long oldBest = scoreManager.getSprintBestTime();
        scoreManager.updateSprintBestTime(elapsedTime);
        boolean isNewBest = (elapsedTime < oldBest);
        
        // Stop game timeline
        if (timeLine != null) {
            timeLine.stop();
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
        String bestTimeStr = (bestTime == Long.MAX_VALUE) ? "--:--" : formatTime(bestTime);
        String currentTimeStr = formatTime(elapsedTime);
        gameOverPanel.setTimeInfo(bestTimeStr, currentTimeStr);
        
        // Center the game over panel on screen
        Parent parent = gameBoard.getParent();
        if (parent instanceof Pane) {
            centerGameOverPanel((Pane) parent);
        }
        
        // Play completion sound
        audioManager.playLineClearSound();
        
        gamePanel.requestFocus();
    }
    
    /**
     * Format time in milliseconds to MM:SS format
     * Delegates to ScoreManager.formatTime()
     */
    private String formatTime(long milliseconds) {
        return ScoreManager.formatTime(milliseconds);
    }
    

    public void gameOver() {
        // Stop Ultra timer if in Ultra mode
        if (currentGameMode == GameMode.ULTRA) {
            stopUltraTimer();
        }
        
        // Lower background music volume instead of stopping (hybrid approach)
        audioManager.setBackgroundMusicVolume(0.15); // Lower to 15% volume
        
        // Play game over sound
        audioManager.playGameOverSound();
        
        timeLine.stop();
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
        timeLine.stop();
        gameOverPanel.setVisible(false);
        gameOverPanel.resetToDefault(); // Reset game over panel to default state
        
        // Restore background music to normal volume when starting new game
        audioManager.setBackgroundMusicVolume(0.4); // Restore to 40% volume
        
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
        createAndStartGameTimeline(getInitialSpeedForMode());
        
        gameStateManager.reset();
        
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
     * Handle key press events for game controls
     */
    private void handleKeyPressed(KeyEvent keyEvent) {
        SettingsManager settings = SettingsManager.getInstance();
        KeyCode keyCode = keyEvent.getCode();
        
        // Game over controls - check FIRST before normal game controls
        if (gameStateManager.isGameOver()) {
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
        if (keyCode == settings.getPause() && !gameStateManager.isGameOver()) {
            togglePause();
            keyEvent.consume();
            return;
        }
        
        // Normal game controls (only when not paused and not game over)
        if (!gameStateManager.isPaused() && !gameStateManager.isGameOver()) {
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
        if (keyCode == settings.getRestart() && !gameStateManager.isGameOver()) {
            newGame(null);
            keyEvent.consume();
        }
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
        if (gameStateManager.isPaused()) {
            gameStateManager.setPaused(false);
            
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
            stage.setFullScreenExitKeyCombination(null);
            stage.setFullScreenExitHint("");
            
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
    
    // Video initialization methods moved to VideoManager
    
    // Audio initialization and playback methods moved to AudioManager
    
    /**
     * Get the initial game speed (in milliseconds) based on current game mode
     */
    private long getInitialSpeedForMode() {
        if (currentGameMode == GameMode.SPRINT) {
            return 400; // Sprint mode: fixed 400ms
        } else if (currentGameMode == GameMode.ULTRA) {
            return currentSpeedInterval; // Ultra mode: starts at 400ms
        } else if (currentGameMode == GameMode.SURVIVAL) {
            return survivalSpeedInterval; // Survival mode: starts at 400ms, increases with score
        }
        return 400; // Default for Classic mode
    }
    
    /**
     * Create and start the game timeline with the specified speed
     */
    private void createAndStartGameTimeline(long speed) {
        if (timeLine != null) {
            timeLine.stop();
        }
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(speed),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }
}
