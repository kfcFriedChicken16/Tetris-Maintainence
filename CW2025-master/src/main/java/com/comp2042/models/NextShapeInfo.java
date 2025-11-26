package com.comp2042.models;

import com.comp2042.utils.MatrixOperations;

/**
 * Data class containing information about the next rotation shape of a brick.
 * Used by BrickRotator to provide rotation preview information.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Constructs a NextShapeInfo object with the next shape and its position index.
     * 
     * @param shape The shape matrix for the next rotation
     * @param position The position index of this rotation in the brick's rotation sequence
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Gets a copy of the next shape matrix.
     * 
     * @return A copy of the shape matrix
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Gets the position index of this rotation.
     * 
     * @return The position index in the rotation sequence
     */
    public int getPosition() {
        return position;
    }
}
