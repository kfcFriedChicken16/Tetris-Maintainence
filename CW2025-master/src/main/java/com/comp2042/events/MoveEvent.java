package com.comp2042.events;

/**
 * Represents a move event in the Tetris game.
 * Contains information about the type of move and its source (user input or automatic).
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Constructs a MoveEvent with the specified type and source.
     * 
     * @param eventType The type of move event (DOWN, LEFT, RIGHT, ROTATE, etc.)
     * @param eventSource The source of the event (USER or USER_OTHER)
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Gets the type of this move event.
     * 
     * @return The EventType of this move
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Gets the source of this move event.
     * 
     * @return The EventSource indicating where this event originated
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}
