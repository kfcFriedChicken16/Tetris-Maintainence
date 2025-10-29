package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    // Configuration flag - easy to switch between menu and direct game
    private static final boolean USE_MAIN_MENU = true;

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
     * Original game launch method - preserved for maintenance purposes
     * Demonstrates encapsulation by extracting original functionality
     */
    private void launchDirectGame(Stage primaryStage) throws Exception {
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 300, 510);
        primaryStage.setScene(scene);
        primaryStage.show();
        new GameController(c);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
