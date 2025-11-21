package com.comp2042.core;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.models.Score;
import com.comp2042.models.ViewData;
import com.comp2042.models.ClearRow;
import com.comp2042.models.NextShapeInfo;
import com.comp2042.utils.MatrixOperations;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TetrisBoard implements Board {

    private final int rows;
    private final int cols;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private int totalLinesCleared = 0; // Track total lines cleared for Sprint mode
    private Brick heldBrick = null; // Currently held piece
    private boolean canHold = true; // Whether we can hold this turn (prevents multiple holds per piece)

    public TetrisBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        currentGameMatrix = new int[rows][cols];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        
        // Try rotation at current position first
        if (!MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY())) {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
        
        // Wall-kick system: Try different positions if rotation fails
        Point[] kickTests = {
            new Point(-1, 0),  // Try left
            new Point(1, 0),   // Try right
            new Point(-2, 0),  // Try further left
            new Point(2, 0),   // Try further right
            new Point(0, -1),  // Try up
            new Point(-1, -1), // Try left-up
            new Point(1, -1),  // Try right-up
        };
        
        for (Point kick : kickTests) {
            int testX = (int) currentOffset.getX() + kick.x;
            int testY = (int) currentOffset.getY() + kick.y;
            
            if (!MatrixOperations.intersect(currentMatrix, nextShape.getShape(), testX, testY)) {
                // Rotation successful with wall-kick
                currentOffset = new Point(testX, testY);
                brickRotator.setCurrentShape(nextShape.getPosition());
                return true;
            }
        }
        
        // All wall-kick attempts failed
        return false;
    }

    @Override
    public int hardDropBrick() {
        int dropDistance = 0;
        // Keep moving down until we can't move anymore
        while (moveBrickDown()) {
            dropDistance++;
        }
        return dropDistance; // Return how far we dropped for bonus points
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        // Fix: Start at top center (X=4 for center of 10-wide board, Y=0 for top)
        currentOffset = new Point(4, 0);
        canHold = true; // Reset hold ability when new piece is created
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }
    
    @Override
    public boolean holdBrick() {
        // Can't hold if already held this turn
        if (!canHold) {
            return false;
        }
        
        // Get current brick
        Brick currentBrick = brickRotator.getBrick();
        
        if (heldBrick == null) {
            // Nothing held yet - store current and get next piece
            heldBrick = currentBrick;
            Brick nextBrick = brickGenerator.getBrick();
            brickRotator.setBrick(nextBrick);
        } else {
            // Swap current with held
            Brick temp = heldBrick;
            heldBrick = currentBrick;
            brickRotator.setBrick(temp);
        }
        
        // Reset position to top center
        currentOffset = new Point(4, 0);
        canHold = false; // Can't hold again until next piece
        
        return true;
    }
    
    /**
     * Get the currently held piece (for display)
     */
    public Brick getHeldBrick() {
        return heldBrick;
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        Point ghostPos = getGhostPiecePosition();
        
        // Get next 4 bricks for preview
        List<int[][]> nextBricksList = new ArrayList<>();
        RandomBrickGenerator randomGen = (RandomBrickGenerator) brickGenerator;
        for (int i = 0; i < 4; i++) {
            Brick next = randomGen.peekNextBrick(i);
            if (next != null) {
                nextBricksList.add(next.getShapeMatrix().get(0));
            }
        }
        
        // Get held piece data (if any)
        int[][] heldBrickData = null;
        if (heldBrick != null) {
            heldBrickData = heldBrick.getShapeMatrix().get(0);
        }
        
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), 
                           brickGenerator.getNextBrick().getShapeMatrix().get(0), nextBricksList, ghostPos, heldBrickData);
    }

    /**
     * Calculate where the current piece would land if hard dropped
     * @return Point representing the ghost piece position (clamped to visible board bounds)
     */
    public Point getGhostPiecePosition() {
        Point ghostPosition = new Point(currentOffset);
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int[][] pieceShape = brickRotator.getCurrentShape();
        
        // Keep moving down until we hit something
        while (true) {
            Point testPosition = new Point(ghostPosition);
            testPosition.translate(0, 1);
            
            if (MatrixOperations.intersect(currentMatrix, pieceShape, 
                    (int) testPosition.getX(), (int) testPosition.getY())) {
                break; // Can't move further down
            }
            
            ghostPosition = testPosition;
        }
        
        // Return the true landing position without clamping
        // The rendering will handle clipping to visible area if needed
        // This ensures ghost always shows where piece will actually land
        return ghostPosition;
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        // Track total lines cleared for Sprint mode
        totalLinesCleared += clearRow.getLinesRemoved();
        return clearRow;

    }
    
    /**
     * Get total lines cleared (for Sprint mode)
     */
    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }
    
    /**
     * Clear the bottom N rows and drop everything above down.
     * @param numRows number of bottom rows to remove
     * @return true if any blocks were removed, false otherwise
     */
    public boolean clearBottomRows(int numRows) {
        if (numRows <= 0) {
            return false;
        }
        
        int rowsToRemove = Math.min(numRows, rows);
        int[][] newMatrix = new int[rows][cols];
        int destinationRow = rows - 1;
        boolean anyBlocksCleared = false;

        for (int sourceRow = rows - 1; sourceRow >= 0; sourceRow--) {
            boolean removeThisRow = (rows - 1 - sourceRow) < rowsToRemove;

            if (removeThisRow) {
                boolean hasBlocks = false;
                for (int col = 0; col < cols; col++) {
                    if (currentGameMatrix[sourceRow][col] != 0) {
                        hasBlocks = true;
                        break;
                    }
                }

                if (hasBlocks) {
                    anyBlocksCleared = true;
                    score.add(100);
                    totalLinesCleared++;
                }
            } else {
                System.arraycopy(currentGameMatrix[sourceRow], 0, newMatrix[destinationRow], 0, cols);
                destinationRow--;
            }
        }

        // Fill remaining rows (if any) at the top with zeros
        while (destinationRow >= 0) {
            Arrays.fill(newMatrix[destinationRow], 0);
            destinationRow--;
        }

        currentGameMatrix = newMatrix;
        System.out.println("✅ Cleared bottom " + rowsToRemove + " rows. Blocks dropped down.");
        return anyBlocksCleared;
    }
    
    @Override
    public int clearColorBlocks(int colorValue) {
        if (colorValue <= 0) {
            return 0;
        }
        int removedCount = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (currentGameMatrix[row][col] == colorValue) {
                    currentGameMatrix[row][col] = 0;
                    removedCount++;
                }
            }
        }
        if (removedCount > 0) {
            collapseColumns();
            score.add(removedCount * 50);
        }
        return removedCount;
    }
    
    @Override
    public int convertAllBlocksToColor(int colorValue) {
        if (colorValue <= 0) {
            return 0;
        }
        int changed = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (currentGameMatrix[row][col] != 0 && currentGameMatrix[row][col] != colorValue) {
                    currentGameMatrix[row][col] = colorValue;
                    changed++;
                }
            }
        }
        return changed;
    }
    
    /**
     * Apply gravity so that columns collapse downward after removals.
     */
    private void collapseColumns() {
        for (int col = 0; col < cols; col++) {
            int writeRow = rows - 1;
            for (int row = rows - 1; row >= 0; row--) {
                int cell = currentGameMatrix[row][col];
                if (cell != 0) {
                    currentGameMatrix[writeRow][col] = cell;
                    if (writeRow != row) {
                        currentGameMatrix[row][col] = 0;
                    }
                    writeRow--;
                }
            }
            for (int row = writeRow; row >= 0; row--) {
                currentGameMatrix[row][col] = 0;
            }
        }
    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[rows][cols];
        score.reset();
        totalLinesCleared = 0; // Reset lines cleared counter
        heldBrick = null; // Clear held piece
        canHold = true; // Reset hold ability
        createNewBrick();
    }
    
    /**
     * Spawn random garbage brick shapes that drop from the top and land on existing blocks.
     * Complete brick shapes (I, O, T, S, Z, J, L) fall down and stack on top of existing blocks.
     * @param numBlocks number of garbage bricks to spawn (each brick is a complete shape)
     * @param level current RPG level (affects spawn pattern)
     * @return number of blocks actually spawned (counts individual blocks in each brick)
     */
    @Override
    public int spawnGarbageBlocks(int numBlocks, int level) {
        if (numBlocks <= 0) {
            return 0;
        }
        
        // SAFETY CHECK: Find the highest occupied row
        int highestOccupiedRow = rows;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (currentGameMatrix[row][col] != 0) {
                    highestOccupiedRow = row;
                    break;
                }
            }
            if (highestOccupiedRow < rows) {
                break;
            }
        }
        
        // If blocks are already too high (above row 10), don't add more to prevent game over
        if (highestOccupiedRow < 10) {
            System.out.println("⚠️ Board is too high (row " + highestOccupiedRow + "), skipping garbage block spawn to prevent game over");
            return 0;
        }
        
        int totalBlocksSpawned = 0;
        Random random = new Random();
        
        // Add complete brick shapes horizontally as rows
        // numBlocks represents how many brick shapes to place horizontally
        for (int brickNum = 0; brickNum < numBlocks; brickNum++) {
            // Generate a random brick shape
            Brick randomBrick = brickGenerator.getBrick();
            List<int[][]> brickShapes = randomBrick.getShapeMatrix();
            
            // Pick a random rotation (some will be horizontal, some vertical)
            int[][] brickShape = brickShapes.get(random.nextInt(brickShapes.size()));
            
            // Convert brick shape to grey (color 8)
            int[][] greyBrickShape = new int[brickShape.length][brickShape[0].length];
            for (int i = 0; i < brickShape.length; i++) {
                for (int j = 0; j < brickShape[i].length; j++) {
                    if (brickShape[i][j] != 0) {
                        greyBrickShape[i][j] = 8; // Grey color for garbage
                    }
                }
            }
            
            // Calculate which row to place this brick (above the highest existing block)
            int targetRow = highestOccupiedRow - 1 - (brickNum / 2); // Place 2 bricks per row
            
            // Make sure we're not placing in the top 4 rows (leave space for new pieces)
            if (targetRow < 4) {
                System.out.println("⚠️ Skipping brick placement - row " + targetRow + " too high");
                continue;
            }
            
            // Find a random X position to place the brick horizontally
            int brickWidth = greyBrickShape[0].length;
            int maxX = cols - brickWidth;
            if (maxX < 0) {
                maxX = 0; // Brick is wider than board, try anyway
            }
            int spawnX = random.nextInt(Math.max(1, maxX + 1));
            
            // Check if brick would fit at this position (horizontally in the row)
            boolean canPlace = true;
            for (int i = 0; i < greyBrickShape.length; i++) {
                for (int j = 0; j < greyBrickShape[i].length; j++) {
                    if (greyBrickShape[i][j] != 0) {
                        int targetCol = spawnX + i;
                        int targetRowForBlock = targetRow + j;
                        if (targetCol >= cols || targetRowForBlock >= rows || targetRowForBlock < 0) {
                            canPlace = false;
                            break;
                        }
                        if (currentGameMatrix[targetRowForBlock][targetCol] != 0) {
                            canPlace = false;
                            break;
                        }
                    }
                }
                if (!canPlace) break;
            }
            
            // Place the brick shape horizontally if it fits
            if (canPlace) {
                for (int i = 0; i < greyBrickShape.length; i++) {
                    for (int j = 0; j < greyBrickShape[i].length; j++) {
                        if (greyBrickShape[i][j] != 0) {
                            int targetCol = spawnX + i;
                            int targetRowForBlock = targetRow + j;
                            currentGameMatrix[targetRowForBlock][targetCol] = 8; // Grey color
                            totalBlocksSpawned++;
                        }
                    }
                }
            }
        }
        
        System.out.println("🗑️ Added " + numBlocks + " complete brick shapes horizontally (" + totalBlocksSpawned + " total blocks) at level " + level);
        return totalBlocksSpawned;
    }
}
