# Tetris Maintenance and Extension - Coursework Documentation

## GitHub Repository

**Repository Link:** https://github.com/kfcFriedChicken16/Tetris-Maintainence

---

## Compilation Instructions

### Prerequisites

Before compiling and running the application, ensure you have the following installed:

1. **Java Development Kit (JDK) 23**
   - The project requires JDK 23 or higher
   - Verify installation: `java -version` should show version 23 or higher

2. **JavaFX SDK 21.0.6**
   - Required for UI components, FXML support, and media playback
   - Ensure JavaFX libraries are available in your IDE's classpath

3. **Integrated Development Environment (IDE)**
   - Recommended: IntelliJ IDEA, Eclipse, or NetBeans
   - The project can be opened directly in your IDE

### Step-by-Step Compilation Guide

1. **Open Project in IDE**
   - Open your IDE (IntelliJ IDEA, Eclipse, NetBeans, etc.)
   - Select "Open Project" or "Import Project"
   - Navigate to the `CW2025-master` directory and open it

2. **Configure Project Settings**
   - Ensure the project is set to use JDK 23
   - Verify that JavaFX libraries are included in the project's module path or classpath
   - Check that the source folder `src/main/java` is marked as a source directory
   - Ensure the resources folder `src/main/resources` is marked as a resources directory

3. **Compile the Project**
   - In most IDEs, compilation happens automatically when you save files
   - Alternatively, use the IDE's "Build" or "Compile" command
   - Check for any compilation errors in the IDE's error panel

4. **Run the Application**
   - Navigate to the Maven panel in your IDE
   - Expand: **MAVEN** → **demo3** → **Plugins** → **javafx**
   - Click on **run** (the run goal under the JavaFX plugin)
   - This will compile and execute the application using the Maven JavaFX plugin
   - **Note:** Do not run `Main.java` directly. The application must be run through the Maven JavaFX plugin's run goal.

### Dependencies

The project requires the following libraries:

- **JavaFX Controls** (21.0.6) - UI components
- **JavaFX FXML** (21.0.6) - FXML scene graph support
- **JavaFX Media** (21.0.6) - Audio and video playback
- **JUnit Jupiter** (5.12.1) - Testing framework (for test classes only)

These dependencies should be configured in your IDE's project settings or build configuration.

### Build Configuration

- **Source Encoding:** UTF-8
- **Java Source/Target Version:** 23
- **Main Class:** `com.comp2042.Main`
- **Source Directory:** `src/main/java`
- **Resources Directory:** `src/main/resources`

### Troubleshooting Compilation Issues

1. **"Java version mismatch" error:**
   - Ensure JDK 23 is installed and configured in your IDE
   - Verify the project's SDK settings match JDK 23
   - Check: `java -version` should show version 23 or higher

2. **"JavaFX module not found" error:**
   - Ensure JavaFX libraries are added to your project's module path or classpath
   - Download JavaFX SDK 21.0.6 if not already included
   - Configure JavaFX in your IDE's project settings

3. **Audio/Video files not loading:**
   - Ensure sound files are placed in `src/main/resources/` or `src/main/resources/audio/`
   - Verify that the resources directory is marked as a resources folder in your IDE
   - Check console output for file loading messages when running the application

4. **Class not found errors:**
   - Ensure all source files are compiled
   - Check that the `target/classes` directory (if present) contains compiled `.class` files
   - Verify that your IDE's build path includes all necessary source directories

---

## Implemented and Working Properly

### 1. RPG Ability Sound Effects System

A comprehensive sound effect system has been implemented for RPG mode abilities, enhancing the gameplay experience with audio feedback when abilities are activated.

#### 1.1 Color Bomb Ability Sound Effect
- **Status:** ✅ Fully Implemented and Working
- **Location:** 
  - Sound file: `src/main/resources/combo.mp3`
  - Loading code: `AudioManager.java` lines 82-88
  - Playback method: `AudioManager.playComboSound()` (lines 330-335)
  - Trigger: `GameController.java` line 361
- **Functionality:** When the Color Bomb ability successfully removes blocks from the board, a combo sound effect is played. The sound provides immediate audio feedback to the player, indicating that the ability has been successfully executed and blocks have been cleared.
- **Technical Details:** The sound is loaded during AudioManager initialization, checks for multiple file path variations (`combo.mp3`, `audio/combo.mp3`, `combo.wav`, `audio/combo.wav`), and respects the user's sound effects settings (volume and enable/disable toggle).

#### 1.2 Color Sync Ability Sound Effect
- **Status:** ✅ Fully Implemented and Working
- **Location:**
  - Sound file: `src/main/resources/color_sync.wav`
  - Loading code: `AudioManager.java` lines 90-96
  - Playback method: `AudioManager.playColorSyncSound()` (lines 337-342)
  - Trigger: `GameController.java` line 379
- **Functionality:** When the Color Sync ability successfully converts blocks to a single color, a unique sound effect is played. This audio cue helps players understand that the color synchronization transformation has occurred.
- **Technical Details:** The sound effect is integrated into the ability execution flow, playing only when blocks are successfully changed (when `changed > 0`). The implementation follows the same robust file loading pattern as other sound effects, supporting multiple file formats and locations.

#### 1.3 Clear Bottom 3 Rows Ability Sound Effect
- **Status:** ✅ Fully Implemented and Working
- **Location:**
  - Sound file: `src/main/resources/clear3lines.wav`
  - Loading code: `AudioManager.java` lines 98-106
  - Playback method: `AudioManager.playClearRowsSound()` (lines 344-349)
  - Trigger: `GameController.java` line 332
- **Functionality:** When the Clear Bottom 3 Rows ability is activated, a distinctive sound effect plays to indicate that the bottom three rows have been cleared. This provides immediate feedback for this powerful ability.
- **Technical Details:** The sound plays immediately upon ability activation, before charge decrement and slot management. The implementation supports multiple file naming conventions (`clear3lines.wav`, `audio/clear3lines.wav`, `clear3lines.mp3`, `clear_rows.wav`) for flexibility.

### 2. Audio Manager Enhancements

The `AudioManager` class has been extended to support the new RPG ability sound effects while maintaining backward compatibility with existing sound effects.

- **Status:** ✅ Fully Implemented and Working
- **Location:** `src/main/java/com/comp2042/managers/AudioManager.java`
- **Key Modifications:**
  - Added three new `MediaPlayer` fields: `comboSound`, `colorSyncSound`, `clearRowsSound` (lines 21-23)
  - Extended `initializeSoundEffects()` method to load new sound files (lines 82-106)
  - Added three new public playback methods: `playComboSound()`, `playColorSyncSound()`, `playClearRowsSound()` (lines 330-349)
  - Updated `dispose()` method to properly clean up new MediaPlayer instances (lines 394-405)
  - Enhanced `loadSoundEffect()` switch statement to handle new sound types (lines 172-180)
- **Rationale:** These modifications centralize audio management, ensuring consistent sound effect handling across all abilities. The implementation follows the existing pattern used for other sound effects (block land, hard drop, line clear, game over), maintaining code consistency and making future extensions easier.

### 3. Game View Controller Integration

The `GameViewController` class has been extended with methods to trigger ability sound effects, acting as a bridge between the game controller and audio manager.

- **Status:** ✅ Fully Implemented and Working
- **Location:** `src/main/java/com/comp2042/ui/GameViewController.java`
- **Key Modifications:**
  - Added `playComboSound()` method (lines 1197-1201)
  - Added `playColorSyncSound()` method (lines 1203-1207)
  - Added `playClearRowsSound()` method (lines 1209-1213)
- **Rationale:** These wrapper methods provide a clean interface for the `GameController` to trigger sound effects without directly accessing the `AudioManager`. This maintains separation of concerns and follows the existing architecture pattern where UI-related operations are handled through the view controller.

### 4. Game Controller Ability Sound Integration

The `GameController` class has been updated to trigger appropriate sound effects when RPG abilities are successfully executed.

- **Status:** ✅ Fully Implemented and Working
- **Location:** `src/main/java/com/comp2042/core/GameController.java`
- **Key Modifications:**
  - Color Bomb ability: Added `viewGuiController.playComboSound()` call after successful block removal (line 361)
  - Clear Bottom 3 Rows ability: Added `viewGuiController.playClearRowsSound()` call after ability execution (line 332)
  - Color Sync ability: Added `viewGuiController.playColorSyncSound()` call after successful block conversion (line 379)
- **Rationale:** Integrating sound effects directly into the ability execution flow ensures that audio feedback is synchronized with visual changes. The sound effects only play when abilities are successfully executed (e.g., when blocks are actually removed or changed), providing accurate feedback to the player.

### 5. Existing RPG Mode Features

The RPG mode system, including ability management, level progression, and ability execution, continues to work properly with the new sound effects integrated seamlessly.

- **Status:** ✅ Fully Functional
- **Features:**
  - Four RPG abilities: Clear Bottom 3 Rows, Slow Time, Color Bomb, Color Sync
  - Level progression system (levels 1-40)
  - Ability charge management
  - Ability slot system (4 slots)
  - Level-up popup with ability selection
  - Dynamic difficulty scaling
  - Garbage block spawning

---

## Implemented but Not Working Properly

### None

All implemented features are functioning correctly. The sound effects system has been thoroughly integrated and tested. Sound effects play correctly when abilities are activated, respect user settings (volume and enable/disable), and properly handle edge cases such as missing sound files (gracefully failing without crashing the application).

---

## Features Not Implemented

### 1. Slow Time Ability Sound Effect

- **Status:** ❌ Not Implemented
- **Reason:** While sound effects were added for three of the four RPG abilities (Color Bomb, Color Sync, Clear Bottom 3 Rows), a sound effect for the Slow Time ability was not implemented. This was a deliberate design choice to focus on the more impactful visual abilities that directly affect the game board state. The Slow Time ability primarily affects game speed, which is already visually indicated through the game's animation system.
- **Future Enhancement:** A sound effect could be added for Slow Time activation, potentially a time-slowing or temporal distortion sound effect to enhance the ability's thematic feel.

### 2. Combo Detection System

- **Status:** ❌ Not Implemented
- **Reason:** While initially discussed (specifically for Color Sync + Color Bomb combination), a system to detect and play special combo sound effects when multiple abilities are used in sequence was not implemented. The current implementation plays individual sound effects for each ability independently.
- **Future Enhancement:** A combo detection system could track ability usage patterns and play enhanced sound effects or visual effects when abilities are used in strategic combinations.

---

## New Java Classes

The following Java classes were newly added during the refactoring and extension work. 

### Manager Classes

- `AudioManager.java` - Manages all audio for the game including sound effects and background music, centralizing all audio operations
- `ScoreManager.java` - Manages score persistence and tracking for all game modes, handles loading/saving scores to files and updating UI displays
- `VideoManager.java` - Manages background video and vignette effects for gameplay, centralizing video operations
- `SettingsManager.java` - Manages game settings including key bindings, audio preferences, and other user preferences, persisted to a properties file
- `GameStateManager.java` - Manages game state (pause and game over), centralizing pause/game over logic

### Core Classes

- `TetrisBoard.java` - Refactored implementation of the Board interface, replacing SimpleBoard.java with enhanced functionality including garbage block spawning, color manipulation, and hold piece functionality

### UI Classes (Extracted from GameViewController)

- `GameAnimationManager.java` - Manages all game animations and timeline operations, extracted from GameViewController for better maintainability
- `GameInputHandler.java` - Handles all keyboard input processing for the game, extracted from GameViewController for better maintainability
- `GameModeManager.java` - Manages game mode specific logic and timers (Sprint, Ultra, Survival modes), extracted from GameViewController for better maintainability
- `GameUIRenderer.java` - Handles all UI rendering responsibilities for the game view, extracted from GameViewController for better maintainability
- `GameViewController.java` - Refactored version of GuiController.java, now acts as a coordinator that delegates to extracted manager classes

### UI Panel Classes

- `PausePanel.java` - UI panel component for the pause menu, providing continue, restart, and quit options

### Game Mode Classes

- `GameMode.java` - Enum representing different game modes (Classic, Sprint, Ultra, Survival, RPG) with display names and descriptions

### RPG System Classes

- `RPGModeManager.java` - Manages all RPG mode functionality including level progression, ability management, speed scaling, and garbage block spawning
- `AbilityType.java` - Enum defining RPG ability types (NONE, CLEAR_ROWS, SLOW_TIME, COLOR_BOMB, COLOR_SYNC)
- `AbilityManager.java` - Manages RPG abilities including charges, slots assignment, and ability execution logic
- `RPGConfig.java` - Configuration constants for RPG mode including slow time duration and other RPG-specific settings
- `LevelProgressionCalculator.java` - Calculates RPG level progression based on lines cleared, with different line requirements per level tier
- `RPGSpeedScaler.java` - Calculates game speed based on RPG level, implementing speed tiers that increase difficulty

### Menu System Classes

- `MenuController.java` - Controller for the main menu screen, handles navigation between different game modes and menu options
- `MenuAnimationManager.java` - Manages animations for menu transitions and visual effects
- `MenuBase.java` - Base class for menu functionality, providing common menu operations
- `MenuInterface.java` - Interface defining menu operations and navigation methods
- `MenuMediaManager.java` - Manages media (video and audio) for menu screens
- `MenuNavigationManager.java` - Handles navigation logic between different menu screens and game modes
- `MenuStateManager.java` - Manages menu state and transitions between different menu states
- `ModeSelectionController.java` - Controller for the game mode selection screen, allowing players to choose between Classic, Sprint, Ultra, Survival, and RPG modes
- `SettingsController.java` - Controller for the settings screen, allowing players to configure key bindings, audio settings, and other preferences
- `MainMenu.java` - Main menu class providing menu functionality

### Test Classes

- `TetrisBoardTest.java` - Unit tests for TetrisBoard class functionality
- `BrickRotatorTest.java` - Unit tests for BrickRotator class functionality
- `ScoreTest.java` - Unit tests for Score model class
- `ViewDataTest.java` - Unit tests for ViewData model class
- `ClearRowTest.java` - Unit tests for ClearRow model class
- `MatrixOperationsTest.java` - Unit tests for MatrixOperations utility class
- `AbilityManagerTest.java` - Unit tests for AbilityManager RPG class
- `LevelProgressionCalculatorTest.java` - Unit tests for LevelProgressionCalculator RPG class
- `RPGSpeedScalerTest.java` - Unit tests for RPGSpeedScaler RPG class

### Resource Files Added

#### Audio Files
- `audio/background_music.mp3` - Background music for gameplay
- `audio/game_over.wav` - Sound effect played when the game ends
- `audio/menu_background.mp4` - Background video for the main menu (loops automatically)
- `audio/play_music.mp3` - Background music during gameplay
- `audio/tetris_success.wav` - Sound effect played when lines are cleared successfully
- `combo.mp3` - Sound effect file for Color Bomb ability activation (added in this work)
- `color_sync.wav` - Sound effect file for Color Sync ability activation (added in this work)
- `clear3lines.wav` - Sound effect file for Clear Bottom 3 Rows ability activation (added in this work)

#### Video Files
- `single_player.mp4` - Video file for single player mode
- `single_player2.mp4` - Additional video file for single player mode

#### FXML Layout Files
- `mainMenu.fxml` - FXML layout file defining the main menu screen UI structure
- `modeSelection.fxml` - FXML layout file defining the game mode selection screen UI structure
- `settings.fxml` - FXML layout file defining the settings screen UI structure

#### CSS Style Files
- `menuStyle.css` - Cascading Style Sheet for menu screen styling

#### Documentation Files
- `audio/README.md` - Documentation file explaining audio and video file requirements and usage

---

## Modified Java Classes

### 1. AudioManager.java

**Location:** `src/main/java/com/comp2042/managers/AudioManager.java`

**Purpose:** Centralized audio management for all game sound effects and background music.

**Modifications Made:**

1. **Added New MediaPlayer Fields (Lines 21-23)**
   - `private MediaPlayer comboSound;` - For Color Bomb ability sound effect
   - `private MediaPlayer colorSyncSound;` - For Color Sync ability sound effect
   - `private MediaPlayer clearRowsSound;` - For Clear Bottom 3 Rows ability sound effect
   - **Rationale:** These fields store the MediaPlayer instances for the new ability sound effects, following the same pattern as existing sound effects.

2. **Extended Sound Effect Initialization (Lines 82-106)**
   - Added loading logic for `combo.mp3` (Color Bomb sound) with multiple file path fallbacks
   - Added loading logic for `color_sync.wav` (Color Sync sound) with multiple file path fallbacks
   - Added loading logic for `clear3lines.wav` (Clear Bottom 3 Rows sound) with multiple file path fallbacks
   - **Rationale:** The `loadSoundEffect()` method is called for each new sound effect during initialization, ensuring all sounds are loaded when the AudioManager is created. Multiple file path variations are checked to support different file naming conventions and locations.

3. **Enhanced loadSoundEffect() Switch Statement (Lines 172-180)**
   - Added `case "combo":` to assign comboSound MediaPlayer
   - Added `case "colorSync":` to assign colorSyncSound MediaPlayer
   - Added `case "clearRows":` to assign clearRowsSound MediaPlayer
   - **Rationale:** The switch statement routes loaded Media objects to the appropriate MediaPlayer field based on the sound type string parameter.

4. **Added New Playback Methods (Lines 330-349)**
   - `public void playComboSound()` - Plays Color Bomb ability sound effect
   - `public void playColorSyncSound()` - Plays Color Sync ability sound effect
   - `public void playClearRowsSound()` - Plays Clear Bottom 3 Rows ability sound effect
   - **Rationale:** These public methods provide controlled access to sound effect playback. Each method checks if the sound is loaded and if sound effects are enabled in settings before playing. The `seek(Duration.ZERO)` call ensures the sound plays from the beginning each time.

5. **Updated dispose() Method (Lines 394-405)**
   - Added cleanup for `comboSound` MediaPlayer
   - Added cleanup for `colorSyncSound` MediaPlayer
   - Added cleanup for `clearRowsSound` MediaPlayer
   - **Rationale:** Proper resource cleanup prevents memory leaks. Each MediaPlayer is stopped and disposed when the AudioManager is disposed, following the same pattern as existing sound effects.

**Impact:** These modifications extend the AudioManager's functionality without breaking existing features. All existing sound effects continue to work, and the new ability sound effects integrate seamlessly into the existing audio system architecture.

---

### 2. GameViewController.java

**Location:** `src/main/java/com/comp2042/ui/GameViewController.java`

**Purpose:** Manages the game's user interface, handles user input, and coordinates between game logic and visual presentation.

**Modifications Made:**

1. **Added playComboSound() Method (Lines 1197-1201)**
   ```java
   public void playComboSound() {
       if (audioManager != null) {
           audioManager.playComboSound();
       }
   }
   ```
   - **Rationale:** This wrapper method provides a clean interface for triggering the Color Bomb ability sound effect. It checks for null audioManager to prevent NullPointerException and delegates to the AudioManager's playComboSound() method.

2. **Added playColorSyncSound() Method (Lines 1203-1207)**
   ```java
   public void playColorSyncSound() {
       if (audioManager != null) {
           audioManager.playColorSyncSound();
       }
   }
   ```
   - **Rationale:** Similar to playComboSound(), this method provides a safe way to trigger the Color Sync ability sound effect through the view controller.

3. **Added playClearRowsSound() Method (Lines 1209-1213)**
   ```java
   public void playClearRowsSound() {
       if (audioManager != null) {
           audioManager.playClearRowsSound();
       }
   }
   ```
   - **Rationale:** This method provides a safe interface for triggering the Clear Bottom 3 Rows ability sound effect.

**Impact:** These modifications maintain the architectural separation between game logic (GameController) and UI/audio management (GameViewController/AudioManager). The GameController can trigger sound effects without directly accessing the AudioManager, maintaining proper encapsulation and separation of concerns.

---

### 3. GameController.java

**Location:** `src/main/java/com/comp2042/core/GameController.java`

**Purpose:** Core game logic controller that handles game state, piece movement, ability execution, and coordinates between the game board and view controller.

**Modifications Made:**

1. **Color Bomb Ability Sound Integration (Line 361)**
   - Added `viewGuiController.playComboSound();` call after successful Color Bomb execution
   - **Location:** Inside the `COLOR_BOMB` case of the `useAbility()` method switch statement
   - **Context:** The sound is triggered only when `removed > 0`, meaning blocks were successfully removed
   - **Rationale:** Provides immediate audio feedback when the Color Bomb ability successfully clears blocks. The sound plays after the visual update (`refreshGameBackground`) to ensure synchronization between audio and visual feedback.

2. **Clear Bottom 3 Rows Ability Sound Integration (Line 332)**
   - Added `viewGuiController.playClearRowsSound();` call after Clear Bottom 3 Rows execution
   - **Location:** Inside the `CLEAR_ROWS` case of the `useAbility()` method switch statement
   - **Context:** The sound plays immediately after the ability execution and board refresh
   - **Rationale:** Provides audio confirmation that the bottom three rows have been cleared. This ability always succeeds (unlike Color Bomb which may find no matching blocks), so the sound always plays.

3. **Color Sync Ability Sound Integration (Line 379)**
   - Added `viewGuiController.playColorSyncSound();` call after successful Color Sync execution
   - **Location:** Inside the `COLOR_SYNC` case of the `useAbility()` method switch statement
   - **Context:** The sound is triggered only when `changed > 0`, meaning blocks were successfully converted
   - **Rationale:** Provides audio feedback when blocks are successfully synchronized to a single color. The sound plays after the visual update to maintain synchronization.

**Impact:** These modifications enhance the player experience by providing immediate audio feedback for ability usage. The sound effects are integrated at the appropriate points in the ability execution flow, ensuring they play at the right time and only when abilities are successfully executed. The implementation maintains the existing code structure and doesn't interfere with other game functionality.

**Code Flow:**
1. Player activates ability (keyboard shortcut or UI button)
2. `useAbility(slotIndex)` is called
3. Ability charges are checked
4. Ability is executed (e.g., `executeColorBomb`, `executeColorSync`, `executeClearBottom3Rows`)
5. Board is visually updated (`refreshGameBackground`)
6. **Sound effect is played** (new addition)
7. Charges are decremented
8. Ability slots are updated if charges reach zero

---

## Unexpected Problems

### 1. Audio File Path Resolution

**Problem:** Initially, there was uncertainty about where to place audio files and how the JavaFX Media API would resolve file paths, especially when running from different directories (IDE vs. command line vs. packaged JAR).

**Description:** The JavaFX Media API requires proper resource path resolution. When files are placed in `src/main/resources/`, they need to be accessed via classloader resources, but the exact path format can vary depending on the build and execution context.

**Solution:** Implemented a robust dual-path loading strategy in `AudioManager.loadSoundEffect()`:
1. First attempts to load from classloader resources (works in JAR and IDE)
2. Falls back to direct file system paths (works during development)
3. Tries multiple file naming variations and locations
4. Provides console logging to help diagnose file loading issues

**Location:** `AudioManager.java` lines 118-153

**Outcome:** The solution successfully handles file loading in all execution contexts. Console output clearly indicates which files were found and loaded, making debugging easier.

---

### 2. Sound Effect Timing and Synchronization

**Problem:** Initially considered playing sound effects at different points in the ability execution flow, which could cause audio-visual desynchronization.

**Description:** There was a question about whether to play sounds before or after visual updates, and whether to play sounds even when abilities fail (e.g., Color Bomb finding no matching blocks).

**Solution:** 
- Sounds are played **after** visual updates (`refreshGameBackground`) to ensure synchronization
- Sounds only play when abilities are **successfully executed** (e.g., `removed > 0` or `changed > 0`)
- This provides accurate feedback: if you hear the sound, you know the ability worked

**Location:** `GameController.java` lines 332, 361, 379

**Outcome:** The timing ensures that players receive accurate audio feedback that matches what they see on screen. The conditional playback prevents confusing audio cues when abilities don't have any effect.

---

### 3. MediaPlayer Resource Management

**Problem:** Concern about potential memory leaks if MediaPlayer instances are not properly disposed, especially with multiple sound effects being loaded and potentially played multiple times.

**Description:** JavaFX MediaPlayer instances hold native resources and can cause memory leaks if not properly cleaned up. With three new MediaPlayer instances added, proper disposal becomes critical.

**Solution:** 
- Extended the `dispose()` method to include cleanup for all new MediaPlayer instances
- Each MediaPlayer is stopped before disposal to prevent resource leaks
- The cleanup follows the same pattern as existing sound effects, ensuring consistency

**Location:** `AudioManager.java` lines 394-405

**Outcome:** Proper resource management prevents memory leaks and ensures the application can run for extended periods without performance degradation.

---

### 4. Settings Integration

**Problem:** Ensuring that new sound effects respect user settings for sound effects volume and enable/disable toggle.

**Description:** The existing sound effects check `settings.isSfxEnabled()` and use `settings.getSfxVolume()` for volume control. New sound effects needed to integrate with this system.

**Solution:** All new playback methods (`playComboSound()`, `playColorSyncSound()`, `playClearRowsSound()`) check `settings.isSfxEnabled()` before playing, and the MediaPlayer instances are initialized with `settings.getSfxVolume()`.

**Location:** `AudioManager.java` lines 330-349

**Outcome:** New sound effects seamlessly integrate with the existing settings system. Users can control all sound effects (including ability sounds) through the game's settings menu, maintaining consistency with the rest of the application.

---

## Summary

This maintenance and extension work successfully integrated sound effects for three RPG mode abilities (Color Bomb, Color Sync, and Clear Bottom 3 Rows) into the existing Tetris game codebase. The implementation follows established patterns, maintains code quality and consistency, and enhances the player experience with audio feedback for ability usage.

All modifications were made to existing classes (`AudioManager`, `GameViewController`, `GameController`), demonstrating effective code extension without requiring new class creation. The sound effects system is robust, handles edge cases gracefully, and integrates seamlessly with the existing audio and settings infrastructure.

The work demonstrates understanding of:
- JavaFX Media API for audio playback
- Resource management and file path resolution
- Code architecture and separation of concerns
- Integration with existing systems
- Error handling and graceful degradation

---

## File Structure Reference

### Files Modified in This Work (Adding RPG Ability Sound Effects)

```
CW2025-master/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── comp2042/
│   │   │           ├── core/
│   │   │           │   └── GameController.java          [MODIFIED - Added sound triggers]
│   │   │           ├── managers/
│   │   │           │   └── AudioManager.java           [MODIFIED - Added ability sound loading/playback]
│   │   │           └── ui/
│   │   │               └── GameViewController.java      [MODIFIED - Added sound wrapper methods]
│   │   └── resources/
│   │       ├── combo.mp3                                [NEW - Color Bomb sound effect]
│   │       ├── color_sync.wav                           [NEW - Color Sync sound effect]
│   │       └── clear3lines.wav                         [NEW - Clear Bottom 3 Rows sound effect]
│   └── test/
└── README.md                                            [MODIFIED - Documentation]
```

### Original Codebase Files (Before Any Refactoring)

The original codebase consisted of only these core files:
- `Main.java`
- `Board.java` / `SimpleBoard.java`
- `BrickRotator.java`
- `GuiController.java` (later refactored to `GameViewController.java`)
- `GameController.java`
- `MatrixOperations.java`
- Model classes: `Score.java`, `ViewData.java`, `ClearRow.java`, `DownData.java`, `NextShapeInfo.java`
- Event classes: `EventSource.java`, `EventType.java`, `InputEventListener.java`, `MoveEvent.java`
- UI Panel classes: `GameOverPanel.java`, `NotificationPanel.java`

**Note:** All other files in the codebase (brick classes, RPG classes, menu classes, manager classes like `AudioManager`, `ScoreManager`, `VideoManager`, extracted classes like `GameAnimationManager`, `GameInputHandler`, `GameModeManager`, `GameUIRenderer`, etc.) were created during previous refactoring and extension work, but are not part of this specific assignment focused on adding RPG ability sound effects.

---

**Documentation Prepared By:** Phung Yu Jie 
**Date:** 24/11/2025 
**Course:** COMP2042 Developing Maintainable Software
