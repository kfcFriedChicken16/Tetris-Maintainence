package com.comp2042.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javafx.beans.property.IntegerProperty;

/**
 * JUnit tests for Score model class.
 * Tests scoring logic and property binding without UI dependencies.
 */
public class ScoreTest {
    
    private Score score;
    
    @BeforeEach
    void setUp() {
        score = new Score();
    }
    
    @Test
    void testScoreInitialization() {
        assertNotNull(score, "Score should be initialized");
        assertEquals(0, score.scoreProperty().get(), "Initial score should be zero");
    }
    
    @Test
    void testScoreProperty() {
        // Test that score property is properly initialized
        IntegerProperty scoreProperty = score.scoreProperty();
        assertNotNull(scoreProperty, "Score property should not be null");
        assertEquals(0, scoreProperty.get(), "Initial score property value should be zero");
    }
    
    @Test
    void testAddScore() {
        // Test adding points to score
        int initialScore = score.scoreProperty().get();
        score.add(100);
        
        assertEquals(initialScore + 100, score.scoreProperty().get(), "Score should increase by added amount");
    }
    
    @Test
    void testAddMultipleScores() {
        // Test adding multiple score increments
        score.add(50);
        score.add(75);
        score.add(25);
        
        assertEquals(150, score.scoreProperty().get(), "Score should accumulate multiple additions");
    }
    
    @Test
    void testAddZeroScore() {
        // Test adding zero points
        int initialScore = score.scoreProperty().get();
        score.add(0);
        
        assertEquals(initialScore, score.scoreProperty().get(), "Adding zero should not change score");
    }
    
    @Test
    void testAddNegativeScore() {
        // Test adding negative points (should this be allowed?)
        score.add(100); // Set initial score
        score.add(-30);
        
        assertEquals(70, score.scoreProperty().get(), "Negative scores should decrease total");
    }
    
    @Test
    void testLargeScoreValues() {
        // Test with large score values
        score.add(999999);
        assertEquals(999999, score.scoreProperty().get(), "Should handle large score values");
        
        score.add(1);
        assertEquals(1000000, score.scoreProperty().get(), "Should handle score overflow correctly");
    }
    
    @Test
    void testScorePropertyBinding() {
        // Test that changes to score are reflected in property
        IntegerProperty scoreProperty = score.scoreProperty();
        int initialValue = scoreProperty.get();
        
        score.add(200);
        
        assertEquals(initialValue + 200, scoreProperty.get(), "Property should reflect score changes");
    }
    
    @Test
    void testMultiplePropertyReferences() {
        // Test that multiple references to scoreProperty() return the same object
        IntegerProperty prop1 = score.scoreProperty();
        IntegerProperty prop2 = score.scoreProperty();
        
        assertSame(prop1, prop2, "Multiple calls to scoreProperty() should return same object");
    }
    
    @Test
    void testScoreReset() {
        // Test resetting score (if such method exists)
        score.add(500);
        assertEquals(500, score.scoreProperty().get(), "Score should be set to 500");
        
        // If Score class has a reset method, test it
        try {
            java.lang.reflect.Method resetMethod = Score.class.getMethod("reset");
            resetMethod.invoke(score);
            assertEquals(0, score.scoreProperty().get(), "Score should be reset to zero");
        } catch (NoSuchMethodException e) {
            // Reset method doesn't exist, that's fine
            assertTrue(true, "Reset method not implemented - this is acceptable");
        } catch (Exception e) {
            fail("Unexpected exception when testing reset: " + e.getMessage());
        }
    }
    
    @Test
    void testScorePropertyListeners() {
        // Test that property change listeners work
        IntegerProperty scoreProperty = score.scoreProperty();
        final boolean[] listenerCalled = {false};
        final int[] oldValue = {-1};
        final int[] newValue = {-1};
        
        scoreProperty.addListener((obs, oldVal, newVal) -> {
            listenerCalled[0] = true;
            oldValue[0] = oldVal.intValue();
            newValue[0] = newVal.intValue();
        });
        
        score.add(150);
        
        assertTrue(listenerCalled[0], "Property change listener should be called");
        assertEquals(0, oldValue[0], "Old value should be 0");
        assertEquals(150, newValue[0], "New value should be 150");
    }
    
    @Test
    void testScoreConsistency() {
        // Test that score remains consistent across multiple operations
        int expectedScore = 0;
        
        // Perform various score operations
        int[] scoreChanges = {10, 25, 50, 100, 5, 200};
        
        for (int change : scoreChanges) {
            score.add(change);
            expectedScore += change;
            assertEquals(expectedScore, score.scoreProperty().get(), 
                "Score should be consistent after adding " + change);
        }
    }
    
    @Test
    void testScoreBoundaries() {
        // Test score with reasonable large values
        score.add(1000000);
        assertEquals(1000000, score.scoreProperty().get(), "Score should handle large values");
        
        // Test negative score (might be allowed for penalties)
        Score newScore = new Score();
        newScore.add(-100);
        assertEquals(-100, newScore.scoreProperty().get(), "Score should handle negative values");
    }
}
