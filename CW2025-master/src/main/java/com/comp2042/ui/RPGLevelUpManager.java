package com.comp2042.ui;

import com.comp2042.core.GameController;
import com.comp2042.core.GameStateManager;
import com.comp2042.events.InputEventListener;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Manages RPG level-up popup functionality.
 * Extracted from GameViewController for better maintainability.
 */
public class RPGLevelUpManager {
    
    private StackPane levelUpGroup;
    private Button clearBottomBtn;
    private Button slowTimeBtn;
    private Button colorBombBtn;
    private Button colorSyncBtn;
    private GameStateManager gameStateManager;
    private InputEventListener eventListener;
    
    // Random ability selection for level-up
    private String[] currentLevelUpAbilities = new String[3]; // Stores the 3 random abilities shown
    
    public RPGLevelUpManager(StackPane levelUpGroup, Button clearBottomBtn, Button slowTimeBtn,
                             Button colorBombBtn, Button colorSyncBtn, GameStateManager gameStateManager) {
        this.levelUpGroup = levelUpGroup;
        this.clearBottomBtn = clearBottomBtn;
        this.slowTimeBtn = slowTimeBtn;
        this.colorBombBtn = colorBombBtn;
        this.colorSyncBtn = colorSyncBtn;
        this.gameStateManager = gameStateManager;
    }
    
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }
    
    /**
     * Show the level-up popup for ability selection with 3 random abilities
     */
    public void showLevelUpPopup() {
        System.out.println("showLevelUpPopup() called");
        if (levelUpGroup != null) {
            // Randomly select 3 abilities from the 4 available
            String[] allAbilities = {"CLEAR_BOTTOM_3", "SLOW_TIME", "COLOR_BOMB", "COLOR_SYNC"};
            List<String> abilityList = new ArrayList<>(Arrays.asList(allAbilities));
            Collections.shuffle(abilityList);
            
            // Take first 3 abilities
            currentLevelUpAbilities[0] = abilityList.get(0);
            currentLevelUpAbilities[1] = abilityList.get(1);
            currentLevelUpAbilities[2] = abilityList.get(2);
            
            System.out.println("Random abilities selected: " + 
                currentLevelUpAbilities[0] + ", " + 
                currentLevelUpAbilities[1] + ", " + 
                currentLevelUpAbilities[2]);
            
            // Update button visibility and labels
            updateAbilityButtons();
            
            levelUpGroup.setVisible(true);
            levelUpGroup.setManaged(true);
            // Pause the game when level-up popup appears
            gameStateManager.setPaused(true);
            System.out.println("Level-up popup should now be visible!");
        } else {
            System.out.println("ERROR: levelUpGroup is null!");
        }
    }
    
    /**
     * Update ability buttons to show only the 3 randomly selected abilities
     * Buttons are always labeled [1], [2], [3] in sequential order from top to bottom
     * The abilities themselves are randomly selected, but displayed in sequential order
     */
    private void updateAbilityButtons() {
        // Hide all buttons first
        if (clearBottomBtn != null) clearBottomBtn.setVisible(false);
        if (slowTimeBtn != null) slowTimeBtn.setVisible(false);
        if (colorBombBtn != null) colorBombBtn.setVisible(false);
        if (colorSyncBtn != null) colorSyncBtn.setVisible(false);
        
        // Buttons in display order (top to bottom) - use first 3 buttons from FXML
        Button[] displayButtons = {clearBottomBtn, slowTimeBtn, colorBombBtn};
        
        // Assign the 3 randomly selected abilities to buttons in sequential order [1], [2], [3]
        for (int i = 0; i < 3; i++) {
            String ability = currentLevelUpAbilities[i];
            Button button = displayButtons[i]; // Use buttons in FXML order
            int buttonNumber = i + 1; // Always sequential: 1, 2, 3
            
            // Set the button text based on ability type
            String buttonText = "";
            switch (ability) {
                case "CLEAR_BOTTOM_3":
                    buttonText = "[" + buttonNumber + "] Clear Bottom 3 Rows";
                    break;
                case "SLOW_TIME":
                    buttonText = "[" + buttonNumber + "] Slow Time (10s)";
                    break;
                case "COLOR_BOMB":
                    buttonText = "[" + buttonNumber + "] Color Bomb (clear matching color)";
                    break;
                case "COLOR_SYNC":
                    buttonText = "[" + buttonNumber + "] Color Sync (combo setup)";
                    break;
            }
            
            if (button != null) {
                button.setVisible(true);
                button.setText(buttonText);
                
                // Update the button's onAction to call the correct ability
                // We need to update the action handler dynamically
                button.setOnAction(e -> {
                    if (eventListener instanceof GameController) {
                        ((GameController) eventListener).selectAbility(ability);
                    }
                    hideLevelUpPopup();
                });
            }
        }
    }
    
    /**
     * Get the ability type for a given keyboard shortcut (1, 2, or 3)
     */
    public String getAbilityForShortcut(int shortcut) {
        if (shortcut >= 1 && shortcut <= 3) {
            return currentLevelUpAbilities[shortcut - 1];
        }
        return null;
    }
    
    /**
     * Hide the level-up popup and resume game
     */
    public void hideLevelUpPopup() {
        if (levelUpGroup != null) {
            levelUpGroup.setVisible(false);
            levelUpGroup.setManaged(false);
            // Resume the game
            gameStateManager.setPaused(false);
        }
    }
    
    /**
     * Handle Clear Bottom ability selection
     */
    public void selectClearBottom() {
        // Notify GameController of ability selection
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).selectAbility("CLEAR_BOTTOM_3");
        }
        hideLevelUpPopup();
    }
    
    /**
     * Handle Slow Time ability selection
     */
    public void selectSlowTime() {
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).selectAbility("SLOW_TIME");
        }
        hideLevelUpPopup();
    }
    
    /**
     * Handle Color Bomb ability selection
     */
    public void selectColorBomb() {
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).selectAbility("COLOR_BOMB");
        }
        hideLevelUpPopup();
    }
    
    /**
     * Handle Color Sync ability selection
     */
    public void selectColorSync() {
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).selectAbility("COLOR_SYNC");
        }
        hideLevelUpPopup();
    }
    
    public boolean isLevelUpPopupVisible() {
        return levelUpGroup != null && levelUpGroup.isVisible();
    }
}

