package com.comp2042.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for ClearRow model class.
 * Tests line clearing data model without UI dependencies.
 */
public class ClearRowTest {
    
    private ClearRow clearRow;
    private int[][] testMatrix;
    
    @BeforeEach
    void setUp() {
        // Create a test matrix
        testMatrix = new int[][] {
            {0, 0, 0, 0},
            {1, 1, 1, 1},
            {0, 1, 0, 1},
            {1, 1, 1, 1}
        };
        clearRow = new ClearRow(2, testMatrix, 800); // 2 lines cleared, test matrix, 800 points bonus
    }
    
    @Test
    void testClearRowInitialization() {
        assertNotNull(clearRow, "ClearRow should be initialized");
        assertEquals(2, clearRow.getLinesRemoved(), "Lines removed should match constructor parameter");
        assertEquals(800, clearRow.getScoreBonus(), "Score bonus should match constructor parameter");
        assertNotNull(clearRow.getNewMatrix(), "New matrix should not be null");
    }
    
    @Test
    void testZeroLinesCleared() {
        int[][] emptyMatrix = new int[4][4];
        ClearRow noLines = new ClearRow(0, emptyMatrix, 0);
        assertEquals(0, noLines.getLinesRemoved(), "Should handle zero lines cleared");
        assertEquals(0, noLines.getScoreBonus(), "Should handle zero score bonus");
        assertNotNull(noLines.getNewMatrix(), "Matrix should not be null even with zero lines");
    }
    
    @Test
    void testSingleLineCleared() {
        int[][] singleMatrix = new int[4][4];
        ClearRow singleLine = new ClearRow(1, singleMatrix, 100);
        assertEquals(1, singleLine.getLinesRemoved(), "Should handle single line cleared");
        assertEquals(100, singleLine.getScoreBonus(), "Should handle single line score bonus");
    }
    
    @Test
    void testMultipleLinesCleared() {
        int[][] multiMatrix = new int[4][4];
        ClearRow multipleLines = new ClearRow(4, multiMatrix, 1200); // Tetris (4 lines)
        assertEquals(4, multipleLines.getLinesRemoved(), "Should handle multiple lines cleared");
        assertEquals(1200, multipleLines.getScoreBonus(), "Should handle multiple lines score bonus");
    }
    
    @Test
    void testGetNewMatrix() {
        int[][] newMatrix = clearRow.getNewMatrix();
        assertNotNull(newMatrix, "New matrix should not be null");
        assertEquals(testMatrix.length, newMatrix.length, "Matrix should have same dimensions");
        
        // Verify it's a copy (modifying returned matrix doesn't affect internal state)
        newMatrix[0][0] = 999;
        int[][] anotherCopy = clearRow.getNewMatrix();
        assertNotEquals(999, anotherCopy[0][0], "Should return a copy, not reference to internal matrix");
    }
    
    @Test
    void testMatrixCopyIntegrity() {
        // Test that the matrix is properly copied
        int[][] retrievedMatrix = clearRow.getNewMatrix();
        
        // Verify dimensions
        assertEquals(testMatrix.length, retrievedMatrix.length, "Matrix row count should match");
        assertEquals(testMatrix[0].length, retrievedMatrix[0].length, "Matrix column count should match");
        
        // Verify content (should be a proper copy)
        for (int i = 0; i < testMatrix.length; i++) {
            assertArrayEquals(testMatrix[i], retrievedMatrix[i], "Row " + i + " should match original");
        }
    }
    
    @Test
    void testGettersConsistency() {
        // Test that getters return consistent values
        int lines = clearRow.getLinesRemoved();
        int bonus = clearRow.getScoreBonus();
        
        // Call getters multiple times to ensure consistency
        assertEquals(lines, clearRow.getLinesRemoved(), "Lines removed should be consistent");
        assertEquals(bonus, clearRow.getScoreBonus(), "Score bonus should be consistent");
        assertEquals(lines, clearRow.getLinesRemoved(), "Lines removed should remain consistent");
        assertEquals(bonus, clearRow.getScoreBonus(), "Score bonus should remain consistent");
    }
    
    @Test
    void testImmutability() {
        // Test that ClearRow is immutable (should not have setters)
        try {
            ClearRow.class.getMethod("setLinesRemoved", int.class);
            fail("ClearRow should not have setLinesRemoved method (should be immutable)");
        } catch (NoSuchMethodException e) {
            // Good - no setter found, object is immutable
            assertTrue(true, "ClearRow is immutable - good design");
        }
        
        try {
            ClearRow.class.getMethod("setScoreBonus", int.class);
            fail("ClearRow should not have setScoreBonus method (should be immutable)");
        } catch (NoSuchMethodException e) {
            // Good - no setter found, object is immutable
            assertTrue(true, "ClearRow is immutable - good design");
        }
    }
    
    @Test
    void testLargeValues() {
        int[][] largeMatrix = new int[10][10];
        ClearRow largeValues = new ClearRow(Integer.MAX_VALUE, largeMatrix, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, largeValues.getLinesRemoved(), "Should handle large line count");
        assertEquals(Integer.MAX_VALUE, largeValues.getScoreBonus(), "Should handle large score bonus");
        assertNotNull(largeValues.getNewMatrix(), "Should handle large matrix");
    }
    
    @Test
    void testNullMatrix() {
        // Test with null matrix (should this be allowed?)
        try {
            ClearRow nullMatrix = new ClearRow(1, null, 100);
            // If constructor accepts null, getNewMatrix() should handle it gracefully
            int[][] matrix = nullMatrix.getNewMatrix();
            // Either null or empty matrix is acceptable
            assertTrue(matrix == null || matrix.length == 0, "Null matrix should be handled gracefully");
        } catch (Exception e) {
            // It's acceptable for constructor to throw exception with null matrix
            assertTrue(true, "Exception with null matrix is acceptable");
        }
    }
    
    @Test
    void testTypicalTetrisScoring() {
        // Test typical Tetris scoring scenarios
        int[][] matrix1 = new int[4][4];
        int[][] matrix2 = new int[4][4];
        int[][] matrix3 = new int[4][4];
        int[][] matrix4 = new int[4][4];
        
        ClearRow single = new ClearRow(1, matrix1, 100);    // Single line
        ClearRow double_ = new ClearRow(2, matrix2, 300);   // Double lines
        ClearRow triple = new ClearRow(3, matrix3, 500);    // Triple lines
        ClearRow tetris = new ClearRow(4, matrix4, 800);    // Tetris (4 lines)
        
        assertEquals(1, single.getLinesRemoved(), "Single line clear");
        assertEquals(2, double_.getLinesRemoved(), "Double line clear");
        assertEquals(3, triple.getLinesRemoved(), "Triple line clear");
        assertEquals(4, tetris.getLinesRemoved(), "Tetris line clear");
        
        assertTrue(single.getScoreBonus() < double_.getScoreBonus(), "Double should score more than single");
        assertTrue(double_.getScoreBonus() < triple.getScoreBonus(), "Triple should score more than double");
        assertTrue(triple.getScoreBonus() < tetris.getScoreBonus(), "Tetris should score more than triple");
    }
    
    @Test
    void testBoundaryValues() {
        // Test boundary values for Tetris game
        int[][] maxMatrix = new int[4][4];
        int[][] minMatrix = new int[4][4];
        
        ClearRow maxLines = new ClearRow(4, maxMatrix, Integer.MAX_VALUE); // Maximum possible lines in Tetris
        assertEquals(4, maxLines.getLinesRemoved(), "Should handle maximum Tetris lines");
        assertEquals(Integer.MAX_VALUE, maxLines.getScoreBonus(), "Should handle maximum score bonus");
        
        ClearRow minLines = new ClearRow(0, minMatrix, 0); // No lines cleared
        assertEquals(0, minLines.getLinesRemoved(), "Should handle no lines cleared");
        assertEquals(0, minLines.getScoreBonus(), "Should handle no score bonus");
    }
}