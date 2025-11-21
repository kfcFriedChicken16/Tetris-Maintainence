package com.comp2042.core;

import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventSource;
import com.comp2042.models.DownData;
import com.comp2042.models.ViewData;
import com.comp2042.models.ClearRow;
import com.comp2042.ui.GameViewController;
import com.comp2042.modes.GameMode;

public class GameController implements InputEventListener {

    private Board board = new TetrisBoard(25, 10);

    private final GameViewController viewGuiController;
    private GameMode currentMode;
    
    // RPG Mode tracking
    private int rpgLevel = 1;
    private int clearRowsCharges = 0; // charges for the Clear Bottom 3 Rows ability
    private int slowTimeCharges = 0; // charges for slow time ability
    private int colorBombCharges = 0; // charges for color bomb ability
    private int colorSyncCharges = 0; // charges for color sync ability
    private static final int SLOW_TIME_DURATION_SECONDS = 10;
    
    private enum AbilityType {
        NONE,
        CLEAR_ROWS,
        SLOW_TIME,
        COLOR_BOMB,
        COLOR_SYNC
    }
    
    private final AbilityType[] abilitySlots = {
            AbilityType.NONE,
            AbilityType.NONE,
            AbilityType.NONE,
            AbilityType.NONE
    };

    public GameController(GameViewController c) {
        this(c, GameMode.CLASSIC); // Default to Classic mode for backward compatibility
    }
    
    public GameController(GameViewController c, GameMode mode) {
        viewGuiController = c;
        currentMode = mode;
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
        if (currentMode == GameMode.RPG && clearRow != null) {
            int totalLinesCleared = board.getTotalLinesCleared();
            
            // Calculate level based on lines cleared
            // Levels 1-5: 10 lines each = 50 lines total for level 6
            // Levels 6-10: 15 lines each = 50 + 75 = 125 lines total for level 11
            // Levels 11-20: 20 lines each = 125 + 200 = 325 lines total for level 21
            // Levels 21-30: 25 lines each = 325 + 250 = 575 lines total for level 31
            // Levels 31-40: 30 lines each = 575 + 300 = 875 lines total for level 41
            int newLevel = calculateLevelFromLines(totalLinesCleared);
            
            // Check if player leveled up
            if (newLevel > rpgLevel) {
                int oldLevel = rpgLevel;
                rpgLevel = newLevel;
                System.out.println("=== RPG LEVEL UP! ===");
                System.out.println("Lines cleared: " + totalLinesCleared);
                System.out.println("Old level: " + oldLevel + " -> New level: " + rpgLevel);
                
                // Check if player reached level 40 (RPG completion!)
                if (rpgLevel == 40) {
                    System.out.println("🎉 CONGRATULATIONS! Player reached level 40!");
                    // Show congratulations screen with final stats
                    viewGuiController.rpgComplete(totalLinesCleared, rpgLevel);
                    return; // Don't show level-up popup, show completion instead
                }
                
                // Update game speed based on new level
                updateRPGSpeed(rpgLevel);
                
                // Spawn garbage blocks as difficulty increases (skip level 1)
                if (rpgLevel > 1) {
                    spawnGarbageBlocksForLevel(rpgLevel);
                }
                
                System.out.println("Showing level-up popup...");
                // Show level up popup for ability selection
                viewGuiController.showLevelUpPopup();
            } else {
                // Also update speed when level doesn't change (in case speed tier changed)
                updateRPGSpeed(rpgLevel);
            }
            
            refreshRPGHud();
        }
    }
    
    /**
     * Calculate RPG level from total lines cleared.
     * Level 1-5: 10 lines per level (Level 6 needs 50 lines total)
     * Level 6-10: 15 lines per level (Level 11 needs 125 lines total)
     * Level 11-20: 20 lines per level (Level 21 needs 325 lines total)
     * Level 21-30: 25 lines per level (Level 31 needs 575 lines total)
     * Level 31-40: 30 lines per level (Level 41 needs 875 lines total)
     */
    private int calculateLevelFromLines(int totalLinesCleared) {
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
        
        int spawned = board.spawnGarbageBlocks(numBricks, level);
        if (spawned > 0) {
            // Refresh the display to show new garbage blocks
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            System.out.println("⚠️ Difficulty increased! " + numBricks + " garbage brick shapes (" + spawned + " total blocks) spawned.");
        }
    }
    
    private void refreshRPGHud() {
        if (currentMode == GameMode.RPG && viewGuiController != null) {
            int totalLinesCleared = board.getTotalLinesCleared();
            // Calculate lines needed for next level
            int linesRequiredForNextLevel = calculateLinesRequiredForLevel(rpgLevel + 1);
            int linesToNextLevel = Math.max(0, linesRequiredForNextLevel - totalLinesCleared);
            viewGuiController.updateRPGDisplay(
                    totalLinesCleared,
                    rpgLevel,
                    linesToNextLevel,
                    getAbilitySlotText(0),
                    getAbilitySlotText(1),
                    getAbilitySlotText(2),
                    getAbilitySlotText(3),
                    getSlowAbilitySlotIndex()
            );
        }
    }
    
    /**
     * Calculate total lines required to reach a specific level.
     * Level 1-5: 10 lines each (Level 6 needs 50 lines total)
     * Level 6-10: 15 lines each (Level 11 needs 125 lines total)
     * Level 11-20: 20 lines each (Level 21 needs 325 lines total)
     * Level 21-30: 25 lines each (Level 31 needs 575 lines total)
     * Level 31-40: 30 lines each (Level 41 needs 875 lines total)
     */
    private int calculateLinesRequiredForLevel(int targetLevel) {
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
    
    private AbilityType mapAbilityType(String abilityName) {
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
    
    private void assignAbilityToSlot(AbilityType type) {
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
    
    private void removeAbilityFromSlots(AbilityType type) {
        if (type == AbilityType.NONE) {
            return;
        }
        for (int i = 0; i < abilitySlots.length; i++) {
            if (abilitySlots[i] == type) {
                abilitySlots[i] = AbilityType.NONE;
            }
        }
    }
    
    private String getAbilitySlotText(int slotIndex) {
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
    
    private int getSlowAbilitySlotIndex() {
        return findAbilitySlotIndex(AbilityType.SLOW_TIME);
    }
    
    private int findAbilitySlotIndex(AbilityType target) {
        for (int i = 0; i < abilitySlots.length; i++) {
            if (abilitySlots[i] == target) {
                return i;
            }
        }
        return -1;
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
        String abilityName = getAbilityDisplayName(abilityType);
        AbilityType mappedType = mapAbilityType(abilityName);
        
        if (mappedType == AbilityType.CLEAR_ROWS) {
            clearRowsCharges++;
        } else if (mappedType == AbilityType.SLOW_TIME) {
            slowTimeCharges++;
        } else if (mappedType == AbilityType.COLOR_BOMB) {
            colorBombCharges++;
        } else if (mappedType == AbilityType.COLOR_SYNC) {
            colorSyncCharges++;
        }
        
        assignAbilityToSlot(mappedType);
        
        System.out.println("Ability selected: " + abilityName + " (Charges remaining - Clear: " 
                + clearRowsCharges + ", Slow: " + slowTimeCharges + ", Bomb: " + colorBombCharges 
                + ", Sync: " + colorSyncCharges + ")");
        
        // Update RPG display with new abilities
        refreshRPGHud();
    }
    
    /**
     * Get display name for ability type
     */
    private String getAbilityDisplayName(String abilityType) {
        switch (abilityType) {
            case "CLEAR_BOTTOM_3": return "Clear 3 Rows";
            case "SLOW_TIME": return "Slow Time";
            case "COLOR_BOMB": return "Color Bomb";
            case "COLOR_SYNC": return "Color Sync";
            default: return "Unknown";
        }
    }
    
    /**
     * Use ability from specific slot (0-4 for slots 1-5)
     */
    public void useAbility(int slotIndex) {
        if (currentMode != GameMode.RPG) return;
        if (slotIndex < 0 || slotIndex >= abilitySlots.length) {
            System.out.println("Invalid ability slot: " + (slotIndex + 1));
            return;
        }
        
        AbilityType slotType = abilitySlots[slotIndex];
        switch (slotType) {
            case CLEAR_ROWS:
                if (clearRowsCharges <= 0) {
                    System.out.println("No Clear Rows charges available.");
                    return;
                }
                System.out.println("Using ability: Clear 3 Rows (charges left before use: " + clearRowsCharges + ")");
                executeClearBottom3Rows();
                clearRowsCharges = Math.max(0, clearRowsCharges - 1);
                if (clearRowsCharges == 0) {
                    removeAbilityFromSlots(AbilityType.CLEAR_ROWS);
                }
                break;
            case SLOW_TIME:
                if (slowTimeCharges <= 0) {
                    System.out.println("No Slow Time charges available.");
                    return;
                }
                System.out.println("Using ability: Slow Time (charges left before use: " + slowTimeCharges + ")");
                if (viewGuiController != null) {
                    viewGuiController.activateSlowTime(SLOW_TIME_DURATION_SECONDS);
                }
                slowTimeCharges = Math.max(0, slowTimeCharges - 1);
                if (slowTimeCharges == 0) {
                    removeAbilityFromSlots(AbilityType.SLOW_TIME);
                }
                break;
            case COLOR_BOMB:
                if (colorBombCharges <= 0) {
                    System.out.println("No Color Bomb charges available.");
                    return;
                }
                System.out.println("Using ability: Color Bomb (charges left before use: " + colorBombCharges + ")");
                executeColorBomb();
                colorBombCharges = Math.max(0, colorBombCharges - 1);
                if (colorBombCharges == 0) {
                    removeAbilityFromSlots(AbilityType.COLOR_BOMB);
                }
                break;
            case COLOR_SYNC:
                if (colorSyncCharges <= 0) {
                    System.out.println("No Color Sync charges available.");
                    return;
                }
                System.out.println("Using ability: Color Sync (charges left before use: " + colorSyncCharges + ")");
                executeColorSync();
                colorSyncCharges = Math.max(0, colorSyncCharges - 1);
                if (colorSyncCharges == 0) {
                    removeAbilityFromSlots(AbilityType.COLOR_SYNC);
                }
                break;
            default:
                System.out.println("No ability assigned to slot [" + (slotIndex + 1) + "]");
                return;
        }
        
        // Update display after using ability
        refreshRPGHud();
    }
    
    /**
     * Execute Clear Bottom 3 Rows ability
     */
    private void executeClearBottom3Rows() {
        System.out.println("🧹 EXECUTING CLEAR BOTTOM 3 ROWS ABILITY!");
        
        if (board instanceof TetrisBoard) {
            ((TetrisBoard) board).clearBottomRows(3);
            // Refresh the game display
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            System.out.println("✅ Bottom 3 rows cleared and blocks dropped down!");
        }
    }
    
    /**
     * Execute Color Bomb ability
     */
    private void executeColorBomb() {
        ViewData currentView = board.getViewData();
        if (currentView == null) {
            System.out.println("Color Bomb failed: no active piece data.");
            return;
        }
        int[][] brickData = currentView.getBrickData();
        int targetColor = findFirstOccupiedColor(brickData);
        if (targetColor <= 0) {
            System.out.println("Color Bomb found no colored cells in current piece.");
            return;
        }
        int removed = board.clearColorBlocks(targetColor);
        if (removed > 0) {
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            System.out.println("💥 Color Bomb removed " + removed + " blocks of color " + targetColor);
        } else {
            System.out.println("Color Bomb found no matching blocks on the board.");
        }
    }
    
    /**
     * Execute Color Sync ability
     */
    private void executeColorSync() {
        ViewData currentView = board.getViewData();
        if (currentView == null) {
            System.out.println("Color Sync failed: no active piece data.");
            return;
        }
        int[][] brickData = currentView.getBrickData();
        int targetColor = findFirstOccupiedColor(brickData);
        if (targetColor <= 0) {
            System.out.println("Color Sync found no colored cells in current piece.");
            return;
        }
        int changed = board.convertAllBlocksToColor(targetColor);
        if (changed > 0) {
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            System.out.println("🎨 Color Sync aligned " + changed + " blocks to color " + targetColor);
        } else {
            System.out.println("Color Sync found no blocks to update.");
        }
    }
    
    private int findFirstOccupiedColor(int[][] brickData) {
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
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}
