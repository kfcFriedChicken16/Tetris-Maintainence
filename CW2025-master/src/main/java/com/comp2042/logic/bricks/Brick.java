package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Interface representing a Tetris brick (piece).
 * Defines the shape matrix containing all possible rotations of the brick.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public interface Brick {

    /**
     * Gets the list of shape matrices representing all possible rotations of this brick.
     * Each matrix in the list represents one rotation state.
     * 
     * @return A list of 2D integer arrays, each representing a rotation of the brick
     */
    List<int[][]> getShapeMatrix();
}
