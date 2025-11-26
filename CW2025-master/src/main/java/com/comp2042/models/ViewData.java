package com.comp2042.models;

import com.comp2042.utils.MatrixOperations;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Data class containing all information needed to render the game view.
 * Includes current piece position, next pieces, ghost piece position, and held piece.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;
    private final List<int[][]> nextBricksList; // List of next 4 bricks
    private final Point ghostPosition;
    private final int[][] heldBrickData; // Currently held piece (null if nothing held)

    /**
     * Constructs ViewData with basic piece information.
     * 
     * @param brickData The current piece shape matrix
     * @param xPosition The X coordinate of the current piece
     * @param yPosition The Y coordinate of the current piece
     * @param nextBrickData The next piece shape matrix
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.nextBricksList = null;
        this.ghostPosition = null; // No ghost position for this constructor
        this.heldBrickData = null;
    }

    /**
     * Constructs ViewData with ghost piece position.
     * 
     * @param brickData The current piece shape matrix
     * @param xPosition The X coordinate of the current piece
     * @param yPosition The Y coordinate of the current piece
     * @param nextBrickData The next piece shape matrix
     * @param ghostPosition The position where the piece would land (ghost piece)
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, Point ghostPosition) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.nextBricksList = null;
        this.ghostPosition = ghostPosition;
        this.heldBrickData = null;
    }

    /**
     * Constructs ViewData with next bricks list and ghost position.
     * 
     * @param brickData The current piece shape matrix
     * @param xPosition The X coordinate of the current piece
     * @param yPosition The Y coordinate of the current piece
     * @param nextBrickData The next piece shape matrix
     * @param nextBricksList List of the next 4 pieces for preview
     * @param ghostPosition The position where the piece would land (ghost piece)
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, 
                    List<int[][]> nextBricksList, Point ghostPosition) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.nextBricksList = nextBricksList != null ? new ArrayList<>(nextBricksList) : null;
        this.ghostPosition = ghostPosition;
        this.heldBrickData = null;
    }
    
    /**
     * Constructs ViewData with all information including held piece.
     * 
     * @param brickData The current piece shape matrix
     * @param xPosition The X coordinate of the current piece
     * @param yPosition The Y coordinate of the current piece
     * @param nextBrickData The next piece shape matrix
     * @param nextBricksList List of the next 4 pieces for preview
     * @param ghostPosition The position where the piece would land (ghost piece)
     * @param heldBrickData The currently held piece shape matrix (null if nothing held)
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, 
                    List<int[][]> nextBricksList, Point ghostPosition, int[][] heldBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.nextBricksList = nextBricksList != null ? new ArrayList<>(nextBricksList) : null;
        this.ghostPosition = ghostPosition;
        this.heldBrickData = heldBrickData != null ? MatrixOperations.copy(heldBrickData) : null;
    }

    /**
     * Gets a copy of the current piece shape matrix.
     * 
     * @return A copy of the current brick data matrix
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Gets the X position of the current piece.
     * 
     * @return The X coordinate
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the Y position of the current piece.
     * 
     * @return The Y coordinate
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets a copy of the next piece shape matrix.
     * 
     * @return A copy of the next brick data matrix
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }

    /**
     * Gets the ghost piece position (where the piece would land).
     * 
     * @return A Point representing the ghost position, or null if not available
     */
    public Point getGhostPosition() {
        return ghostPosition != null ? new Point(ghostPosition) : null;
    }

    /**
     * Gets a copy of the list of next 4 pieces.
     * 
     * @return A list of copies of the next brick matrices, or null if not available
     */
    public List<int[][]> getNextBricksList() {
        if (nextBricksList == null) {
            return null;
        }
        List<int[][]> copy = new ArrayList<>();
        for (int[][] brick : nextBricksList) {
            copy.add(MatrixOperations.copy(brick));
        }
        return copy;
    }
    
    /**
     * Gets a copy of the held piece shape matrix.
     * 
     * @return A copy of the held brick data matrix, or null if nothing is held
     */
    public int[][] getHeldBrickData() {
        return heldBrickData != null ? MatrixOperations.copy(heldBrickData) : null;
    }
}
