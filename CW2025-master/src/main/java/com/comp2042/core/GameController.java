package com.comp2042.core;

import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventSource;
import com.comp2042.models.DownData;
import com.comp2042.models.ViewData;
import com.comp2042.models.ClearRow;
import com.comp2042.ui.GuiController;
import com.comp2042.modes.GameMode;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;
    private GameMode currentMode;

    public GameController(GuiController c) {
        this(c, GameMode.CLASSIC); // Default to Classic mode for backward compatibility
    }
    
    public GameController(GuiController c, GameMode mode) {
        viewGuiController = c;
        currentMode = mode;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.setGameMode(mode); // Pass mode to GuiController FIRST (before initGameView)
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
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
