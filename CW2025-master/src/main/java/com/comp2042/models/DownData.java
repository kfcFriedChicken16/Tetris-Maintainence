package com.comp2042.models;

/**
 * Data class containing information returned from down movement events.
 * Includes information about cleared rows and updated view data.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    /**
     * Constructs a DownData object with cleared row information and view data.
     * 
     * @param clearRow Information about cleared rows (may be null if no rows were cleared)
     * @param viewData Updated view data after the down movement
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Gets the cleared row information.
     * 
     * @return ClearRow object containing information about cleared rows, or null if none were cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the updated view data after the down movement.
     * 
     * @return ViewData object containing the updated game view
     */
    public ViewData getViewData() {
        return viewData;
    }
}
