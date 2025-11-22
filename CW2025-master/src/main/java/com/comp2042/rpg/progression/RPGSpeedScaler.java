package com.comp2042.rpg.progression;

/**
 * Updates game speed based on RPG level.
 * Extracted from GameController for better organization.
 */
public class RPGSpeedScaler {
    
    /**
     * Update game speed based on RPG level.
     * Speed tiers based on level ranges:
     * - Levels 1-3 (first 3 levels): 400ms
     * - Levels 4-9 (next 6 levels): 365ms
     * - Levels 10-18 (next 9 levels): 330ms
     * - Levels 19-30 (next 12 levels): 295ms
     * - Levels 31+ (next 12 each): continues decreasing by 35ms per tier
     * Minimum speed: 50ms
     */
    public long calculateSpeedForLevel(int level) {
        long newSpeed = 400; // Default/base speed
        
        if (level <= 3) {
            // First 3 levels: 400ms
            newSpeed = 400;
        } else if (level <= 9) {
            // Next 6 levels (4-9): 365ms
            newSpeed = 365;
        } else if (level <= 18) {
            // Next 9 levels (10-18): 330ms
            newSpeed = 330;
        } else if (level <= 30) {
            // Next 12 levels (19-30): 295ms
            newSpeed = 295;
        } else {
            // Levels 31+: Continue pattern, each 12 levels reduces by 35ms
            // Tier 5: 31-42 (260ms), Tier 6: 43-54 (225ms), etc.
            int tier = 4 + ((level - 18) / 12); // Tier 4 was 19-30, so tier 5 starts at 31
            newSpeed = 400 - (tier * 35);
            // Ensure minimum speed of 50ms
            newSpeed = Math.max(newSpeed, 50);
        }
        
        return newSpeed;
    }
}

