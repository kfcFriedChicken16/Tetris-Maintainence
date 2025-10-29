package com.comp2042;

/**
 * Abstract base class for menu systems.
 * Demonstrates Abstraction and Inheritance OOP principles.
 * 
 * This abstract class provides common functionality for all menu types
 * while forcing subclasses to implement specific menu initialization.
 * 
 * @author Student - Software Maintenance Course
 */
public abstract class MenuBase {
    
    /**
     * Abstract method that must be implemented by subclasses.
     * Demonstrates Abstraction principle - defines what must be done
     * without specifying how it should be done.
     */
    protected abstract void initializeMenu();
    
    /**
     * Abstract method for error handling.
     * Allows each menu type to handle errors differently.
     * 
     * @param message Error message
     * @param e Exception that occurred
     */
    protected abstract void handleMenuError(String message, Exception e);
    
    /**
     * Common method available to all menu subclasses.
     * Demonstrates Inheritance - subclasses inherit this functionality.
     * 
     * @param message Message to log
     */
    protected void logMenuAction(String message) {
        System.out.println("[Menu] " + message);
    }
    
    /**
     * Template method pattern - defines the skeleton of menu creation.
     * Demonstrates how abstract classes can provide structure while
     * allowing customization in subclasses.
     */
    protected final void createMenuTemplate() {
        logMenuAction("Starting menu creation...");
        try {
            initializeMenu();
            logMenuAction("Menu created successfully");
        } catch (Exception e) {
            handleMenuError("Menu creation failed", e);
        }
    }
}
