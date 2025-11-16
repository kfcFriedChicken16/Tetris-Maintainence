package com.comp2042.menu;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * MainMenu class implementing OOP principles for software maintenance coursework.
 * 
 * OOP Concepts Applied:
 * 1. Encapsulation: Private fields and methods, controlled access via public methods
 * 2. Inheritance: Extends MenuBase abstract class
 * 3. Polymorphism: Implements MenuInterface, overrides abstract methods
 * 4. Abstraction: Uses abstract MenuBase class and MenuInterface
 * 
 * @author Student - Software Maintenance Course
 */
public class MainMenu extends MenuBase implements MenuInterface {
    
    // Encapsulation: Private fields
    private Stage primaryStage;
    private MenuController menuController;
    private Scene menuScene;
    
    // Constants for configuration (Encapsulation)
    private static final String MENU_FXML = "mainMenu.fxml";
    private static final String WINDOW_TITLE = "Tetris - Enhanced Edition";
    private static final int WINDOW_WIDTH = 1200;  // Larger default size
    private static final int WINDOW_HEIGHT = 800;  // Larger default size
    // Fullscreen is always enabled - no longer configurable
    
    /**
     * Constructor - Encapsulation principle
     * @param stage The primary stage for the application
     */
    public MainMenu(Stage stage) {
        this.primaryStage = stage;
        initializeMenu();
    }
    
    /**
     * Polymorphism: Implementation of abstract method from MenuBase
     */
    @Override
    protected void initializeMenu() {
        try {
            loadMenuLayout();
            configureStage();
            setupEventHandlers();
        } catch (Exception e) {
            handleMenuError("Failed to initialize menu", e);
        }
    }
    
    /**
     * Polymorphism: Implementation of MenuInterface method
     */
    @Override
    public void showMenu() {
        if (primaryStage != null && menuScene != null) {
            primaryStage.setScene(menuScene);
            primaryStage.show();
            
            // Force fullscreen after showing - this ensures it works properly
            enforceFullscreenMode();
        }
    }
    
    /**
     * Polymorphism: Implementation of MenuInterface method
     */
    @Override
    public void hideMenu() {
        if (primaryStage != null) {
            primaryStage.hide();
        }
    }
    
    /**
     * Polymorphism: Implementation of MenuInterface method
     */
    @Override
    public void cleanup() {
        if (menuController != null) {
            menuController.cleanup();
        }
    }
    
    /**
     * Encapsulation: Private method for loading FXML layout
     */
    private void loadMenuLayout() throws Exception {
        URL location = getClass().getClassLoader().getResource(MENU_FXML);
        if (location == null) {
            throw new RuntimeException("Cannot find " + MENU_FXML + " file");
        }
        
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();
        
        // Get controller and configure
        menuController = fxmlLoader.getController();
        menuController.setPrimaryStage(primaryStage);
        
        // Create scene
        menuScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
    }
    
    /**
     * Encapsulation: Private method for stage configuration
     */
    private void configureStage() {
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setResizable(false); // Prevent window manipulation
        
        // Enable full screen mode - always enabled, cannot be disabled
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(null);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setMaximized(true);
    }
    
    /**
     * Encapsulation: Private method for setting up event handlers
     */
    private void setupEventHandlers() {
        primaryStage.setOnCloseRequest(e -> cleanup());
    }
    
    /**
     * Enforce fullscreen mode - called after stage is shown to ensure it works
     */
    private void enforceFullscreenMode() {
        if (primaryStage != null) {
            // Force fullscreen mode with all protections
            primaryStage.setResizable(false);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitKeyCombination(null);
            primaryStage.setFullScreenExitHint("");
            primaryStage.setMaximized(true);
            
            // Add a listener to prevent any attempts to exit fullscreen
            primaryStage.fullScreenProperty().addListener((obs, wasFullScreen, isNowFullScreen) -> {
                if (!isNowFullScreen) {
                    // If someone tries to exit fullscreen, immediately re-enable it
                    primaryStage.setFullScreen(true);
                }
            });
        }
    }
    
    /**
     * Polymorphism: Override from MenuBase for error handling
     */
    @Override
    protected void handleMenuError(String message, Exception e) {
        System.err.println("MainMenu Error: " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }
    
    /**
     * Encapsulation: Getter method for controlled access
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Encapsulation: Getter method for controlled access
     */
    public MenuController getMenuController() {
        return menuController;
    }
    
    /**
     * Static factory method - demonstrates good OOP design
     * @param stage The primary stage
     * @return Configured MainMenu instance
     */
    public static MainMenu createMenu(Stage stage) {
        return new MainMenu(stage);
    }
}
