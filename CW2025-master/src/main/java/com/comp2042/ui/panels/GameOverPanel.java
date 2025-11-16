package com.comp2042.ui.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class GameOverPanel extends BorderPane {

    private Label gameOverLabel;
    private Label instructionLabel;
    private Label timeInfoLabel; // For showing best time and current time
    private VBox container;

    public GameOverPanel() {
        container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        
        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        
        timeInfoLabel = new Label();
        timeInfoLabel.getStyleClass().add("gameOverTimeInfo");
        timeInfoLabel.setWrapText(true);
        timeInfoLabel.setVisible(false);
        timeInfoLabel.setManaged(false);
        
        instructionLabel = new Label("Press SPACE to Continue\nPress ESC to Quit");
        instructionLabel.getStyleClass().add("gameOverInstruction");
        instructionLabel.setWrapText(true);
        
        container.getChildren().addAll(gameOverLabel, timeInfoLabel, instructionLabel);
        setCenter(container);
    }
    
    /**
     * Set custom game over message (for Sprint mode completion, etc.)
     */
    public void setGameOverMessage(String message) {
        if (gameOverLabel != null) {
            gameOverLabel.setText(message);
        }
    }
    
    /**
     * Set time information (for Sprint mode completion)
     */
    public void setTimeInfo(String bestTime, String currentTime) {
        if (timeInfoLabel != null) {
            timeInfoLabel.setText("BEST TIME: " + bestTime + "\nCURRENT TIME: " + currentTime);
            timeInfoLabel.setVisible(true);
            timeInfoLabel.setManaged(true);
        }
    }
    
    /**
     * Reset to default game over display (hide time info)
     */
    public void resetToDefault() {
        if (timeInfoLabel != null) {
            timeInfoLabel.setVisible(false);
            timeInfoLabel.setManaged(false);
        }
        if (gameOverLabel != null) {
            gameOverLabel.setText("GAME OVER");
        }
    }

}
