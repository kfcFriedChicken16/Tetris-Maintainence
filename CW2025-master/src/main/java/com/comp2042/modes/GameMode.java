package com.comp2042.modes;

/**
 * Enum representing different game modes in Tetris
 */
public enum GameMode {
    CLASSIC("Classic", "Endless Tetris - Play until you lose"),
    SPRINT("Sprint", "Clear 40 lines as fast as possible"),
    ULTRA("Ultra", "Score as much as possible in 2 minutes"),
    SURVIVAL("Survival", "Speed increases as you survive longer");
    
    private final String displayName;
    private final String description;
    
    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}

