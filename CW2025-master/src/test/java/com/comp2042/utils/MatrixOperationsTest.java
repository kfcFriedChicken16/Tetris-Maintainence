package com.comp2042.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.comp2042.models.ClearRow;

/**
 * JUnit tests for MatrixOperations utility class.
 * Tests matrix manipulation methods used in Tetris game logic.
 */
public class MatrixOperationsTest {
    
    @Test
    void testCopyMatrix() {
        // Create a test matrix
        int[][] original = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        
        // Copy the matrix
        int[][] copy = MatrixOperations.copy(original);
        
        // Verify copy is not null and has same dimensions
        assertNotNull(copy, "Copied matrix should not be null");
        assertEquals(original.length, copy.length, "Copy should have same number of rows");
        assertEquals(original[0].length, copy[0].length, "Copy should have same number of columns");
        
        // Verify all values are copied correctly
        for (int i = 0; i < original.length; i++) {
            assertArrayEquals(original[i], copy[i], "Row " + i + " should be copied correctly");
        }
        
        // Verify it's a deep copy (modifying copy doesn't affect original)
        copy[1][1] = 999;
        assertEquals(5, original[1][1], "Original matrix should not be affected by changes to copy");
        assertEquals(999, copy[1][1], "Copy should reflect the change");
    }
    
    @Test
    void testCopyEmptyMatrix() {
        // Test copying an empty matrix
        int[][] empty = new int[0][0];
        int[][] copy = MatrixOperations.copy(empty);
        
        assertNotNull(copy, "Copy of empty matrix should not be null");
        assertEquals(0, copy.length, "Copy of empty matrix should have zero length");
    }
    
    @Test
    void testIntersectNoCollision() {
        // Create a game board with some filled cells
        int[][] board = {
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        
        // Create a small brick that doesn't collide
        int[][] brick = {
            {0, 1},
            {1, 1}
        };
        
        // Test intersection at position (1, 1) - should not intersect with empty board
        boolean intersects = MatrixOperations.intersect(board, brick, 1, 1);
        assertFalse(intersects, "Brick should not intersect with empty board");
    }
    
    @Test
    void testIntersectWithCollision() {
        // Create a game board with filled cells
        int[][] board = {
            {0, 0, 0, 0},
            {0, 1, 0, 0},
            {1, 1, 0, 0},
            {1, 1, 1, 0}
        };
        
        // Create a brick that will collide
        int[][] brick = {
            {1, 1},
            {1, 1}
        };
        
        // Test intersection at position (0, 1) where there's already a filled cell
        boolean intersects = MatrixOperations.intersect(board, brick, 0, 1);
        assertTrue(intersects, "Brick should intersect with filled board cells");
    }
    
    @Test
    void testIntersectOutOfBounds() {
        // Create a small board
        int[][] board = {
            {0, 0},
            {0, 0}
        };
        
        // Create a brick
        int[][] brick = {
            {1, 1},
            {1, 1}
        };
        
        // Test intersection at out-of-bounds position
        boolean intersects = MatrixOperations.intersect(board, brick, -1, 0);
        assertTrue(intersects, "Brick should intersect when positioned out of bounds (left)");
        
        intersects = MatrixOperations.intersect(board, brick, 2, 0);
        assertTrue(intersects, "Brick should intersect when positioned out of bounds (right)");
        
        intersects = MatrixOperations.intersect(board, brick, 0, 2);
        assertTrue(intersects, "Brick should intersect when positioned out of bounds (bottom)");
    }
    
    @Test
    void testMergeMatrices() {
        // Create a base matrix
        int[][] base = {
            {0, 0, 0, 0},
            {0, 1, 0, 0},
            {1, 1, 0, 0},
            {1, 1, 1, 0}
        };
        
        // Create a simple brick to merge
        int[][] brick = {
            {2, 2},
            {2, 0}
        };
        
        // Merge brick at position (1, 0)
        int[][] result = MatrixOperations.merge(base, brick, 1, 0);
        
        // Verify merge result
        assertNotNull(result, "Merge result should not be null");
        assertEquals(base.length, result.length, "Result should have same dimensions as base");
        
        // Check that brick values are merged correctly (note: coordinate system might be swapped)
        // The merge should place brick values where brick[j][i] != 0
        boolean foundMergedValues = false;
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                if (result[i][j] == 2) {
                    foundMergedValues = true;
                    break;
                }
            }
        }
        assertTrue(foundMergedValues, "Brick values should be merged into result matrix");
    }
    
    @Test
    void testCheckRemovingNoLines() {
        // Create a matrix with no complete lines
        int[][] matrix = {
            {0, 0, 0, 0},
            {1, 0, 1, 0},
            {1, 1, 0, 0},
            {1, 1, 1, 0}
        };
        
        ClearRow result = MatrixOperations.checkRemoving(matrix);
        
        assertNotNull(result, "ClearRow result should not be null");
        assertEquals(0, result.getLinesRemoved(), "No lines should be removed");
    }
    
    @Test
    void testCheckRemovingWithCompleteLines() {
        // Create a matrix with complete lines
        int[][] matrix = {
            {0, 0, 0, 0},
            {1, 1, 1, 1}, // Complete line
            {1, 0, 1, 0},
            {1, 1, 1, 1}  // Complete line
        };
        
        ClearRow result = MatrixOperations.checkRemoving(matrix);
        
        assertNotNull(result, "ClearRow result should not be null");
        assertEquals(2, result.getLinesRemoved(), "Two complete lines should be detected");
        assertTrue(result.getScoreBonus() > 0, "Score bonus should be positive for cleared lines");
    }
    
    @Test
    void testMatrixDimensions() {
        // Test with different matrix dimensions
        int[][] tallMatrix = new int[10][5];
        int[][] wideMatrix = new int[3][8];
        
        int[][] tallCopy = MatrixOperations.copy(tallMatrix);
        int[][] wideCopy = MatrixOperations.copy(wideMatrix);
        
        assertEquals(10, tallCopy.length, "Tall matrix copy should preserve height");
        assertEquals(5, tallCopy[0].length, "Tall matrix copy should preserve width");
        
        assertEquals(3, wideCopy.length, "Wide matrix copy should preserve height");
        assertEquals(8, wideCopy[0].length, "Wide matrix copy should preserve width");
    }
}
