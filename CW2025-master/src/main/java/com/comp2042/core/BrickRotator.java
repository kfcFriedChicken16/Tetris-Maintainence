package com.comp2042.core;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.models.NextShapeInfo;

/**
 * Manages rotation state for Tetris pieces.
 * Handles the current rotation state of a brick and provides methods
 * to get the next rotation shape.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Gets information about the next rotation shape.
     * 
     * @return NextShapeInfo containing the next shape matrix and position index
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Gets the current shape matrix of the brick.
     * 
     * @return A 2D array representing the current rotation state of the brick
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the current rotation state of the brick.
     * 
     * @param currentShape The index of the rotation state to set
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Sets the brick and resets rotation to the initial state.
     * 
     * @param brick The Brick object to set
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }
    
    /**
     * Gets the current brick.
     * 
     * @return The Brick object currently being rotated
     */
    public Brick getBrick() {
        return brick;
    }

}
