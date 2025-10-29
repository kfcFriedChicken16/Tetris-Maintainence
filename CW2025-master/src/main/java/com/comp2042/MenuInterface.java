package com.comp2042;

/**
 * Interface defining contract for menu operations.
 * Demonstrates Abstraction and Polymorphism OOP principles.
 * 
 * This interface ensures all menu implementations provide
 * consistent functionality while allowing different implementations.
 * 
 * @author Student - Software Maintenance Course
 */
public interface MenuInterface {
    
    /**
     * Show the menu to the user.
     * Demonstrates Abstraction - defines what should happen
     * without specifying implementation details.
     */
    void showMenu();
    
    /**
     * Hide the menu from view.
     * Each implementing class can hide menus differently.
     */
    void hideMenu();
    
    /**
     * Clean up menu resources.
     * Important for proper resource management.
     */
    void cleanup();
    
    /**
     * Default method (Java 8+ feature) providing common functionality.
     * Demonstrates how interfaces can provide default implementations
     * while still maintaining the contract.
     * 
     * @return true if menu is ready to display
     */
    default boolean isMenuReady() {
        return true; // Default implementation - can be overridden
    }
    
    /**
     * Static method in interface (Java 8+ feature).
     * Utility method available to all implementations.
     * 
     * @param menuType Type of menu being created
     * @return Formatted menu title
     */
    static String formatMenuTitle(String menuType) {
        return "Tetris - " + menuType + " Menu";
    }
}
