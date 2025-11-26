package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.comp2042.menu.MainMenu;
import com.comp2042.ui.GameViewController;
import com.comp2042.core.GameController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main entry point for the Tetris application.
 * Initializes the JavaFX application and launches either the main menu or direct game mode
 * based on configuration settings.
 * 
 * @author Phung Yu Jie
 * @version 1.0
 */
public class Main extends Application {

    // Configuration flag - easy to switch between menu and direct game
    private static final boolean USE_MAIN_MENU = true;

    /**
     * Initializes and starts the JavaFX application.
     * Launches either the main menu interface or directly starts the game
     * based on the USE_MAIN_MENU configuration flag.
     * 
     * @param primaryStage The primary stage for the JavaFX application
     * @throws Exception If there is an error loading the FXML layout or initializing the application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        if (USE_MAIN_MENU) {
            // Use new MainMenu class (OOP approach)
            MainMenu mainMenu = MainMenu.createMenu(primaryStage);
            mainMenu.showMenu();
        } else {
            // Original direct game launch (preserved for comparison)
            launchDirectGame(primaryStage);
        }
    }
    
    /**
     * Launches the game directly without the menu interface.
     * This method is preserved for maintenance purposes and demonstrates
     * encapsulation by extracting original functionality.
     * 
     * @param primaryStage The primary stage for the JavaFX application
     * @throws Exception If there is an error loading the game layout FXML file
     */
    private void launchDirectGame(Stage primaryStage) throws Exception {
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        GameViewController c = fxmlLoader.getController();

        primaryStage.setTitle("TetrisJFX - Full Screen");
        Scene scene = new Scene(root, 1200, 800); // Larger default size
        primaryStage.setScene(scene);
        
        // Enable full screen mode - locked, cannot be exited
        primaryStage.setResizable(false); // Prevent window manipulation
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(null);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setMaximized(true);
        
        primaryStage.show();
        new GameController(c);
    }

    /**
     * Main method that launches the JavaFX application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
