package com.comp2042.models;

import com.comp2042.utils.MatrixOperations;

/**
 * Data class containing information about cleared rows.
 * Stores the number of lines removed, the updated board matrix, and score bonus.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Constructs a ClearRow object with information about cleared lines.
     * 
     * @param linesRemoved The number of lines that were cleared
     * @param newMatrix The updated board matrix after clearing rows
     * @param scoreBonus The score bonus awarded for clearing these lines
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Gets the number of lines that were removed.
     * 
     * @return The number of cleared lines
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Gets a copy of the updated board matrix after clearing rows.
     * 
     * @return A copy of the new board matrix
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Gets the score bonus awarded for clearing these lines.
     * 
     * @return The score bonus value
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}
