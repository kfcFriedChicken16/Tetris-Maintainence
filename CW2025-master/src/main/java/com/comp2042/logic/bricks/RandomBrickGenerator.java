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
    private int totalWeightWithoutI; // Sum of weights excluding I-piece

    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private final Deque<Brick> recentPieces = new ArrayDeque<>(); // Track last 2 pieces to prevent 3+ consecutive I-pieces

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
        // Calculate total weight without I-piece (for when we need to exclude it)
        totalWeightWithoutI = totalWeight - weights.get(0); // I-piece is at index 0
        
        // Initialize next bricks queue
        nextBricks.add(getWeightedRandomBrick());
        nextBricks.add(getWeightedRandomBrick());
    }
    
    /**
     * Check if the last 2 pieces were I-pieces
     */
    private boolean hasTwoConsecutiveI() {
        if (recentPieces.size() < 2) {
            return false;
        }
        Brick[] recent = recentPieces.toArray(new Brick[0]);
        // Check if last 2 pieces are both I-pieces
        return recent[recent.length - 1] instanceof IBrick && 
               recent[recent.length - 2] instanceof IBrick;
    }
    
    /**
     * Get a random brick based on weighted probability
     * I-piece has 20% probability, other pieces ~13.33% each
     * Prevents more than 2 consecutive I-pieces
     */
    private Brick getWeightedRandomBrick() {
        boolean excludeI = hasTwoConsecutiveI();
        int weightToUse = excludeI ? totalWeightWithoutI : totalWeight;
        int random = ThreadLocalRandom.current().nextInt(weightToUse);
        int cumulativeWeight = 0;
        int startIndex = excludeI ? 1 : 0; // Skip I-piece if excluding
        
        for (int i = startIndex; i < brickList.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (random < cumulativeWeight) {
                return brickList.get(i);
            }
        }
        
        // Fallback (should never reach here)
        return brickList.get(excludeI ? 1 : 0);
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(getWeightedRandomBrick());
        }
        Brick consumed = nextBricks.poll();
        // Track consumed piece in recent pieces (keep only last 2)
        recentPieces.add(consumed);
        if (recentPieces.size() > 2) {
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
