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
     * Level up every 5 lines cleared.
     */
    private void checkRPGProgression(ClearRow clearRow) {
        if (currentMode == GameMode.RPG && clearRow != null) {
            int totalLinesCleared = board.getTotalLinesCleared();
            int newLevel = (totalLinesCleared / 5) + 1;
            
            // Check if player leveled up
            if (newLevel > rpgLevel) {
                rpgLevel = newLevel;
                System.out.println("=== RPG LEVEL UP! ===");
                System.out.println("Lines cleared: " + totalLinesCleared);
                System.out.println("Old level: " + (rpgLevel - 1) + " -> New level: " + rpgLevel);
                System.out.println("Showing level-up popup...");
                // Show level up popup for ability selection
                viewGuiController.showLevelUpPopup();
            }
            
            refreshRPGHud();
        }
    }
    
    private void refreshRPGHud() {
        if (currentMode == GameMode.RPG && viewGuiController != null) {
            int totalLinesCleared = board.getTotalLinesCleared();
            int remainder = totalLinesCleared % 5;
            int linesToNextLevel = remainder == 0 ? 5 : 5 - remainder;
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
        for (int i = 0; i < abilitySlots.length; i++) {
            AbilityType existing = abilitySlots[i];
            if (existing == type) {
                return;
            }
            if (existing == AbilityType.NONE) {
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
