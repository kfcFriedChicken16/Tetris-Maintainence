package com.comp2042.rpg;

import com.comp2042.core.Board;
import com.comp2042.core.TetrisBoard;
import com.comp2042.models.ViewData;
import com.comp2042.rpg.ability.AbilityManager;
import com.comp2042.rpg.config.RPGConfig;
import com.comp2042.rpg.progression.LevelProgressionCalculator;
import com.comp2042.rpg.progression.RPGSpeedScaler;

/**
 * Manages all RPG mode functionality.
 * Extracted from GameController for better organization.
 */
public class RPGModeManager {
    
    private int rpgLevel = 1;
    private final AbilityManager abilityManager;
    private final LevelProgressionCalculator levelCalculator;
    private final RPGSpeedScaler speedScaler;
    
    public RPGModeManager() {
        this.abilityManager = new AbilityManager();
        this.levelCalculator = new LevelProgressionCalculator();
        this.speedScaler = new RPGSpeedScaler();
    }
    
    public int getRpgLevel() { return rpgLevel; }
    public void setRpgLevel(int level) { this.rpgLevel = level; }
    
    public AbilityManager getAbilityManager() { return abilityManager; }
    public LevelProgressionCalculator getLevelCalculator() { return levelCalculator; }
    public RPGSpeedScaler getSpeedScaler() { return speedScaler; }
    
    /**
     * Calculate RPG level from total lines cleared.
     */
    public int calculateLevelFromLines(int totalLinesCleared) {
        return levelCalculator.calculateLevelFromLines(totalLinesCleared);
    }
    
    /**
     * Calculate total lines required to reach a specific level.
     */
    public int calculateLinesRequiredForLevel(int targetLevel) {
        return levelCalculator.calculateLinesRequiredForLevel(targetLevel);
    }
    
    /**
     * Update game speed based on RPG level.
     */
    public long calculateSpeedForLevel(int level) {
        return speedScaler.calculateSpeedForLevel(level);
    }
    
    /**
     * Spawn garbage brick shapes based on current RPG level.
     * Difficulty scales every 5 levels: more bricks at higher level ranges.
     * Levels 1-5: 3 bricks, Levels 6-10: 5 bricks, Levels 11-15: 7 bricks, Levels 16-20: 9 bricks, etc.
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
     * Find first occupied color in brick data.
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
     * Execute Clear Bottom 3 Rows ability.
     */
    public void executeClearBottom3Rows(Board board) {
        if (board instanceof TetrisBoard) {
            ((TetrisBoard) board).clearBottomRows(3);
        }
    }
    
    /**
     * Execute Color Bomb ability.
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
     * Execute Color Sync ability.
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
    
    public static int getSlowTimeDurationSeconds() {
        return RPGConfig.SLOW_TIME_DURATION_SECONDS;
    }
}

