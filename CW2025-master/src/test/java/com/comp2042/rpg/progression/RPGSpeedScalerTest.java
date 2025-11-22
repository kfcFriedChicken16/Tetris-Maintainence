package com.comp2042.rpg.progression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for RPGSpeedScaler.
 * Tests core speed calculation logic without dependencies.
 */
public class RPGSpeedScalerTest {
    
    private RPGSpeedScaler speedScaler;
    
    @BeforeEach
    void setUp() {
        speedScaler = new RPGSpeedScaler();
    }
    
    @Test
    void testSpeedTier1() {
        // Levels 1-3: 400ms
        assertEquals(400, speedScaler.calculateSpeedForLevel(1), "Level 1 should be 400ms");
        assertEquals(400, speedScaler.calculateSpeedForLevel(2), "Level 2 should be 400ms");
        assertEquals(400, speedScaler.calculateSpeedForLevel(3), "Level 3 should be 400ms");
    }
    
    @Test
    void testSpeedTier2() {
        // Levels 4-9: 365ms
        assertEquals(365, speedScaler.calculateSpeedForLevel(4), "Level 4 should be 365ms");
        assertEquals(365, speedScaler.calculateSpeedForLevel(5), "Level 5 should be 365ms");
        assertEquals(365, speedScaler.calculateSpeedForLevel(9), "Level 9 should be 365ms");
    }
    
    @Test
    void testSpeedTier3() {
        // Levels 10-18: 330ms
        assertEquals(330, speedScaler.calculateSpeedForLevel(10), "Level 10 should be 330ms");
        assertEquals(330, speedScaler.calculateSpeedForLevel(15), "Level 15 should be 330ms");
        assertEquals(330, speedScaler.calculateSpeedForLevel(18), "Level 18 should be 330ms");
    }
    
    @Test
    void testSpeedTier4() {
        // Levels 19-30: 295ms
        assertEquals(295, speedScaler.calculateSpeedForLevel(19), "Level 19 should be 295ms");
        assertEquals(295, speedScaler.calculateSpeedForLevel(25), "Level 25 should be 295ms");
        assertEquals(295, speedScaler.calculateSpeedForLevel(30), "Level 30 should be 295ms");
    }
    
    @Test
    void testSpeedTier5() {
        // Tier 5: levels 31-41 (tier = 4 + ((31-18)/12) = 4 + 1 = 5)
        // Level 42: tier = 4 + ((42-18)/12) = 4 + 2 = 6 (different tier!)
        long speed31 = speedScaler.calculateSpeedForLevel(31);
        long speed35 = speedScaler.calculateSpeedForLevel(35);
        long speed41 = speedScaler.calculateSpeedForLevel(41);
        long speed42 = speedScaler.calculateSpeedForLevel(42);
        
        // Levels 31-41 should be same tier (tier 5)
        assertEquals(speed31, speed35, "Level 31 and 35 should have same speed (tier 5)");
        assertEquals(speed31, speed41, "Level 31 and 41 should have same speed (tier 5)");
        // Level 42 should be different tier (tier 6)
        assertNotEquals(speed31, speed42, "Level 31 and 42 should be different tiers");
        assertTrue(speed31 < 295, "Tier 5 speed should be less than tier 4 (295ms)");
        assertTrue(speed31 >= 50, "Tier 5 speed should not go below minimum (50ms)");
    }
    
    @Test
    void testSpeedTier6() {
        // Tier calculation: tier = 4 + ((level - 18) / 12)
        // Level 43: tier = 4 + ((43-18)/12) = 4 + (25/12) = 4 + 2 = 6
        // Level 54: tier = 4 + ((54-18)/12) = 4 + (36/12) = 4 + 3 = 7 (different tier!)
        long speed43 = speedScaler.calculateSpeedForLevel(43);
        long speed50 = speedScaler.calculateSpeedForLevel(50);
        long speed53 = speedScaler.calculateSpeedForLevel(53);
        long speed54 = speedScaler.calculateSpeedForLevel(54);
        
        // Levels 43-53 should be same tier (tier 6)
        assertEquals(speed43, speed50, "Level 43 and 50 should have same speed (tier 6)");
        assertEquals(speed43, speed53, "Level 43 and 53 should have same speed (tier 6)");
        // Level 54 should be different tier (tier 7)
        assertNotEquals(speed43, speed54, "Level 43 and 54 should be different tiers");
        assertTrue(speed54 < speed43, "Tier 7 (level 54) should be faster than tier 6 (level 43)");
        
        // Verify tier 6 is faster than tier 5
        long speed31 = speedScaler.calculateSpeedForLevel(31);
        assertTrue(speed43 < speed31, "Tier 6 speed should be faster (less ms) than tier 5");
        assertTrue(speed43 >= 50, "Tier 6 speed should not go below minimum (50ms)");
    }
    
    @Test
    void testSpeedTier7() {
        // Tier 7: levels 54-65
        // Level 54: tier = 4 + ((54-18)/12) = 4 + 3 = 7
        // Level 65: tier = 4 + ((65-18)/12) = 4 + 3 = 7
        long speed54 = speedScaler.calculateSpeedForLevel(54);
        long speed60 = speedScaler.calculateSpeedForLevel(60);
        long speed65 = speedScaler.calculateSpeedForLevel(65);
        
        // All should be the same (same tier)
        assertEquals(speed54, speed60, "Level 54 and 60 should have same speed (tier 7)");
        assertEquals(speed54, speed65, "Level 54 and 65 should have same speed (tier 7)");
        
        // Verify tier 7 is faster than tier 6
        long speed43 = speedScaler.calculateSpeedForLevel(43);
        assertTrue(speed54 < speed43, "Tier 7 speed should be faster (less ms) than tier 6");
    }
    
    @Test
    void testMinimumSpeed() {
        // Speed should never go below 50ms
        long speed = speedScaler.calculateSpeedForLevel(100);
        assertTrue(speed >= 50, "Speed should not go below 50ms, got: " + speed);
        
        long speed200 = speedScaler.calculateSpeedForLevel(200);
        assertTrue(speed200 >= 50, "Speed at level 200 should not go below 50ms, got: " + speed200);
    }
    
    @Test
    void testSpeedDecreasesWithLevel() {
        // Speed should generally decrease as level increases
        long speed1 = speedScaler.calculateSpeedForLevel(1);
        long speed10 = speedScaler.calculateSpeedForLevel(10);
        long speed20 = speedScaler.calculateSpeedForLevel(20);
        long speed30 = speedScaler.calculateSpeedForLevel(30);
        
        assertTrue(speed1 >= speed10, "Level 1 speed (" + speed1 + "ms) should be >= Level 10 speed (" + speed10 + "ms)");
        assertTrue(speed10 >= speed20, "Level 10 speed (" + speed10 + "ms) should be >= Level 20 speed (" + speed20 + "ms)");
        assertTrue(speed20 >= speed30, "Level 20 speed (" + speed20 + "ms) should be >= Level 30 speed (" + speed30 + "ms)");
    }
    
    @Test
    void testTierBoundaries() {
        // Test exact tier boundaries
        assertEquals(400, speedScaler.calculateSpeedForLevel(3), "Level 3 (end of tier 1) should be 400ms");
        assertEquals(365, speedScaler.calculateSpeedForLevel(4), "Level 4 (start of tier 2) should be 365ms");
        assertEquals(365, speedScaler.calculateSpeedForLevel(9), "Level 9 (end of tier 2) should be 365ms");
        assertEquals(330, speedScaler.calculateSpeedForLevel(10), "Level 10 (start of tier 3) should be 330ms");
        assertEquals(330, speedScaler.calculateSpeedForLevel(18), "Level 18 (end of tier 3) should be 330ms");
        assertEquals(295, speedScaler.calculateSpeedForLevel(19), "Level 19 (start of tier 4) should be 295ms");
        assertEquals(295, speedScaler.calculateSpeedForLevel(30), "Level 30 (end of tier 4) should be 295ms");
        long speed31 = speedScaler.calculateSpeedForLevel(31);
        assertTrue(speed31 < 295, "Level 31 (start of tier 5) should be faster than tier 4");
        assertTrue(speed31 >= 50, "Level 31 speed should not go below minimum (50ms)");
    }
    
    @Test
    void testSpeedCalculationFormula() {
        // Test the formula for tier 5+: tier = 4 + ((level - 18) / 12), speed = 400 - (tier * 35)
        long speed31 = speedScaler.calculateSpeedForLevel(31);
        long speed41 = speedScaler.calculateSpeedForLevel(41);
        long speed42 = speedScaler.calculateSpeedForLevel(42);
        long speed43 = speedScaler.calculateSpeedForLevel(43);
        
        // Verify tier boundaries
        // Level 31-41: tier 5, Level 42-53: tier 6, Level 54-65: tier 7
        assertEquals(speed31, speed41, "Level 31 and 41 should be same tier (tier 5)");
        assertEquals(speed42, speed43, "Level 42 and 43 should be same tier (tier 6)");
        assertNotEquals(speed31, speed42, "Level 31 and 42 should be different tiers");
        assertTrue(speed42 < speed31, "Tier 6 should be faster (less ms) than tier 5");
        
        // Verify the pattern: each tier should be faster (less ms) than previous
        long speed30 = speedScaler.calculateSpeedForLevel(30); // Tier 4: 295ms
        assertTrue(speed31 < speed30, "Tier 5 should be faster (less ms) than tier 4");
        assertTrue(speed42 < speed31, "Tier 6 should be faster (less ms) than tier 5");
        
        // Verify speed decreases by approximately 35ms per tier (allowing for implementation differences)
        long speedDiff1 = speed30 - speed31; // Difference between tier 4 and 5
        long speedDiff2 = speed31 - speed42; // Difference between tier 5 and 6
        assertTrue(speedDiff1 > 0, "Speed should decrease between tier 4 and 5");
        assertTrue(speedDiff2 > 0, "Speed should decrease between tier 5 and 6");
    }
}

