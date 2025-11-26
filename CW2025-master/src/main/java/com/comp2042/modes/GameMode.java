package com.comp2042.modes;

/**
 * Enum representing different game modes available in Tetris.
 * Each mode has a display name and description for the UI.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public enum GameMode {
    CLASSIC("Classic", "Endless Tetris - Play until you lose"),
    SPRINT("Sprint", "Clear 40 lines as fast as possible"),
    ULTRA("Ultra", "Score as much as possible in 2 minutes"),
    SURVIVAL("Survival", "Speed increases as you survive longer"),
    RPG("RPG Mode", "Level up and gain abilities every 5 lines cleared");
    
    private final String displayName;
    private final String description;
    
    /**
     * Constructs a GameMode enum value with display information.
     * 
     * @param displayName The name to display in the UI
     * @param description The description of the game mode
     */
    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the display name of this game mode.
     * 
     * @return The display name string
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of this game mode.
     * 
     * @return The description string
     */
    public String getDescription() {
        return description;
    }
}

