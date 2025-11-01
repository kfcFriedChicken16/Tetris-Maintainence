package com.comp2042;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class GameOverPanel extends BorderPane {

    public GameOverPanel() {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        
        final Label instructionLabel = new Label("Press SPACE to Continue\nPress ESC to Quit");
        instructionLabel.getStyleClass().add("gameOverInstruction");
        instructionLabel.setWrapText(true);
        
        container.getChildren().addAll(gameOverLabel, instructionLabel);
        setCenter(container);
    }

}
