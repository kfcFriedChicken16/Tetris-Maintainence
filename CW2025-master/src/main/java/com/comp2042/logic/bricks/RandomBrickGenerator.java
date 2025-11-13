package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;
    private final List<Integer> weights; // Weight for each brick type
    private int totalWeight; // Sum of all weights

    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private final Deque<Brick> recentPieces = new ArrayDeque<>(); // Track last 3 pieces to prevent 4+ consecutive of any type

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        weights = new ArrayList<>();
        
        // I-piece (4-in-a-row) gets weight 6 (20% probability: 6/30 = 20%)
        brickList.add(new IBrick());
        weights.add(6);
        
        // Other pieces get weight 4 each (80% total, ~13.33% each: 4/30 = 13.33%)
        brickList.add(new JBrick());
        weights.add(4);
        brickList.add(new LBrick());
        weights.add(4);
        brickList.add(new OBrick());
        weights.add(4);
        brickList.add(new SBrick());
        weights.add(4);
        brickList.add(new TBrick());
        weights.add(4);
        brickList.add(new ZBrick());
        weights.add(4);
        
        // Calculate total weight
        totalWeight = weights.stream().mapToInt(Integer::intValue).sum();
        
        // Initialize next bricks queue
        nextBricks.add(getWeightedRandomBrick());
        nextBricks.add(getWeightedRandomBrick());
    }
    
    /**
     * Check if a specific brick type has appeared 3 times in a row
     * @param brickType The class of the brick type to check
     * @return true if the last 3 pieces are all of this type
     */
    private boolean hasThreeConsecutive(Class<? extends Brick> brickType) {
        if (recentPieces.size() < 3) {
            return false;
        }
        Brick[] recent = recentPieces.toArray(new Brick[0]);
        int lastIndex = recent.length - 1;
        // Check if last 3 pieces are all of the same type
        return brickType.isInstance(recent[lastIndex]) && 
               brickType.isInstance(recent[lastIndex - 1]) &&
               brickType.isInstance(recent[lastIndex - 2]);
    }
    
    /**
     * Get the index of a brick type in the brickList
     * @param brickType The class of the brick type
     * @return The index, or -1 if not found
     */
    private int getBrickTypeIndex(Class<? extends Brick> brickType) {
        for (int i = 0; i < brickList.size(); i++) {
            if (brickType.isInstance(brickList.get(i))) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Get a random brick based on weighted probability
     * I-piece has 20% probability (weight 6), other pieces ~13.33% each (weight 4)
     * Prevents any block type from appearing 4 times in a row (max 3 consecutive)
     * 
     * When a type is excluded (has appeared 3 times), the remaining types maintain
     * their relative probabilities to each other.
     */
    private Brick getWeightedRandomBrick() {
        // Find which brick type (if any) has appeared 3 times consecutively
        // If found, exclude it from this selection to prevent 4 in a row
        Class<? extends Brick> excludedType = null;
        for (Brick brick : brickList) {
            Class<? extends Brick> brickType = brick.getClass();
            if (hasThreeConsecutive(brickType)) {
                excludedType = brickType;
                break;
            }
        }
        
        // Calculate total weight excluding the blocked type (if any)
        // This maintains the relative probabilities of remaining types
        int weightToUse = totalWeight;
        if (excludedType != null) {
            int excludedIndex = getBrickTypeIndex(excludedType);
            if (excludedIndex >= 0) {
                weightToUse -= weights.get(excludedIndex);
            }
        }
        
        // Safety check: if weight becomes invalid, use total weight
        if (weightToUse <= 0) {
            weightToUse = totalWeight;
            excludedType = null; // Don't exclude anything if weights are invalid
        }
        
        // Generate random number within the valid weight range
        int random = ThreadLocalRandom.current().nextInt(weightToUse);
        int cumulativeWeight = 0;
        
        // Select brick based on weighted probability, skipping excluded type
        for (int i = 0; i < brickList.size(); i++) {
            // Skip excluded type to prevent 4 in a row
            if (excludedType != null && excludedType.isInstance(brickList.get(i))) {
                continue;
            }
            
            // Add this brick's weight to cumulative
            cumulativeWeight += weights.get(i);
            if (random < cumulativeWeight) {
                return brickList.get(i);
            }
        }
        
        // Fallback: return first non-excluded brick (should rarely be needed)
        for (int i = 0; i < brickList.size(); i++) {
            if (excludedType == null || !excludedType.isInstance(brickList.get(i))) {
                return brickList.get(i);
            }
        }
        
        // Ultimate fallback (should never reach here)
        return brickList.get(0);
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(getWeightedRandomBrick());
        }
        Brick consumed = nextBricks.poll();
        // Track consumed piece in recent pieces (keep only last 3 to prevent 4 in a row)
        recentPieces.add(consumed);
        if (recentPieces.size() > 3) {
            recentPieces.poll();
        }
        return consumed;
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

    /**
     * Peek at a future brick in the queue without consuming it
     * @param index 0 = next, 1 = after next, etc.
     * @return The brick at that position, or null if not enough bricks
     */
    public Brick peekNextBrick(int index) {
        // Ensure we have enough bricks
        while (nextBricks.size() < index + 1) {
            nextBricks.add(getWeightedRandomBrick());
        }
        
        // Convert to array to peek at specific index
        Brick[] array = nextBricks.toArray(new Brick[0]);
        return index < array.length ? array[index] : null;
    }
}
