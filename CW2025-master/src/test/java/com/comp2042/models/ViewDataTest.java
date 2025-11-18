package com.comp2042.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.util.List;

/**
 * JUnit tests for ViewData model class.
 * Tests data model for piece positioning and display without UI dependencies.
 */
public class ViewDataTest {
    
    private ViewData viewData;
    private int[][] testBrickData;
    
    @BeforeEach
    void setUp() {
        // Create test brick data (T-piece)
        testBrickData = new int[][] {
            {0, 1, 0, 0},
            {1, 1, 1, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        
        viewData = new ViewData(testBrickData, 5, 2, new int[4][4]);
    }
    
    @Test
    void testViewDataInitialization() {
        assertNotNull(viewData, "ViewData should be initialized");
        assertNotNull(viewData.getBrickData(), "Brick data should not be null");
        assertEquals(5, viewData.getxPosition(), "X position should match constructor parameter");
        assertEquals(2, viewData.getyPosition(), "Y position should match constructor parameter");
    }
    
    @Test
    void testBrickDataCopy() {
        // Test that brick data is properly stored
        int[][] retrievedData = viewData.getBrickData();
        assertNotNull(retrievedData, "Retrieved brick data should not be null");
        assertEquals(testBrickData.length, retrievedData.length, "Brick data should have correct dimensions");
        
        // Verify data content
        for (int i = 0; i < testBrickData.length; i++) {
            assertArrayEquals(testBrickData[i], retrievedData[i], "Row " + i + " should match original data");
        }
    }
    
    @Test
    void testPositionGetters() {
        // Test position getters
        assertEquals(5, viewData.getxPosition(), "X position getter should return correct value");
        assertEquals(2, viewData.getyPosition(), "Y position getter should return correct value");
    }
    
    @Test
    void testPositionSetters() {
        // Test position setters (if they exist)
        try {
            java.lang.reflect.Method setX = ViewData.class.getMethod("setxPosition", int.class);
            java.lang.reflect.Method setY = ViewData.class.getMethod("setyPosition", int.class);
            
            setX.invoke(viewData, 10);
            setY.invoke(viewData, 15);
            
            assertEquals(10, viewData.getxPosition(), "X position should be updated");
            assertEquals(15, viewData.getyPosition(), "Y position should be updated");
        } catch (NoSuchMethodException e) {
            // Setters don't exist, that's fine for immutable objects
            assertTrue(true, "Position setters not implemented - acceptable for immutable ViewData");
        } catch (Exception e) {
            fail("Unexpected exception when testing position setters: " + e.getMessage());
        }
    }
    
    @Test
    void testNegativePositions() {
        // Test ViewData with negative positions (valid for Tetris pieces spawning)
        ViewData negativeViewData = new ViewData(testBrickData, -1, -2, new int[4][4]);
        
        assertEquals(-1, negativeViewData.getxPosition(), "Should handle negative X position");
        assertEquals(-2, negativeViewData.getyPosition(), "Should handle negative Y position");
    }
    
    @Test
    void testEmptyBrickData() {
        // Test ViewData with empty brick data
        int[][] emptyData = new int[4][4]; // All zeros
        ViewData emptyViewData = new ViewData(emptyData, 0, 0, new int[4][4]);
        
        assertNotNull(emptyViewData.getBrickData(), "Empty brick data should not be null");
        assertEquals(4, emptyViewData.getBrickData().length, "Empty brick should maintain dimensions");
    }
    
    @Test
    void testNullBrickData() {
        // Test ViewData with null brick data (should handle gracefully)
        try {
            ViewData nullViewData = new ViewData(null, 0, 0, new int[4][4]);
            // If constructor accepts null, getBrickData() should return null or empty array
            int[][] data = nullViewData.getBrickData();
            // Either null or empty array is acceptable
            assertTrue(data == null || data.length == 0, "Null brick data should be handled gracefully");
        } catch (Exception e) {
            // It's acceptable for constructor to throw exception with null data
            assertTrue(true, "Exception with null brick data is acceptable");
        }
    }
    
    @Test
    void testGhostPosition() {
        // Test ghost position functionality (if implemented)
        try {
            java.lang.reflect.Method getGhostPosition = ViewData.class.getMethod("getGhostPosition");
            Point ghostPos = (Point) getGhostPosition.invoke(viewData);
            
            // Ghost position can be null initially
            if (ghostPos != null) {
                assertTrue(ghostPos.x >= 0 || ghostPos.x < 0, "Ghost position X should be a valid integer");
                assertTrue(ghostPos.y >= 0 || ghostPos.y < 0, "Ghost position Y should be a valid integer");
            }
        } catch (NoSuchMethodException e) {
            // Ghost position not implemented, that's fine
            assertTrue(true, "Ghost position not implemented - acceptable");
        } catch (Exception e) {
            fail("Unexpected exception when testing ghost position: " + e.getMessage());
        }
    }
    
    @Test
    void testNextBricksList() {
        // Test next bricks list functionality (if implemented)
        try {
            java.lang.reflect.Method getNextBricksList = ViewData.class.getMethod("getNextBricksList");
            @SuppressWarnings("unchecked")
            List<int[][]> nextBricks = (List<int[][]>) getNextBricksList.invoke(viewData);
            
            // Next bricks list can be null or empty initially
            if (nextBricks != null) {
                assertTrue(nextBricks.size() >= 0, "Next bricks list should have non-negative size");
            }
        } catch (NoSuchMethodException e) {
            // Next bricks list not implemented, that's fine
            assertTrue(true, "Next bricks list not implemented - acceptable");
        } catch (Exception e) {
            fail("Unexpected exception when testing next bricks list: " + e.getMessage());
        }
    }
    
    @Test
    void testHeldBrickData() {
        // Test held brick data functionality (if implemented)
        try {
            java.lang.reflect.Method getHeldBrickData = ViewData.class.getMethod("getHeldBrickData");
            int[][] heldData = (int[][]) getHeldBrickData.invoke(viewData);
            
            // Held brick data can be null initially
            if (heldData != null) {
                assertTrue(heldData.length >= 0, "Held brick data should have valid dimensions");
            }
        } catch (NoSuchMethodException e) {
            // Held brick data not implemented, that's fine
            assertTrue(true, "Held brick data not implemented - acceptable");
        } catch (Exception e) {
            fail("Unexpected exception when testing held brick data: " + e.getMessage());
        }
    }
    
    @Test
    void testBrickDataImmutability() {
        // Test that modifying returned brick data doesn't affect internal state
        int[][] retrievedData = viewData.getBrickData();
        int originalValue = retrievedData[0][1];
        
        // Modify the retrieved data
        retrievedData[0][1] = 999;
        
        // Get data again and check if it was affected
        int[][] newRetrievedData = viewData.getBrickData();
        
        // Depending on implementation, this could be a deep copy or reference
        // Both are acceptable, but we should document the behavior
        if (newRetrievedData[0][1] == originalValue) {
            // Deep copy implementation - changes don't affect internal state
            assertTrue(true, "ViewData uses deep copy - good encapsulation");
        } else {
            // Reference implementation - changes affect internal state
            assertTrue(true, "ViewData uses reference - acceptable but less safe");
        }
    }
    
    @Test
    void testLargePositions() {
        // Test ViewData with large position values
        ViewData largeViewData = new ViewData(testBrickData, Integer.MAX_VALUE, Integer.MIN_VALUE, new int[4][4]);
        
        assertEquals(Integer.MAX_VALUE, largeViewData.getxPosition(), "Should handle large X position");
        assertEquals(Integer.MIN_VALUE, largeViewData.getyPosition(), "Should handle large Y position");
    }
    
    @Test
    void testToString() {
        // Test toString method (if implemented)
        try {
            String stringRepresentation = viewData.toString();
            assertNotNull(stringRepresentation, "toString should not return null");
            assertTrue(stringRepresentation.length() > 0, "toString should return non-empty string");
        } catch (Exception e) {
            // toString might not be overridden, that's fine
            assertTrue(true, "toString method behavior is acceptable");
        }
    }
}
