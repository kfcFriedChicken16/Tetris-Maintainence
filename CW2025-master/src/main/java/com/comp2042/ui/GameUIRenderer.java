package com.comp2042.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import com.comp2042.models.ViewData;
import com.comp2042.managers.SettingsManager;

/**
 * Handles all UI rendering responsibilities for the game view.
 * Extracted from GameViewController for better maintainability.
 */
public class GameUIRenderer {
    
    private static final int BRICK_SIZE = 20;
    
    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;
    
    private GridPane gamePanel;
    private GridPane brickPanel;
    private GridPane ghostPanel;
    private javafx.scene.layout.VBox nextPiecesContainer;
    private javafx.scene.layout.VBox holdPieceContainer;
    
    public GameUIRenderer(GridPane gamePanel, GridPane brickPanel, GridPane ghostPanel,
                         javafx.scene.layout.VBox nextPiecesContainer, 
                         javafx.scene.layout.VBox holdPieceContainer) {
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.ghostPanel = ghostPanel;
        this.nextPiecesContainer = nextPiecesContainer;
        this.holdPieceContainer = holdPieceContainer;
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
            case 8:
                returnPaint = Color.GRAY; // Grey for garbage blocks
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }

    public void refreshBrick(ViewData brick, boolean isPaused) {
        if (!isPaused) {
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
}
