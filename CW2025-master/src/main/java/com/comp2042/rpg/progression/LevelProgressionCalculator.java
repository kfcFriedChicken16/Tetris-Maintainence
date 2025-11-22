package com.comp2042.rpg.progression;

/**
 * Calculates RPG level progression.
 * Extracted from GameController for better organization.
 */
public class LevelProgressionCalculator {
    
    /**
     * Calculate RPG level from total lines cleared.
     * Level 1-5: 10 lines per level (Level 6 needs 50 lines total)
     * Level 6-10: 15 lines per level (Level 11 needs 125 lines total)
     * Level 11-20: 20 lines per level (Level 21 needs 325 lines total)
     * Level 21-30: 25 lines per level (Level 31 needs 575 lines total)
     * Level 31-40: 30 lines per level (Level 41 needs 875 lines total)
     */
    public int calculateLevelFromLines(int totalLinesCleared) {
        if (totalLinesCleared < 10) {
            return 1; // Level 1: 0-9 lines
        }
        
        // Levels 1-5: 10 lines each (total 50 lines for level 6)
        if (totalLinesCleared < 50) {
            return (totalLinesCleared / 10) + 1; // Level 2-5
        }
        
        // Levels 6-10: 15 lines each (after 50 lines, need 75 more = 125 total for level 11)
        int linesAfter50 = totalLinesCleared - 50;
        if (linesAfter50 < 75) {
            return 6 + (linesAfter50 / 15); // Level 6-10
        }
        
        // Levels 11-20: 20 lines each (after 125 lines, need 200 more = 325 total for level 21)
        int linesAfter125 = totalLinesCleared - 125;
        if (linesAfter125 < 200) {
            return 11 + (linesAfter125 / 20); // Level 11-20
        }
        
        // Levels 21-30: 25 lines each (after 325 lines, need 250 more = 575 total for level 31)
        int linesAfter325 = totalLinesCleared - 325;
        if (linesAfter325 < 250) {
            return 21 + (linesAfter325 / 25); // Level 21-30
        }
        
        // Levels 31-40: 30 lines each (after 575 lines, need 300 more = 875 total for level 41)
        int linesAfter575 = totalLinesCleared - 575;
        if (linesAfter575 < 300) {
            return 31 + (linesAfter575 / 30); // Level 31-40
        }
        
        // Level 41+: Continue with 30 lines per level
        return 41 + ((totalLinesCleared - 875) / 30);
    }
    
    /**
     * Calculate total lines required to reach a specific level.
     * Level 1-5: 10 lines each (Level 6 needs 50 lines total)
     * Level 6-10: 15 lines each (Level 11 needs 125 lines total)
     * Level 11-20: 20 lines each (Level 21 needs 325 lines total)
     * Level 21-30: 25 lines each (Level 31 needs 575 lines total)
     * Level 31-40: 30 lines each (Level 41 needs 875 lines total)
     */
    public int calculateLinesRequiredForLevel(int targetLevel) {
        if (targetLevel <= 1) {
            return 0; // Level 1 starts at 0 lines
        }
        if (targetLevel <= 6) {
            // Levels 1-5: 10 lines each
            return (targetLevel - 1) * 10;
        }
        if (targetLevel <= 11) {
            // Levels 6-10: 15 lines each (50 base + 15 per level after 5)
            return 50 + (targetLevel - 6) * 15;
        }
        if (targetLevel <= 21) {
            // Levels 11-20: 20 lines each (125 base + 20 per level after 10)
            return 125 + (targetLevel - 11) * 20;
        }
        if (targetLevel <= 31) {
            // Levels 21-30: 25 lines each (325 base + 25 per level after 20)
            return 325 + (targetLevel - 21) * 25;
        }
        if (targetLevel <= 41) {
            // Levels 31-40: 30 lines each (575 base + 30 per level after 30)
            return 575 + (targetLevel - 31) * 30;
        }
        
        // Level 41+: Continue with 30 lines per level
        return 875 + (targetLevel - 41) * 30;
    }
}

