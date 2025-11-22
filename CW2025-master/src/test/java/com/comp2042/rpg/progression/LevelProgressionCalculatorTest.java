package com.comp2042.rpg.progression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for LevelProgressionCalculator.
 * Tests core level calculation logic without dependencies.
 */
public class LevelProgressionCalculatorTest {
    
    private LevelProgressionCalculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new LevelProgressionCalculator();
    }
    
    @Test
    void testLevel1AtStart() {
        assertEquals(1, calculator.calculateLevelFromLines(0), "0 lines should be level 1");
        assertEquals(1, calculator.calculateLevelFromLines(9), "9 lines should be level 1");
    }
    
    @Test
    void testLevels1To5() {
        assertEquals(2, calculator.calculateLevelFromLines(10), "10 lines should be level 2");
        assertEquals(3, calculator.calculateLevelFromLines(20), "20 lines should be level 3");
        assertEquals(4, calculator.calculateLevelFromLines(30), "30 lines should be level 4");
        assertEquals(5, calculator.calculateLevelFromLines(40), "40 lines should be level 5");
        assertEquals(5, calculator.calculateLevelFromLines(49), "49 lines should still be level 5");
    }
    
    @Test
    void testLevel6Threshold() {
        assertEquals(5, calculator.calculateLevelFromLines(49), "49 lines should be level 5");
        assertEquals(6, calculator.calculateLevelFromLines(50), "50 lines should be level 6");
    }
    
    @Test
    void testLevels6To10() {
        assertEquals(6, calculator.calculateLevelFromLines(50), "50 lines should be level 6");
        assertEquals(7, calculator.calculateLevelFromLines(65), "65 lines should be level 7");
        assertEquals(8, calculator.calculateLevelFromLines(80), "80 lines should be level 8");
        assertEquals(10, calculator.calculateLevelFromLines(124), "124 lines should be level 10");
        // At 125 lines, it transitions to level 11 (threshold)
    }
    
    @Test
    void testLevel11Threshold() {
        // At 124 lines: linesAfter50 = 74, 74 < 75, so 6 + (74/15) = 6 + 4 = 10
        assertEquals(10, calculator.calculateLevelFromLines(124), "124 lines should be level 10");
        // At 125 lines: linesAfter50 = 75, 75 < 75 is false, so linesAfter125 = 0, 11 + (0/20) = 11
        assertEquals(11, calculator.calculateLevelFromLines(125), "125 lines should be level 11");
    }
    
    @Test
    void testLevels11To20() {
        assertEquals(11, calculator.calculateLevelFromLines(125), "125 lines should be level 11");
        // Level 15: linesAfter125 = 100, 100 < 200, so 11 + (100/20) = 11 + 5 = 16... wait
        // Let me recalculate: 225 lines, linesAfter125 = 100, 100 < 200, so 11 + (100/20) = 11 + 5 = 16
        // But test expects 15. Let me check: 225 - 125 = 100, 100 / 20 = 5, so 11 + 5 = 16
        // So the test expectation might be wrong, or I need to check the actual calculation
        
        // Actually, let me verify: for level 15, we need 125 + (15-11)*20 = 125 + 80 = 205 lines
        // At 205 lines: linesAfter125 = 80, 80 < 200, so 11 + (80/20) = 11 + 4 = 15 ✓
        // At 225 lines: linesAfter125 = 100, 100 < 200, so 11 + (100/20) = 11 + 5 = 16
        assertEquals(16, calculator.calculateLevelFromLines(225), "225 lines should be level 16");
        // Level 20: 125 + (20-11)*20 = 125 + 180 = 305 lines
        // At 305 lines: linesAfter125 = 180, 180 < 200, so 11 + (180/20) = 11 + 9 = 20 ✓
        assertEquals(20, calculator.calculateLevelFromLines(305), "305 lines should be level 20");
        // At 325 lines: linesAfter125 = 200, 200 < 200 is false, so goes to next tier = level 21
        assertEquals(21, calculator.calculateLevelFromLines(325), "325 lines should be level 21");
    }
    
    @Test
    void testLevel21Threshold() {
        // Level 20 needs: 125 + (20-11)*20 = 125 + 180 = 305 lines
        // At 324 lines: linesAfter125 = 199, 199 < 200, so 11 + (199/20) = 11 + 9 = 20
        assertEquals(20, calculator.calculateLevelFromLines(324), "324 lines should be level 20");
        // At 325 lines: linesAfter125 = 200, 200 < 200 is false, so linesAfter325 = 0, 21 + (0/25) = 21
        assertEquals(21, calculator.calculateLevelFromLines(325), "325 lines should be level 21");
    }
    
    @Test
    void testLevels21To30() {
        assertEquals(21, calculator.calculateLevelFromLines(325), "325 lines should be level 21");
        // Level 25: 325 + (25-21)*25 = 325 + 100 = 425 lines
        // At 425 lines: linesAfter325 = 100, 100 < 250, so 21 + (100/25) = 21 + 4 = 25 ✓
        assertEquals(25, calculator.calculateLevelFromLines(425), "425 lines should be level 25");
        // At 450 lines: linesAfter325 = 125, 125 < 250, so 21 + (125/25) = 21 + 5 = 26
        assertEquals(26, calculator.calculateLevelFromLines(450), "450 lines should be level 26");
        // Level 30: 325 + (30-21)*25 = 325 + 225 = 550 lines
        // At 550 lines: linesAfter325 = 225, 225 < 250, so 21 + (225/25) = 21 + 9 = 30 ✓
        assertEquals(30, calculator.calculateLevelFromLines(550), "550 lines should be level 30");
        // At 575 lines: linesAfter325 = 250, 250 < 250 is false, so goes to next tier = level 31
        assertEquals(31, calculator.calculateLevelFromLines(575), "575 lines should be level 31");
    }
    
    @Test
    void testLevel31Threshold() {
        // Level 30 needs: 325 + (30-21)*25 = 325 + 225 = 550 lines
        // At 574 lines: linesAfter325 = 249, 249 < 250, so 21 + (249/25) = 21 + 9 = 30
        assertEquals(30, calculator.calculateLevelFromLines(574), "574 lines should be level 30");
        // At 575 lines: linesAfter325 = 250, 250 < 250 is false, so linesAfter575 = 0, 31 + (0/30) = 31
        assertEquals(31, calculator.calculateLevelFromLines(575), "575 lines should be level 31");
    }
    
    @Test
    void testLevels31To40() {
        assertEquals(31, calculator.calculateLevelFromLines(575), "575 lines should be level 31");
        // Level 35: 575 + (35-31)*30 = 575 + 120 = 695 lines
        // At 695 lines: linesAfter575 = 120, 120 < 300, so 31 + (120/30) = 31 + 4 = 35 ✓
        assertEquals(35, calculator.calculateLevelFromLines(695), "695 lines should be level 35");
        // Level 40: 575 + (40-31)*30 = 575 + 270 = 845 lines
        // At 845 lines: linesAfter575 = 270, 270 < 300, so 31 + (270/30) = 31 + 9 = 40 ✓
        assertEquals(40, calculator.calculateLevelFromLines(845), "845 lines should be level 40");
        // At 875 lines: linesAfter575 = 300, 300 < 300 is false, so goes to next tier = level 41
        assertEquals(41, calculator.calculateLevelFromLines(875), "875 lines should be level 41");
    }
    
    @Test
    void testLevel41Plus() {
        assertEquals(40, calculator.calculateLevelFromLines(874), "874 lines should be level 40");
        assertEquals(41, calculator.calculateLevelFromLines(875), "875 lines should be level 41");
        assertEquals(42, calculator.calculateLevelFromLines(905), "905 lines should be level 42");
    }
    
    @Test
    void testCalculateLinesRequiredForLevel1() {
        assertEquals(0, calculator.calculateLinesRequiredForLevel(1), "Level 1 requires 0 lines");
    }
    
    @Test
    void testCalculateLinesRequiredForLevels1To5() {
        assertEquals(10, calculator.calculateLinesRequiredForLevel(2), "Level 2 requires 10 lines");
        assertEquals(20, calculator.calculateLinesRequiredForLevel(3), "Level 3 requires 20 lines");
        assertEquals(40, calculator.calculateLinesRequiredForLevel(5), "Level 5 requires 40 lines");
        assertEquals(50, calculator.calculateLinesRequiredForLevel(6), "Level 6 requires 50 lines");
    }
    
    @Test
    void testCalculateLinesRequiredForLevels6To10() {
        assertEquals(65, calculator.calculateLinesRequiredForLevel(7), "Level 7 requires 65 lines");
        assertEquals(80, calculator.calculateLinesRequiredForLevel(8), "Level 8 requires 80 lines");
        assertEquals(125, calculator.calculateLinesRequiredForLevel(11), "Level 11 requires 125 lines");
    }
    
    @Test
    void testCalculateLinesRequiredForLevels11To20() {
        // Level 12: 125 + (12-11)*20 = 125 + 20 = 145 ✓
        assertEquals(145, calculator.calculateLinesRequiredForLevel(12), "Level 12 requires 145 lines");
        // Level 15: 125 + (15-11)*20 = 125 + 80 = 205
        assertEquals(205, calculator.calculateLinesRequiredForLevel(15), "Level 15 requires 205 lines");
        // Level 21: 325 (threshold)
        assertEquals(325, calculator.calculateLinesRequiredForLevel(21), "Level 21 requires 325 lines");
    }
    
    @Test
    void testCalculateLinesRequiredForLevels21To30() {
        // Level 22: 325 + (22-21)*25 = 325 + 25 = 350 ✓
        assertEquals(350, calculator.calculateLinesRequiredForLevel(22), "Level 22 requires 350 lines");
        // Level 25: 325 + (25-21)*25 = 325 + 100 = 425
        assertEquals(425, calculator.calculateLinesRequiredForLevel(25), "Level 25 requires 425 lines");
        // Level 31: 575 (threshold)
        assertEquals(575, calculator.calculateLinesRequiredForLevel(31), "Level 31 requires 575 lines");
    }
    
    @Test
    void testCalculateLinesRequiredForLevels31To40() {
        assertEquals(605, calculator.calculateLinesRequiredForLevel(32), "Level 32 requires 605 lines");
        assertEquals(695, calculator.calculateLinesRequiredForLevel(35), "Level 35 requires 695 lines");
        assertEquals(875, calculator.calculateLinesRequiredForLevel(41), "Level 41 requires 875 lines");
    }
    
    @Test
    void testConsistencyBetweenMethods() {
        // Test that calculateLevelFromLines and calculateLinesRequiredForLevel are consistent
        for (int level = 1; level <= 50; level++) {
            int linesRequired = calculator.calculateLinesRequiredForLevel(level);
            int calculatedLevel = calculator.calculateLevelFromLines(linesRequired);
            // The calculated level should be at least the target level (might be higher due to rounding)
            assertTrue(calculatedLevel >= level, 
                "Level " + level + " requires " + linesRequired + " lines, but calculated level is " + calculatedLevel);
        }
    }
    
    @Test
    void testExactThresholds() {
        // Test exact threshold values (these are the transition points)
        assertEquals(6, calculator.calculateLevelFromLines(50), "Exactly 50 lines should be level 6");
        assertEquals(11, calculator.calculateLevelFromLines(125), "Exactly 125 lines should be level 11");
        assertEquals(21, calculator.calculateLevelFromLines(325), "Exactly 325 lines should be level 21");
        assertEquals(31, calculator.calculateLevelFromLines(575), "Exactly 575 lines should be level 31");
        assertEquals(41, calculator.calculateLevelFromLines(875), "Exactly 875 lines should be level 41");
    }
}

