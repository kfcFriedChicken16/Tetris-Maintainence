package com.comp2042.core;

import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventSource;
import com.comp2042.models.DownData;
import com.comp2042.models.ViewData;
import com.comp2042.models.ClearRow;
import com.comp2042.ui.GameViewController;
import com.comp2042.modes.GameMode;
import com.comp2042.rpg.RPGModeManager;
import com.comp2042.rpg.AbilityType;

public class GameController implements InputEventListener {

    private Board board = new TetrisBoard(25, 10);

    private final GameViewController viewGuiController;
    private GameMode currentMode;
    
    // RPG Mode manager (only initialized in RPG mode)
    private RPGModeManager rpgModeManager;

    public GameController(GameViewController c) {
        this(c, GameMode.CLASSIC); // Default to Classic mode for backward compatibility
    }
    
    public GameController(GameViewController c, GameMode mode) {
        viewGuiController = c;
        currentMode = mode;
        
        // Initialize RPG mode manager if in RPG mode
        if (mode == GameMode.RPG) {
            rpgModeManager = new RPGModeManager();
        }
        
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.setGameMode(mode); // Pass mode to GuiController FIRST (before initGameView)
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        
        // Initialize RPG display if in RPG mode
        if (mode == GameMode.RPG) {
            refreshRPGHud();
        }
    }
    
    public GameMode getCurrentMode() {
        return currentMode;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            
            // Check Sprint mode completion (40 lines)
            if (checkSprintCompletion(clearRow)) {
                return new DownData(clearRow, board.getViewData());
            }
            
            // Check RPG mode progression (every 5 lines)
            checkRPGProgression(clearRow);
            
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        // Hard drop the piece and get bonus points
        int dropDistance = board.hardDropBrick();
        
        // Add bonus points for hard drop (2 points per row dropped)
        if (dropDistance > 0) {
            board.getScore().add(dropDistance * 2);
        }
        
        // Now handle the landing (same as normal drop)
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }
        
        // Check Sprint mode completion (40 lines)
        if (checkSprintCompletion(clearRow)) {
            return new DownData(clearRow, board.getViewData());
        }
        
        // Check RPG mode progression (every 5 lines)
        checkRPGProgression(clearRow);
        
        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }
        
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return new DownData(clearRow, board.getViewData());
    }
    
    /**
     * Check if Sprint mode completion condition is met (40 lines cleared).
     * Returns true if game should end, false otherwise.
     */
    private boolean checkSprintCompletion(ClearRow clearRow) {
        if (currentMode == GameMode.SPRINT) {
            int linesCleared = board.getTotalLinesCleared();
            viewGuiController.updateSprintLines(linesCleared);
            if (linesCleared >= 40) {
                viewGuiController.sprintComplete();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check RPG mode progression and update display.
     * Level progression: 
     * - Levels 1-5: 10 lines per level
     * - Levels 6-10: 15 lines per level
     * - Levels 11-20: 20 lines per level
     * - Levels 21-30: 25 lines per level
     * - Levels 31-40: 30 lines per level
     */
    private void checkRPGProgression(ClearRow clearRow) {
        if (currentMode == GameMode.RPG && clearRow != null && rpgModeManager != null) {
            int totalLinesCleared = board.getTotalLinesCleared();
            
            // Calculate level based on lines cleared
            // Levels 1-5: 10 lines each = 50 lines total for level 6
            // Levels 6-10: 15 lines each = 50 + 75 = 125 lines total for level 11
            // Levels 11-20: 20 lines each = 125 + 200 = 325 lines total for level 21
            // Levels 21-30: 25 lines each = 325 + 250 = 575 lines total for level 31
            // Levels 31-40: 30 lines each = 575 + 300 = 875 lines total for level 41
            int newLevel = rpgModeManager.calculateLevelFromLines(totalLinesCleared);
            
            // Check if player leveled up
            if (newLevel > rpgModeManager.getRpgLevel()) {
                int oldLevel = rpgModeManager.getRpgLevel();
                rpgModeManager.setRpgLevel(newLevel);
                System.out.println("=== RPG LEVEL UP! ===");
                System.out.println("Lines cleared: " + totalLinesCleared);
                System.out.println("Old level: " + oldLevel + " -> New level: " + rpgModeManager.getRpgLevel());
                
                // Check if player reached level 40 (RPG completion!)
                if (rpgModeManager.getRpgLevel() == 40) {
                    System.out.println("🎉 CONGRATULATIONS! Player reached level 40!");
                    // Show congratulations screen with final stats
                    viewGuiController.rpgComplete(totalLinesCleared, rpgModeManager.getRpgLevel());
                    return; // Don't show level-up popup, show completion instead
                }
                
                // Update game speed based on new level
                updateRPGSpeed(rpgModeManager.getRpgLevel());
                
                // Spawn garbage blocks as difficulty increases (skip level 1)
                if (rpgModeManager.getRpgLevel() > 1) {
                    spawnGarbageBlocksForLevel(rpgModeManager.getRpgLevel());
                }
                
                System.out.println("Showing level-up popup...");
                // Show level up popup for ability selection
                viewGuiController.showLevelUpPopup();
            } else {
                // Also update speed when level doesn't change (in case speed tier changed)
                updateRPGSpeed(rpgModeManager.getRpgLevel());
            }
            
            refreshRPGHud();
        }
    }
    
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
    private void updateRPGSpeed(int level) {
        if (rpgModeManager == null) return;
        
        long newSpeed = rpgModeManager.calculateSpeedForLevel(level);
        
        System.out.println("⚡ Speed updated! Drop interval: " + newSpeed + "ms (Level " + level + ")");
        
        // Update the game speed through the view controller
        if (viewGuiController != null) {
            viewGuiController.updateRPGSpeed(newSpeed);
        }
    }
    
    /**
     * Spawn garbage brick shapes based on current RPG level.
     * Difficulty scales every 5 levels: more bricks at higher level ranges.
     * Levels 1-5: 3 bricks, Levels 6-10: 5 bricks, Levels 11-15: 7 bricks, Levels 16-20: 9 bricks, etc.
     */
    private void spawnGarbageBlocksForLevel(int level) {
        if (rpgModeManager == null) return;
        
        int spawned = rpgModeManager.spawnGarbageBlocksForLevel(board, level);
        if (spawned > 0) {
            // Refresh the display to show new garbage blocks
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            System.out.println("⚠️ Difficulty increased! Garbage brick shapes (" + spawned + " total blocks) spawned.");
        }
    }
    
    private void refreshRPGHud() {
        if (currentMode == GameMode.RPG && viewGuiController != null && rpgModeManager != null) {
            int totalLinesCleared = board.getTotalLinesCleared();
            // Calculate lines needed for next level
            int linesRequiredForNextLevel = rpgModeManager.calculateLinesRequiredForLevel(rpgModeManager.getRpgLevel() + 1);
            int linesToNextLevel = Math.max(0, linesRequiredForNextLevel - totalLinesCleared);
            
            var abilityManager = rpgModeManager.getAbilityManager();
            viewGuiController.updateRPGDisplay(
                    totalLinesCleared,
                    rpgModeManager.getRpgLevel(),
                    linesToNextLevel,
                    abilityManager.getAbilitySlotText(0),
                    abilityManager.getAbilitySlotText(1),
                    abilityManager.getAbilitySlotText(2),
                    abilityManager.getAbilitySlotText(3),
                    abilityManager.findAbilitySlotIndex(AbilityType.SLOW_TIME)
            );
        }
    }
    
    /**
     * TEST METHOD: Force show level-up popup (for debugging)
     */
    public void testLevelUpPopup() {
        System.out.println("=== TESTING LEVEL UP POPUP ===");
        viewGuiController.showLevelUpPopup();
    }
    
    /**
     * Handle ability selection from level-up popup
     */
    public void selectAbility(String abilityType) {
        if (rpgModeManager == null) return;
        
        var abilityManager = rpgModeManager.getAbilityManager();
        String abilityName = abilityManager.getAbilityDisplayName(abilityType);
        AbilityType mappedType = abilityManager.mapAbilityType(abilityName);
        
        if (mappedType == AbilityType.CLEAR_ROWS) {
            abilityManager.incrementClearRowsCharges();
        } else if (mappedType == AbilityType.SLOW_TIME) {
            abilityManager.incrementSlowTimeCharges();
        } else if (mappedType == AbilityType.COLOR_BOMB) {
            abilityManager.incrementColorBombCharges();
        } else if (mappedType == AbilityType.COLOR_SYNC) {
            abilityManager.incrementColorSyncCharges();
        }
        
        abilityManager.assignAbilityToSlot(mappedType);
        
        System.out.println("Ability selected: " + abilityName + " (Charges remaining - Clear: " 
                + abilityManager.getClearRowsCharges() + ", Slow: " + abilityManager.getSlowTimeCharges() 
                + ", Bomb: " + abilityManager.getColorBombCharges() 
                + ", Sync: " + abilityManager.getColorSyncCharges() + ")");
        
        // Update RPG display with new abilities
        refreshRPGHud();
    }
    
    /**
     * Use ability from specific slot (0-4 for slots 1-5)
     */
    public void useAbility(int slotIndex) {
        if (currentMode != GameMode.RPG || rpgModeManager == null) return;
        
        var abilityManager = rpgModeManager.getAbilityManager();
        AbilityType[] abilitySlots = abilityManager.getAbilitySlots();
        
        if (slotIndex < 0 || slotIndex >= abilitySlots.length) {
            System.out.println("Invalid ability slot: " + (slotIndex + 1));
            return;
        }
        
        AbilityType slotType = abilitySlots[slotIndex];
        switch (slotType) {
            case CLEAR_ROWS:
                if (!abilityManager.hasCharges(AbilityType.CLEAR_ROWS)) {
                    System.out.println("No Clear Rows charges available.");
                    return;
                }
                System.out.println("Using ability: Clear 3 Rows (charges left before use: " + abilityManager.getClearRowsCharges() + ")");
                rpgModeManager.executeClearBottom3Rows(board);
                viewGuiController.refreshGameBackground(board.getBoardMatrix());
                abilityManager.decrementClearRowsCharges();
                if (abilityManager.getClearRowsCharges() == 0) {
                    abilityManager.removeAbilityFromSlots(AbilityType.CLEAR_ROWS);
                }
                break;
            case SLOW_TIME:
                if (!abilityManager.hasCharges(AbilityType.SLOW_TIME)) {
                    System.out.println("No Slow Time charges available.");
                    return;
                }
                System.out.println("Using ability: Slow Time (charges left before use: " + abilityManager.getSlowTimeCharges() + ")");
                if (viewGuiController != null) {
                    viewGuiController.activateSlowTime(RPGModeManager.getSlowTimeDurationSeconds());
                }
                abilityManager.decrementSlowTimeCharges();
                if (abilityManager.getSlowTimeCharges() == 0) {
                    abilityManager.removeAbilityFromSlots(AbilityType.SLOW_TIME);
                }
                break;
            case COLOR_BOMB:
                if (!abilityManager.hasCharges(AbilityType.COLOR_BOMB)) {
                    System.out.println("No Color Bomb charges available.");
                    return;
                }
                System.out.println("Using ability: Color Bomb (charges left before use: " + abilityManager.getColorBombCharges() + ")");
                int removed = rpgModeManager.executeColorBomb(board);
                if (removed > 0) {
                    viewGuiController.refreshGameBackground(board.getBoardMatrix());
                    System.out.println("💥 Color Bomb removed " + removed + " blocks");
                } else {
                    System.out.println("Color Bomb found no matching blocks on the board.");
                }
                abilityManager.decrementColorBombCharges();
                if (abilityManager.getColorBombCharges() == 0) {
                    abilityManager.removeAbilityFromSlots(AbilityType.COLOR_BOMB);
                }
                break;
            case COLOR_SYNC:
                if (!abilityManager.hasCharges(AbilityType.COLOR_SYNC)) {
                    System.out.println("No Color Sync charges available.");
                    return;
                }
                System.out.println("Using ability: Color Sync (charges left before use: " + abilityManager.getColorSyncCharges() + ")");
                int changed = rpgModeManager.executeColorSync(board);
                if (changed > 0) {
                    viewGuiController.refreshGameBackground(board.getBoardMatrix());
                    System.out.println("🎨 Color Sync aligned " + changed + " blocks");
                } else {
                    System.out.println("Color Sync found no blocks to update.");
                }
                abilityManager.decrementColorSyncCharges();
                if (abilityManager.getColorSyncCharges() == 0) {
                    abilityManager.removeAbilityFromSlots(AbilityType.COLOR_SYNC);
                }
                break;
            default:
                System.out.println("No ability assigned to slot [" + (slotIndex + 1) + "]");
                return;
        }
        
        // Update display after using ability
        refreshRPGHud();
    }

    @Override
    public ViewData onHoldEvent() {
        boolean success = board.holdBrick();
        if (success) {
            return board.getViewData();
        }
        // If hold failed (already held this turn), return current view data
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        if (rpgModeManager != null) {
            rpgModeManager.setRpgLevel(1);
            rpgModeManager = new RPGModeManager(); // Reset abilities
        }
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        if (currentMode == GameMode.RPG) {
            refreshRPGHud();
        }
    }
}
