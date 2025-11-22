package com.comp2042.rpg.ability;

import com.comp2042.rpg.AbilityType;

/**
 * Manages RPG abilities (charges, slots, execution).
 * Extracted from GameController for better organization.
 */
public class AbilityManager {
    
    private int clearRowsCharges = 0;
    private int slowTimeCharges = 0;
    private int colorBombCharges = 0;
    private int colorSyncCharges = 0;
    
    private final AbilityType[] abilitySlots = {
        AbilityType.NONE,
        AbilityType.NONE,
        AbilityType.NONE,
        AbilityType.NONE
    };
    
    public int getClearRowsCharges() { return clearRowsCharges; }
    public int getSlowTimeCharges() { return slowTimeCharges; }
    public int getColorBombCharges() { return colorBombCharges; }
    public int getColorSyncCharges() { return colorSyncCharges; }
    
    public void incrementClearRowsCharges() { clearRowsCharges++; }
    public void incrementSlowTimeCharges() { slowTimeCharges++; }
    public void incrementColorBombCharges() { colorBombCharges++; }
    public void incrementColorSyncCharges() { colorSyncCharges++; }
    
    public void decrementClearRowsCharges() { clearRowsCharges = Math.max(0, clearRowsCharges - 1); }
    public void decrementSlowTimeCharges() { slowTimeCharges = Math.max(0, slowTimeCharges - 1); }
    public void decrementColorBombCharges() { colorBombCharges = Math.max(0, colorBombCharges - 1); }
    public void decrementColorSyncCharges() { colorSyncCharges = Math.max(0, colorSyncCharges - 1); }
    
    public AbilityType[] getAbilitySlots() { return abilitySlots; }
    
    public AbilityType mapAbilityType(String abilityName) {
        switch (abilityName) {
            case "Clear 3 Rows":
                return AbilityType.CLEAR_ROWS;
            case "Slow Time":
                return AbilityType.SLOW_TIME;
            case "Color Bomb":
                return AbilityType.COLOR_BOMB;
            case "Color Sync":
                return AbilityType.COLOR_SYNC;
            default:
                return AbilityType.NONE;
        }
    }
    
    public void assignAbilityToSlot(AbilityType type) {
        if (type == AbilityType.NONE) {
            return;
        }
        // First, check if this ability type already exists in a slot
        for (int i = 0; i < abilitySlots.length; i++) {
            if (abilitySlots[i] == type) {
                // Ability already in a slot, don't add it again
                return;
            }
        }
        // If not found, find the first empty slot and assign it there
        for (int i = 0; i < abilitySlots.length; i++) {
            if (abilitySlots[i] == AbilityType.NONE) {
                abilitySlots[i] = type;
                return;
            }
        }
    }
    
    public void removeAbilityFromSlots(AbilityType type) {
        if (type == AbilityType.NONE) {
            return;
        }
        for (int i = 0; i < abilitySlots.length; i++) {
            if (abilitySlots[i] == type) {
                abilitySlots[i] = AbilityType.NONE;
            }
        }
    }
    
    public String getAbilitySlotText(int slotIndex) {
        AbilityType type = abilitySlots[slotIndex];
        String prefix = "[" + (slotIndex + 1) + "]";
        switch (type) {
            case CLEAR_ROWS:
                return clearRowsCharges > 0 ? prefix + " Clear 3 Rows (x" + clearRowsCharges + ")" : prefix + " Clear 3 Rows (x0)";
            case SLOW_TIME:
                return slowTimeCharges > 0 ? prefix + " Slow Time (x" + slowTimeCharges + ")" : prefix + " Slow Time (x0)";
            case COLOR_BOMB:
                return colorBombCharges > 0 ? prefix + " Color Bomb (x" + colorBombCharges + ")" : prefix + " Color Bomb (x0)";
            case COLOR_SYNC:
                return colorSyncCharges > 0 ? prefix + " Color Sync (x" + colorSyncCharges + ")" : prefix + " Color Sync (x0)";
            default:
                return prefix + " None";
        }
    }
    
    public int findAbilitySlotIndex(AbilityType target) {
        for (int i = 0; i < abilitySlots.length; i++) {
            if (abilitySlots[i] == target) {
                return i;
            }
        }
        return -1;
    }
    
    public String getAbilityDisplayName(String abilityType) {
        switch (abilityType) {
            case "CLEAR_BOTTOM_3": return "Clear 3 Rows";
            case "SLOW_TIME": return "Slow Time";
            case "COLOR_BOMB": return "Color Bomb";
            case "COLOR_SYNC": return "Color Sync";
            default: return "Unknown";
        }
    }
    
    public int getChargesForType(AbilityType type) {
        switch (type) {
            case CLEAR_ROWS: return clearRowsCharges;
            case SLOW_TIME: return slowTimeCharges;
            case COLOR_BOMB: return colorBombCharges;
            case COLOR_SYNC: return colorSyncCharges;
            default: return 0;
        }
    }
    
    public boolean hasCharges(AbilityType type) {
        return getChargesForType(type) > 0;
    }
}

