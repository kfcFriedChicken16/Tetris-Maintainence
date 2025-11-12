package com.comp2042;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;
    private final List<int[][]> nextBricksList; // List of next 4 bricks
    private final Point ghostPosition;
    private final int[][] heldBrickData; // Currently held piece (null if nothing held)

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.nextBricksList = null;
        this.ghostPosition = null; // No ghost position for this constructor
        this.heldBrickData = null;
    }

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, Point ghostPosition) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.nextBricksList = null;
        this.ghostPosition = ghostPosition;
        this.heldBrickData = null;
    }

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

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }

    public Point getGhostPosition() {
        return ghostPosition != null ? new Point(ghostPosition) : null;
    }

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
    
    public int[][] getHeldBrickData() {
        return heldBrickData != null ? MatrixOperations.copy(heldBrickData) : null;
    }
}
