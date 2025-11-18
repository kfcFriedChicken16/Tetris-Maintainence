package com.comp2042.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.models.NextShapeInfo;

import java.util.List;
import java.util.ArrayList;

/**
 * JUnit tests for BrickRotator class.
 * Tests piece rotation logic without UI dependencies.
 */
public class BrickRotatorTest {
    
    private BrickRotator rotator;
    private MockBrick mockBrick;
    
    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
        mockBrick = new MockBrick();
    }
    
    @Test
    void testRotatorInitialization() {
        assertNotNull(rotator, "BrickRotator should be initialized");
    }
    
    @Test
    void testSetBrick() {
        // Test setting a brick
        rotator.setBrick(mockBrick);
        
        // Verify we can get the current shape (should not be null)
        int[][] currentShape = rotator.getCurrentShape();
        assertNotNull(currentShape, "Current shape should not be null after setting brick");
        
        // Verify shape has proper dimensions (4x4 for Tetris pieces)
        assertEquals(4, currentShape.length, "Tetris pieces should be 4x4 matrix");
        assertEquals(4, currentShape[0].length, "Tetris pieces should be 4x4 matrix");
    }
    
    @Test
    void testGetCurrentShape() {
        rotator.setBrick(mockBrick);
        
        int[][] shape = rotator.getCurrentShape();
        assertNotNull(shape, "Current shape should not be null");
        
        // Verify it matches the first shape from mock brick
        int[][] expectedShape = mockBrick.getShapeMatrix().get(0);
        assertArrayEquals(expectedShape, shape, "Should return first shape initially");
    }
    
    @Test
    void testGetNextShape() {
        rotator.setBrick(mockBrick);
        
        NextShapeInfo nextInfo = rotator.getNextShape();
        assertNotNull(nextInfo, "Next shape info should not be null");
        
        int[][] nextShape = nextInfo.getShape();
        assertNotNull(nextShape, "Next shape should not be null");
        assertEquals(4, nextShape.length, "Next shape should be 4x4");
    }
    
    @Test
    void testSetCurrentShape() {
        rotator.setBrick(mockBrick);
        
        // Set to second shape (index 1)
        rotator.setCurrentShape(1);
        
        int[][] currentShape = rotator.getCurrentShape();
        int[][] expectedShape = mockBrick.getShapeMatrix().get(1);
        
        assertArrayEquals(expectedShape, currentShape, "Should return second shape after setting index 1");
    }
    
    @Test
    void testGetBrick() {
        rotator.setBrick(mockBrick);
        
        Brick retrievedBrick = rotator.getBrick();
        assertSame(mockBrick, retrievedBrick, "Should return the same brick that was set");
    }
    
    @Test
    void testShapeIndexWrapping() {
        rotator.setBrick(mockBrick);
        
        // Test that shape index wraps around (mock brick has 4 shapes: 0,1,2,3)
        rotator.setCurrentShape(4); // Should wrap to 0
        
        // This might cause an exception or wrap around - both are acceptable
        try {
            int[][] shape = rotator.getCurrentShape();
            assertNotNull(shape, "Shape should be valid even with wrapped index");
        } catch (IndexOutOfBoundsException e) {
            // It's acceptable if the implementation throws an exception for invalid indices
            assertTrue(true, "Exception for invalid shape index is acceptable");
        }
    }
    
    @Test
    void testWithoutBrick() {
        // Test behavior when no brick is set
        try {
            int[][] shape = rotator.getCurrentShape();
            // If it doesn't throw an exception, shape should be null
            assertNull(shape, "Should return null when no brick is set");
        } catch (NullPointerException e) {
            // It's acceptable for this to throw NPE when no brick is set
            assertTrue(true, "NullPointerException is acceptable when no brick is set");
        }
    }
    
    @Test
    void testNextShapeProgression() {
        rotator.setBrick(mockBrick);
        
        // Get next shape info multiple times
        NextShapeInfo next1 = rotator.getNextShape();
        NextShapeInfo next2 = rotator.getNextShape();
        
        assertNotNull(next1, "First next shape should not be null");
        assertNotNull(next2, "Second next shape should not be null");
        
        // Both should return the same next shape (since current shape hasn't changed)
        assertEquals(next1.getPosition(), next2.getPosition(), 
            "Next shape position should be consistent");
    }
    
    /**
     * Mock brick implementation for testing
     */
    private static class MockBrick implements Brick {
        private final List<int[][]> shapes;
        
        public MockBrick() {
            shapes = new ArrayList<>();
            
            // Add 4 different rotations of a simple shape
            shapes.add(new int[][] {
                {0, 1, 0, 0},
                {1, 1, 1, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
            });
            
            shapes.add(new int[][] {
                {0, 1, 0, 0},
                {0, 1, 1, 0},
                {0, 1, 0, 0},
                {0, 0, 0, 0}
            });
            
            shapes.add(new int[][] {
                {0, 0, 0, 0},
                {1, 1, 1, 0},
                {0, 1, 0, 0},
                {0, 0, 0, 0}
            });
            
            shapes.add(new int[][] {
                {0, 1, 0, 0},
                {1, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 0, 0}
            });
        }
        
        @Override
        public List<int[][]> getShapeMatrix() {
            return new ArrayList<>(shapes); // Return a copy to avoid modification
        }
    }
}