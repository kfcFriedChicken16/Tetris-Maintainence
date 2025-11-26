package com.comp2042.rpg;

import com.comp2042.core.Board;
import com.comp2042.core.TetrisBoard;
import com.comp2042.models.ViewData;
import com.comp2042.rpg.ability.AbilityManager;
import com.comp2042.rpg.config.RPGConfig;
import com.comp2042.rpg.progression.LevelProgressionCalculator;
import com.comp2042.rpg.progression.RPGSpeedScaler;

/**
 * Manages all RPG mode functionality including level progression, ability management,
 * speed scaling, and garbage block spawning.
 * Handles RPG-specific game mechanics and coordinates with AbilityManager for ability execution.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public class RPGModeManager {
    
    private int rpgLevel = 1;
    private final AbilityManager abilityManager;
    private final LevelProgressionCalculator levelCalculator;
    private final RPGSpeedScaler speedScaler;
    
    /**
     * Constructs a new RPGModeManager and initializes all RPG subsystems.
     * Creates AbilityManager, LevelProgressionCalculator, and RPGSpeedScaler instances.
     */
    public RPGModeManager() {
        this.abilityManager = new AbilityManager();
        this.levelCalculator = new LevelProgressionCalculator();
        this.speedScaler = new RPGSpeedScaler();
    }
    
    /**
     * Gets the current RPG level.
     * 
     * @return The current level (starts at 1)
     */
    public int getRpgLevel() { return rpgLevel; }
    
    /**
     * Sets the RPG level.
     * 
     * @param level The level to set
     */
    public void setRpgLevel(int level) { this.rpgLevel = level; }
    
    /**
     * Gets the AbilityManager instance for managing RPG abilities.
     * 
     * @return The AbilityManager instance
     */
    public AbilityManager getAbilityManager() { return abilityManager; }
    
    /**
     * Gets the LevelProgressionCalculator for calculating level progression.
     * 
     * @return The LevelProgressionCalculator instance
     */
    public LevelProgressionCalculator getLevelCalculator() { return levelCalculator; }
    
    /**
     * Gets the RPGSpeedScaler for calculating game speed based on level.
     * 
     * @return The RPGSpeedScaler instance
     */
    public RPGSpeedScaler getSpeedScaler() { return speedScaler; }
    
    /**
     * Calculates the RPG level based on total lines cleared.
     * Uses different line requirements per level tier.
     * 
     * @param totalLinesCleared The total number of lines cleared since game start
     * @return The calculated RPG level
     */
    public int calculateLevelFromLines(int totalLinesCleared) {
        return levelCalculator.calculateLevelFromLines(totalLinesCleared);
    }
    
    /**
     * Calculates the total lines required to reach a specific level.
     * 
     * @param targetLevel The target level to calculate lines for
     * @return The total number of lines needed to reach the target level
     */
    public int calculateLinesRequiredForLevel(int targetLevel) {
        return levelCalculator.calculateLinesRequiredForLevel(targetLevel);
    }
    
    /**
     * Calculates the game speed (drop interval in milliseconds) based on RPG level.
     * Speed increases (interval decreases) as level increases.
     * 
     * @param level The current RPG level
     * @return The drop interval in milliseconds for this level
     */
    public long calculateSpeedForLevel(int level) {
        return speedScaler.calculateSpeedForLevel(level);
    }
    
    /**
     * Spawns garbage brick shapes based on current RPG level to increase difficulty.
     * The number of garbage blocks spawned scales with level progression.
     * Difficulty increases every 5 levels: Levels 1-5: 3 bricks, 6-10: 5 bricks, etc.
     * 
     * @param board The game board to spawn garbage blocks on
     * @param level The current RPG level that determines spawn difficulty
     * @return The number of blocks actually spawned
     */
    public int spawnGarbageBlocksForLevel(Board board, int level) {
        // Calculate number of garbage brick shapes to spawn based on level range
        // Every 5 levels, increase by 2 bricks
        // Levels 1-5: 3 bricks
        // Levels 6-10: 5 bricks
        // Levels 11-15: 7 bricks
        // Levels 16-20: 9 bricks
        // And so on...
        int levelRange = (level - 1) / 5; // 0 for levels 1-5, 1 for 6-10, 2 for 11-15, etc.
        int numBricks = 3 + (levelRange * 2); // Start at 3, add 2 per range
        numBricks = Math.min(numBricks, 15); // Cap at 15 bricks per level (reasonable max)
        
        return board.spawnGarbageBlocks(numBricks, level);
    }
    
    /**
     * Finds the first occupied color value in the brick data matrix.
     * Used to determine the color of the current piece for ability execution.
     * 
     * @param brickData The brick shape matrix to search
     * @return The first non-zero color value found, or 0 if none found
     */
    public int findFirstOccupiedColor(int[][] brickData) {
        if (brickData == null) {
            return 0;
        }
        for (int[] column : brickData) {
            if (column == null) continue;
            for (int cell : column) {
                if (cell != 0) {
                    return cell;
                }
            }
        }
        return 0;
    }
    
    /**
     * Executes the Clear Bottom 3 Rows ability.
     * Removes the bottom 3 rows from the board and drops all blocks above down.
     * 
     * @param board The game board to execute the ability on
     */
    public void executeClearBottom3Rows(Board board) {
        if (board instanceof TetrisBoard) {
            ((TetrisBoard) board).clearBottomRows(3);
        }
    }
    
    /**
     * Executes the Color Bomb ability.
     * Removes all blocks matching the color of the current piece from the board.
     * 
     * @param board The game board to execute the ability on
     * @return The number of blocks removed by the ability
     */
    public int executeColorBomb(Board board) {
        ViewData currentView = board.getViewData();
        if (currentView == null) {
            return 0;
        }
        int[][] brickData = currentView.getBrickData();
        int targetColor = findFirstOccupiedColor(brickData);
        if (targetColor <= 0) {
            return 0;
        }
        return board.clearColorBlocks(targetColor);
    }
    
    /**
     * Executes the Color Sync ability.
     * Converts all blocks on the board to match the color of the current piece.
     * 
     * @param board The game board to execute the ability on
     * @return The number of blocks that were changed
     */
    public int executeColorSync(Board board) {
        ViewData currentView = board.getViewData();
        if (currentView == null) {
            return 0;
        }
        int[][] brickData = currentView.getBrickData();
        int targetColor = findFirstOccupiedColor(brickData);
        if (targetColor <= 0) {
            return 0;
        }
        return board.convertAllBlocksToColor(targetColor);
    }
    
    /**
     * Gets the duration of the Slow Time ability in seconds.
     * 
     * @return The slow time duration in seconds
     */
    public static int getSlowTimeDurationSeconds() {
        return RPGConfig.SLOW_TIME_DURATION_SECONDS;
    }
}

