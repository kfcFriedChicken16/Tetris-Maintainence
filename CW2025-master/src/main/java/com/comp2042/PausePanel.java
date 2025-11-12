package com.comp2042;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PausePanel extends BorderPane {

    private Label pauseLabel;
    private Button continueButton;
    private Button restartButton;
    private Button quitButton;
    private VBox container;

    public PausePanel() {
        container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        
        pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("gameOverStyle");
        
        continueButton = new Button("Continue");
        continueButton.getStyleClass().add("pause-menu-button");
        continueButton.setPrefWidth(200);
        continueButton.setPrefHeight(50);
        
        restartButton = new Button("Restart");
        restartButton.getStyleClass().add("pause-menu-button");
        restartButton.setPrefWidth(200);
        restartButton.setPrefHeight(50);
        
        quitButton = new Button("Quit to Main Menu");
        quitButton.getStyleClass().add("pause-menu-button");
        quitButton.setPrefWidth(200);
        quitButton.setPrefHeight(50);
        
        container.getChildren().addAll(pauseLabel, continueButton, restartButton, quitButton);
        setCenter(container);
    }
    
    public Button getContinueButton() {
        return continueButton;
    }
    
    public Button getRestartButton() {
        return restartButton;
    }
    
    public Button getQuitButton() {
        return quitButton;
    }
}

