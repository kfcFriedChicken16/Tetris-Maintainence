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

import java.io.File;
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
    private BorderPane gameBoard;

    @FXML
    private Button backToMenuBtn;

    @FXML
    private javafx.scene.control.Label scoreLabel;

    @FXML
    private javafx.scene.layout.VBox nextPiecesContainer;
    
    @FXML
    private javafx.scene.layout.VBox nextPanelVBox; // The container VBox from FXML
    
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

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        
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
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                        if (keyEvent.getCode() == KeyCode.SPACE) {
                            hardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
                            keyEvent.consume();
                        }
                    }
                    // Game over controls
                    if (isGameOver.getValue() == Boolean.TRUE) {
                        if (keyEvent.getCode() == KeyCode.SPACE) {
                            // Press Space to restart game
                            newGame(null);
                            keyEvent.consume();
                        } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                            // Press ESC to return to main menu
                            backToMenu(null);
                            keyEvent.consume();
                        }
                    }
                    if (keyEvent.getCode() == KeyCode.N) {
                        newGame(null);
                    }
                }
            });
        gameOverPanel.setVisible(false);

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

        // Move the 4 nodes as a single unit
        Group gameCluster = new Group(gameBoard, brickPanel, ghostPanel, groupNotification);
        gameCluster.setManaged(false);
        brickPanel.setManaged(false);
        ghostPanel.setManaged(false);
        groupNotification.setManaged(false);

        root.getChildren().removeAll(gameBoard, brickPanel, ghostPanel, groupNotification);
        root.getChildren().add(gameCluster);

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


        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
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

    private void updateGhostPiece(ViewData brick) {
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
        if (isPause.getValue() == Boolean.FALSE) {
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
    }

    public void gameOver() {
        // Lower background music volume instead of stopping (hybrid approach)
        if (gameplayBackgroundMusic != null) {
            gameplayBackgroundMusic.setVolume(0.15); // Lower to 15% volume
        }
        
        // Play game over sound
        playGameOverSound();
        
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
        
        // Ensure focus is on gamePanel so keyboard controls work for game over
        Platform.runLater(() -> {
            gamePanel.requestFocus();
        });
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        
        // Restore background music to normal volume when starting new game
        if (gameplayBackgroundMusic != null) {
            gameplayBackgroundMusic.setVolume(0.4); // Restore to 40% volume
        }
        
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
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
        if (blockLandSound != null) {
            // Reset to start if already playing
            blockLandSound.seek(Duration.ZERO);
            blockLandSound.play();
        }
    }
    
    /**
     * Play hard drop sound
     */
    private void playHardDropSound() {
        if (hardDropSound != null) {
            // Reset to start if already playing
            hardDropSound.seek(Duration.ZERO);
            hardDropSound.play();
        }
    }
    
    /**
     * Play line clear success sound
     */
    private void playLineClearSound() {
        if (lineClearSound != null) {
            // Reset to start if already playing
            lineClearSound.seek(Duration.ZERO);
            lineClearSound.play();
        }
    }
    
    /**
     * Play game over sound
     */
    private void playGameOverSound() {
        if (gameOverSound != null) {
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
                gameplayBackgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                gameplayBackgroundMusic.setVolume(0.4); // Set volume to 40% (lower than sound effects)
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
