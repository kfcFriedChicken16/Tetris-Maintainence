package com.comp2042.events;

/**
 * Enum representing the source of a move event.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public enum EventSource {
    /** Event originated from user input */
    USER, 
    /** Event originated from automatic game thread (timed drops) */
    THREAD
}
