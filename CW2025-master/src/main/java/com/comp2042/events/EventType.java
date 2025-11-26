package com.comp2042.events;

/**
 * Enum representing the types of move events in the Tetris game.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public enum EventType {
    /** Move piece down one row */
    DOWN, 
    /** Move piece one column to the left */
    LEFT, 
    /** Move piece one column to the right */
    RIGHT, 
    /** Rotate piece counter-clockwise */
    ROTATE, 
    /** Instantly drop piece to bottom */
    HARD_DROP
}
