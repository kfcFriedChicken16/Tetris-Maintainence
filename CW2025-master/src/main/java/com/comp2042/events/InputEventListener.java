package com.comp2042.events;

import com.comp2042.models.DownData;
import com.comp2042.models.ViewData;

/**
 * Interface for handling user input events in the Tetris game.
 * Defines methods for all piece movement and game control operations.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public interface InputEventListener {

    /**
     * Handles the down movement event for the current piece.
     * 
     * @param event The move event containing information about the movement source
     * @return DownData containing information about cleared rows and updated view data
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles the left movement event for the current piece.
     * 
     * @param event The move event containing information about the movement source
     * @return ViewData containing the updated game view after the move
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles the right movement event for the current piece.
     * 
     * @param event The move event containing information about the movement source
     * @return ViewData containing the updated game view after the move
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles the rotation event for the current piece.
     * 
     * @param event The move event containing information about the movement source
     * @return ViewData containing the updated game view after the rotation
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles the hard drop event, instantly dropping the piece to the bottom.
     * 
     * @param event The move event containing information about the movement source
     * @return DownData containing information about cleared rows and updated view data
     */
    DownData onHardDropEvent(MoveEvent event);
    
    /**
     * Handles the hold event, allowing the player to swap the current piece with the held piece.
     * 
     * @return ViewData with the new current piece after holding
     */
    ViewData onHoldEvent();

    /**
     * Resets the game to start a new game.
     * Clears the board and resets all game state.
     */
    void createNewGame();
}
