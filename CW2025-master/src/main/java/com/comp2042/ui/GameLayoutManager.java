package com.comp2042.ui;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Manages layout and positioning of game UI elements.
 * Extracted from GameViewController for better maintainability.
 */
public class GameLayoutManager {
    
    private BorderPane gameBoard;
    private GridPane brickPanel;
    private GridPane ghostPanel;
    private Group groupNotification;
    private Group pauseGroup;
    private VBox holdPanelVBox;
    private VBox nextPanelVBox;
    
    public GameLayoutManager(BorderPane gameBoard, GridPane brickPanel, GridPane ghostPanel,
                            Group groupNotification, Group pauseGroup,
                            VBox holdPanelVBox, VBox nextPanelVBox) {
        this.gameBoard = gameBoard;
        this.brickPanel = brickPanel;
        this.ghostPanel = ghostPanel;
        this.groupNotification = groupNotification;
        this.pauseGroup = pauseGroup;
        this.holdPanelVBox = holdPanelVBox;
        this.nextPanelVBox = nextPanelVBox;
    }
    
    /**
     * Center the entire game cluster without changing FXML structure
     */
    public void centerGameCluster() {
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
    public void centerGameOverPanel(Pane root) {
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
    public void centerPausePanel(Pane root) {
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
}

