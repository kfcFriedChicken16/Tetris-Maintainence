package com.comp2042.rpg.ability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.comp2042.rpg.AbilityType;

/**
 * JUnit tests for AbilityManager.
 * Tests core ability management logic without dependencies.
 */
public class AbilityManagerTest {
    
    private AbilityManager abilityManager;
    
    @BeforeEach
    void setUp() {
        abilityManager = new AbilityManager();
    }
    
    @Test
    void testInitialState() {
        // All charges should start at 0
        assertEquals(0, abilityManager.getClearRowsCharges(), "Clear Rows charges should start at 0");
        assertEquals(0, abilityManager.getSlowTimeCharges(), "Slow Time charges should start at 0");
        assertEquals(0, abilityManager.getColorBombCharges(), "Color Bomb charges should start at 0");
        assertEquals(0, abilityManager.getColorSyncCharges(), "Color Sync charges should start at 0");
        
        // All slots should be NONE
        AbilityType[] slots = abilityManager.getAbilitySlots();
        for (int i = 0; i < slots.length; i++) {
            assertEquals(AbilityType.NONE, slots[i], "Slot " + i + " should be NONE initially");
        }
    }
    
    @Test
    void testIncrementCharges() {
        abilityManager.incrementClearRowsCharges();
        assertEquals(1, abilityManager.getClearRowsCharges(), "Clear Rows charges should be 1");
        
        abilityManager.incrementSlowTimeCharges();
        assertEquals(1, abilityManager.getSlowTimeCharges(), "Slow Time charges should be 1");
        
        abilityManager.incrementColorBombCharges();
        assertEquals(1, abilityManager.getColorBombCharges(), "Color Bomb charges should be 1");
        
        abilityManager.incrementColorSyncCharges();
        assertEquals(1, abilityManager.getColorSyncCharges(), "Color Sync charges should be 1");
    }
    
    @Test
    void testMultipleIncrements() {
        abilityManager.incrementClearRowsCharges();
        abilityManager.incrementClearRowsCharges();
        abilityManager.incrementClearRowsCharges();
        
        assertEquals(3, abilityManager.getClearRowsCharges(), "Clear Rows charges should be 3");
    }
    
    @Test
    void testDecrementCharges() {
        abilityManager.incrementClearRowsCharges();
        abilityManager.incrementClearRowsCharges();
        
        abilityManager.decrementClearRowsCharges();
        assertEquals(1, abilityManager.getClearRowsCharges(), "Clear Rows charges should be 1 after decrement");
        
        abilityManager.decrementClearRowsCharges();
        assertEquals(0, abilityManager.getClearRowsCharges(), "Clear Rows charges should be 0 after second decrement");
    }
    
    @Test
    void testDecrementBelowZero() {
        // Decrementing when charges are 0 should not go negative
        abilityManager.decrementClearRowsCharges();
        assertEquals(0, abilityManager.getClearRowsCharges(), "Charges should not go below 0");
        
        abilityManager.decrementClearRowsCharges();
        abilityManager.decrementClearRowsCharges();
        assertEquals(0, abilityManager.getClearRowsCharges(), "Charges should remain at 0");
    }
    
    @Test
    void testMapAbilityType() {
        assertEquals(AbilityType.CLEAR_ROWS, abilityManager.mapAbilityType("Clear 3 Rows"), "Should map to CLEAR_ROWS");
        assertEquals(AbilityType.SLOW_TIME, abilityManager.mapAbilityType("Slow Time"), "Should map to SLOW_TIME");
        assertEquals(AbilityType.COLOR_BOMB, abilityManager.mapAbilityType("Color Bomb"), "Should map to COLOR_BOMB");
        assertEquals(AbilityType.COLOR_SYNC, abilityManager.mapAbilityType("Color Sync"), "Should map to COLOR_SYNC");
        assertEquals(AbilityType.NONE, abilityManager.mapAbilityType("Unknown"), "Unknown should map to NONE");
        assertEquals(AbilityType.NONE, abilityManager.mapAbilityType(""), "Empty string should map to NONE");
        // Note: null will throw NullPointerException with switch statement, which is acceptable
    }
    
    @Test
    void testAssignAbilityToSlot() {
        abilityManager.assignAbilityToSlot(AbilityType.CLEAR_ROWS);
        
        AbilityType[] slots = abilityManager.getAbilitySlots();
        assertEquals(AbilityType.CLEAR_ROWS, slots[0], "First slot should have CLEAR_ROWS");
        assertEquals(AbilityType.NONE, slots[1], "Second slot should still be NONE");
    }
    
    @Test
    void testAssignMultipleAbilities() {
        abilityManager.assignAbilityToSlot(AbilityType.CLEAR_ROWS);
        abilityManager.assignAbilityToSlot(AbilityType.SLOW_TIME);
        abilityManager.assignAbilityToSlot(AbilityType.COLOR_BOMB);
        
        AbilityType[] slots = abilityManager.getAbilitySlots();
        assertEquals(AbilityType.CLEAR_ROWS, slots[0], "Slot 0 should have CLEAR_ROWS");
        assertEquals(AbilityType.SLOW_TIME, slots[1], "Slot 1 should have SLOW_TIME");
        assertEquals(AbilityType.COLOR_BOMB, slots[2], "Slot 2 should have COLOR_BOMB");
        assertEquals(AbilityType.NONE, slots[3], "Slot 3 should still be NONE");
    }
    
    @Test
    void testAssignDuplicateAbility() {
        abilityManager.assignAbilityToSlot(AbilityType.CLEAR_ROWS);
        abilityManager.assignAbilityToSlot(AbilityType.CLEAR_ROWS); // Try to assign again
        
        AbilityType[] slots = abilityManager.getAbilitySlots();
        int count = 0;
        for (AbilityType slot : slots) {
            if (slot == AbilityType.CLEAR_ROWS) {
                count++;
            }
        }
        assertEquals(1, count, "CLEAR_ROWS should only appear once in slots");
    }
    
    @Test
    void testAssignNoneAbility() {
        abilityManager.assignAbilityToSlot(AbilityType.NONE);
        
        AbilityType[] slots = abilityManager.getAbilitySlots();
        for (AbilityType slot : slots) {
            assertEquals(AbilityType.NONE, slot, "Assigning NONE should not change slots");
        }
    }
    
    @Test
    void testRemoveAbilityFromSlots() {
        abilityManager.assignAbilityToSlot(AbilityType.CLEAR_ROWS);
        abilityManager.assignAbilityToSlot(AbilityType.SLOW_TIME);
        
        abilityManager.removeAbilityFromSlots(AbilityType.CLEAR_ROWS);
        
        AbilityType[] slots = abilityManager.getAbilitySlots();
        assertEquals(AbilityType.NONE, slots[0], "Slot 0 should be NONE after removal");
        // Note: removeAbilityFromSlots doesn't shift, it just sets to NONE
        // So slot 1 should still have SLOW_TIME
        assertEquals(AbilityType.SLOW_TIME, slots[1], "Slot 1 should still have SLOW_TIME");
    }
    
    @Test
    void testRemoveNoneAbility() {
        abilityManager.assignAbilityToSlot(AbilityType.CLEAR_ROWS);
        abilityManager.removeAbilityFromSlots(AbilityType.NONE);
        
        AbilityType[] slots = abilityManager.getAbilitySlots();
        assertEquals(AbilityType.CLEAR_ROWS, slots[0], "Removing NONE should not affect existing abilities");
    }
    
    @Test
    void testGetAbilitySlotText() {
        // Test empty slot
        String emptyText = abilityManager.getAbilitySlotText(0);
        assertEquals("[1] None", emptyText, "Empty slot should show 'None'");
        
        // Test with ability but no charges
        abilityManager.assignAbilityToSlot(AbilityType.CLEAR_ROWS);
        String noChargesText = abilityManager.getAbilitySlotText(0);
        assertEquals("[1] Clear 3 Rows (x0)", noChargesText, "Ability with 0 charges should show (x0)");
        
        // Test with charges
        abilityManager.incrementClearRowsCharges();
        abilityManager.incrementClearRowsCharges();
        String withChargesText = abilityManager.getAbilitySlotText(0);
        assertEquals("[1] Clear 3 Rows (x2)", withChargesText, "Ability with 2 charges should show (x2)");
    }
    
    @Test
    void testGetAbilitySlotTextForAllTypes() {
        abilityManager.assignAbilityToSlot(AbilityType.CLEAR_ROWS);
        abilityManager.assignAbilityToSlot(AbilityType.SLOW_TIME);
        abilityManager.assignAbilityToSlot(AbilityType.COLOR_BOMB);
        abilityManager.assignAbilityToSlot(AbilityType.COLOR_SYNC);
        
        abilityManager.incrementClearRowsCharges();
        abilityManager.incrementSlowTimeCharges();
        abilityManager.incrementColorBombCharges();
        abilityManager.incrementColorSyncCharges();
        
        assertTrue(abilityManager.getAbilitySlotText(0).contains("Clear 3 Rows"), "Slot 0 should show Clear 3 Rows");
        assertTrue(abilityManager.getAbilitySlotText(1).contains("Slow Time"), "Slot 1 should show Slow Time");
        assertTrue(abilityManager.getAbilitySlotText(2).contains("Color Bomb"), "Slot 2 should show Color Bomb");
        assertTrue(abilityManager.getAbilitySlotText(3).contains("Color Sync"), "Slot 3 should show Color Sync");
    }
    
    @Test
    void testFindAbilitySlotIndex() {
        abilityManager.assignAbilityToSlot(AbilityType.CLEAR_ROWS);
        abilityManager.assignAbilityToSlot(AbilityType.SLOW_TIME);
        
        int clearRowsIndex = abilityManager.findAbilitySlotIndex(AbilityType.CLEAR_ROWS);
        int slowTimeIndex = abilityManager.findAbilitySlotIndex(AbilityType.SLOW_TIME);
        int notFoundIndex = abilityManager.findAbilitySlotIndex(AbilityType.COLOR_BOMB);
        
        assertEquals(0, clearRowsIndex, "CLEAR_ROWS should be at index 0");
        assertEquals(1, slowTimeIndex, "SLOW_TIME should be at index 1");
        assertEquals(-1, notFoundIndex, "COLOR_BOMB should not be found (-1)");
    }
    
    @Test
    void testGetAbilityDisplayName() {
        assertEquals("Clear 3 Rows", abilityManager.getAbilityDisplayName("CLEAR_BOTTOM_3"), "Should return Clear 3 Rows");
        assertEquals("Slow Time", abilityManager.getAbilityDisplayName("SLOW_TIME"), "Should return Slow Time");
        assertEquals("Color Bomb", abilityManager.getAbilityDisplayName("COLOR_BOMB"), "Should return Color Bomb");
        assertEquals("Color Sync", abilityManager.getAbilityDisplayName("COLOR_SYNC"), "Should return Color Sync");
        assertEquals("Unknown", abilityManager.getAbilityDisplayName("UNKNOWN"), "Unknown type should return Unknown");
    }
    
    @Test
    void testGetChargesForType() {
        abilityManager.incrementClearRowsCharges();
        abilityManager.incrementClearRowsCharges();
        abilityManager.incrementSlowTimeCharges();
        
        assertEquals(2, abilityManager.getChargesForType(AbilityType.CLEAR_ROWS), "CLEAR_ROWS should have 2 charges");
        assertEquals(1, abilityManager.getChargesForType(AbilityType.SLOW_TIME), "SLOW_TIME should have 1 charge");
        assertEquals(0, abilityManager.getChargesForType(AbilityType.COLOR_BOMB), "COLOR_BOMB should have 0 charges");
        assertEquals(0, abilityManager.getChargesForType(AbilityType.NONE), "NONE should have 0 charges");
    }
    
    @Test
    void testHasCharges() {
        assertFalse(abilityManager.hasCharges(AbilityType.CLEAR_ROWS), "Should not have charges initially");
        
        abilityManager.incrementClearRowsCharges();
        assertTrue(abilityManager.hasCharges(AbilityType.CLEAR_ROWS), "Should have charges after increment");
        
        abilityManager.decrementClearRowsCharges();
        assertFalse(abilityManager.hasCharges(AbilityType.CLEAR_ROWS), "Should not have charges after decrement to 0");
    }
    
    @Test
    void testSlotCapacity() {
        // Fill all 4 slots
        abilityManager.assignAbilityToSlot(AbilityType.CLEAR_ROWS);
        abilityManager.assignAbilityToSlot(AbilityType.SLOW_TIME);
        abilityManager.assignAbilityToSlot(AbilityType.COLOR_BOMB);
        abilityManager.assignAbilityToSlot(AbilityType.COLOR_SYNC);
        
        // Try to assign a 5th ability (should not work, all slots full)
        // Since we can't assign COLOR_SYNC again (duplicate check), let's verify all slots are filled
        AbilityType[] slots = abilityManager.getAbilitySlots();
        int filledSlots = 0;
        for (AbilityType slot : slots) {
            if (slot != AbilityType.NONE) {
                filledSlots++;
            }
        }
        assertEquals(4, filledSlots, "All 4 slots should be filled");
    }
}

